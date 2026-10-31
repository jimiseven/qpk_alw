package com.gestorcuentas.app.utils

import android.content.Context
import androidx.work.*
import com.gestorcuentas.app.repository.InventarioRepository
import com.gestorcuentas.app.workers.DailyBackupWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * Información de un archivo de respaldo local.
 */
data class BackupItemInfo(
    val file: File,
    val nombre: String,
    val fecha: String,
    val tamanoBytes: Long,
    val totalCuentas: Int = 0,
    val totalVentas: Int = 0
)

/**
 * Gestor del sistema de copias de seguridad automáticas diarias a las 21:00.
 * Diseñado con persistencia atómica, rotación inteligente y mantenimiento SQLite.
 */
object AutoBackupManager {

    const val WORK_NAME = "DailyAutoBackupWorker_2100"

    /**
     * Retorna el directorio accesible para copias de seguridad.
     * Ubicación: Android/data/com.gestorcuentas.app/files/backups/ (accesible por USB/PC).
     */
    fun getBackupsDirectory(context: Context): File {
        val dir = context.getExternalFilesDir("backups") ?: File(context.filesDir, "backups")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Genera una copia de seguridad atómica con los datos actuales.
     */
    suspend fun ejecutarBackupDiario(
        context: Context,
        repository: InventarioRepository,
        forzar: Boolean = false
    ): File? = withContext(Dispatchers.IO) {
        val prefs = AppPreferences(context)
        if (!forzar && !prefs.isAutoBackupEnabled) {
            return@withContext null
        }

        val hoyIso = DateUtils.obtenerFechaHoy()
        if (!forzar && prefs.lastAutoBackupDate == hoyIso) {
            // Ya se realizó el respaldo de hoy
            return@withContext null
        }

        try {
            val cuentas = repository.getAllCuentasSnapshot()
            val ventas = repository.getAllVentasSnapshot()
            val jsonString = BackupUtils.exportarAJson(cuentas, ventas)

            val dir = getBackupsDirectory(context)
            val fileName = "backup_auto_${hoyIso}_2100.json"
            val tempFile = File(dir, "$fileName.tmp")
            val targetFile = File(dir, fileName)

            // 1. Escritura atómica (evita archivos corruptos de 0 bytes)
            FileOutputStream(tempFile).use { output ->
                output.write(jsonString.toByteArray(Charsets.UTF_8))
                output.flush()
                output.fd.sync()
            }

            if (targetFile.exists()) {
                targetFile.delete()
            }
            tempFile.renameTo(targetFile)

            // 2. Marcar fecha de último respaldo exitoso
            prefs.lastAutoBackupDate = hoyIso

            // 3. Rotación inteligente: Mantener últimos N días (por defecto 14)
            rotarBackups(context, prefs.backupRetentionDays)

            // 4. Mantenimiento preventivo de SQLite (PRAGMA optimize)
            repository.optimizeDatabase()

            targetFile
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Elimina respaldos automáticos más antiguos que los días de retención configurados.
     */
    fun rotarBackups(context: Context, diasRetencion: Int = 14) {
        try {
            val dir = getBackupsDirectory(context)
            val archivos = dir.listFiles { f -> f.isFile && f.name.startsWith("backup_auto_") && f.name.endsWith(".json") }
                ?: return

            val fechaLimite = LocalDate.now().minusDays(diasRetencion.toLong())

            for (archivo in archivos) {
                // Nombre: backup_auto_YYYY-MM-DD_2100.json
                val partes = archivo.name.removePrefix("backup_auto_").split("_")
                if (partes.isNotEmpty()) {
                    val fechaStr = partes[0]
                    try {
                        val fechaArchivo = LocalDate.parse(fechaStr, DateTimeFormatter.ISO_LOCAL_DATE)
                        if (fechaArchivo.isBefore(fechaLimite)) {
                            archivo.delete()
                        }
                    } catch (ignored: Exception) {
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    /**
     * Lista todos los archivos de respaldo disponibles ordenados por fecha descendente.
     */
    fun listarBackups(context: Context): List<BackupItemInfo> {
        val dir = getBackupsDirectory(context)
        val archivos = dir.listFiles { f -> f.isFile && f.name.endsWith(".json") } ?: return emptyList()

        return archivos.sortedByDescending { it.lastModified() }.map { file ->
            var cuentasCount = 0
            var ventasCount = 0
            var fechaStr = DateUtils.obtenerFechaHoy()

            try {
                // Lectura rápida de cabecera sin parsear todo el archivo si es muy grande
                val lineas = file.bufferedReader().useLines { lines -> lines.take(6).toList().joinToString("\n") }
                if (lineas.contains("total_cuentas")) {
                    val root = org.json.JSONObject(file.readText())
                    cuentasCount = root.optInt("total_cuentas", 0)
                    ventasCount = root.optInt("total_ventas", 0)
                    fechaStr = root.optString("fecha_exportacion", fechaStr)
                }
            } catch (e: Exception) {
            }

            BackupItemInfo(
                file = file,
                nombre = file.name,
                fecha = fechaStr,
                tamanoBytes = file.length(),
                totalCuentas = cuentasCount,
                totalVentas = ventasCount
            )
        }
    }

    /**
     * Programa el trabajo periódico diario a las 21:00 con WorkManager.
     */
    fun programarWorkManager(context: Context) {
        val prefs = AppPreferences(context)
        if (!prefs.isAutoBackupEnabled) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            return
        }

        val now = LocalDateTime.now()
        var target = now.withHour(21).withMinute(0).withSecond(0).withNano(0)
        if (now.isAfter(target)) {
            target = target.plusDays(1)
        }
        val initialDelayMinutes = Duration.between(now, target).toMinutes()

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .build()

        val backupWorkRequest = PeriodicWorkRequestBuilder<DailyBackupWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            backupWorkRequest
        )
    }

    /**
     * Verificación pasiva al abrir la app: si faltó el respaldo de ayer o de hoy (después de las 21:00), lo genera.
     */
    suspend fun verificarRespaldoPasivo(context: Context, repository: InventarioRepository) {
        val prefs = AppPreferences(context)
        if (!prefs.isAutoBackupEnabled) return

        val now = LocalDateTime.now()
        val hoyIso = DateUtils.obtenerFechaHoy()

        // Si ya son más de las 21:00 y hoy no se respaldó, o si nunca se ha respaldado
        val necesitaBackupHoy = now.hour >= 21 && prefs.lastAutoBackupDate != hoyIso
        val nuncaRespaldado = prefs.lastAutoBackupDate.isBlank()

        if (necesitaBackupHoy || nuncaRespaldado) {
            ejecutarBackupDiario(context, repository, forzar = true)
        }
    }
}
