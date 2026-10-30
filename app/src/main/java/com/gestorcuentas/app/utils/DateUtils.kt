package com.gestorcuentas.app.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * Utilidades para cálculo de fechas y lógica de vencimientos.
 * Compatible nativamente con Android 8+ (API 26+) y Android 13 (API 33).
 */
object DateUtils {

    private val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)

    /**
     * Retorna la fecha actual en formato YYYY-MM-DD.
     */
    fun obtenerFechaHoy(): String {
        return LocalDate.now().format(FORMATTER)
    }

    /**
     * Calcula la fecha límite sumando días a la fecha de hoy.
     */
    fun obtenerFechaFutura(dias: Int): String {
        return LocalDate.now().plusDays(dias.toLong()).format(FORMATTER)
    }

    /**
     * Calcula automáticamente la fecha_fin sumando duracionDias a la fechaInicio (YYYY-MM-DD).
     */
    fun calcularFechaFin(fechaInicio: String, duracionDias: Int): String {
        return try {
            val inicio = LocalDate.parse(fechaInicio, FORMATTER)
            val fin = inicio.plusDays(duracionDias.toLong())
            fin.format(FORMATTER)
        } catch (e: Exception) {
            // Fallback en caso de error de formato
            obtenerFechaHoy()
        }
    }

    /**
     * Convierte milisegundos UTC (de DatePicker) a fecha ISO (YYYY-MM-DD).
     */
    fun millisToFechaIso(millis: Long): String {
        return try {
            Instant.ofEpochMilli(millis)
                .atZone(ZoneId.of("UTC"))
                .toLocalDate()
                .format(FORMATTER)
        } catch (e: Exception) {
            obtenerFechaHoy()
        }
    }

    /**
     * Convierte una fecha ISO (YYYY-MM-DD) a milisegundos UTC (para inicializar DatePicker).
     */
    fun fechaIsoToMillis(fechaIso: String): Long {
        return try {
            LocalDate.parse(fechaIso, FORMATTER)
                .atStartOfDay(ZoneId.of("UTC"))
                .toInstant()
                .toEpochMilli()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    /**
     * Formatea una fecha YYYY-MM-DD a formato legible (ej: 28 oct 2026).
     */
    fun formatearFechaLegible(fechaIso: String): String {
        return try {
            val date = LocalDate.parse(fechaIso, FORMATTER)
            val monthNames = arrayOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")
            "${date.dayOfMonth} ${monthNames[date.monthValue - 1]} ${date.year}"
        } catch (e: Exception) {
            fechaIso
        }
    }

    /**
     * Retorna la diferencia de días entre la fecha de referencia y la fecha_fin.
     * Permite pasar un `hoy` precalculado para evitar llamar a `LocalDate.now()` en cada frame de scroll.
     */
    fun calcularDiasRestantes(fechaFin: String, hoy: LocalDate = LocalDate.now()): Long {
        return try {
            val fin = LocalDate.parse(fechaFin, FORMATTER)
            ChronoUnit.DAYS.between(hoy, fin)
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Formatea un valor numérico como moneda legible (ej: $ 15.50).
     */
    fun formatearMoneda(monto: Double): String {
        return String.format(Locale.US, "$ %.2f", monto)
    }

    /**
     * Clasificación visual del estado de un perfil/venta.
     */
    enum class EstadoVencimiento {
        VENCIDA,
        VENCE_HOY,
        POR_VENCER, // 1 a 3 días
        ACTIVA
    }

    fun obtenerEstadoVencimiento(fechaFin: String, hoy: LocalDate = LocalDate.now()): EstadoVencimiento {
        val dias = calcularDiasRestantes(fechaFin, hoy)
        return when {
            dias < 0 -> EstadoVencimiento.VENCIDA
            dias == 0L -> EstadoVencimiento.VENCE_HOY
            dias in 1..3 -> EstadoVencimiento.POR_VENCER
            else -> EstadoVencimiento.ACTIVA
        }
    }
}
