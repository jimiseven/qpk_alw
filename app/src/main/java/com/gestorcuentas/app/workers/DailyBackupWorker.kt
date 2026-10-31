package com.gestorcuentas.app.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gestorcuentas.app.CuentasApp
import com.gestorcuentas.app.utils.AutoBackupManager

/**
 * Worker programado con WorkManager para ejecutar la copia de seguridad diaria a las 21:00.
 * 100% Offline, seguro y respetuoso con la batería de Android.
 */
class DailyBackupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val app = applicationContext as? CuentasApp ?: return Result.failure()
            val repository = app.repository

            AutoBackupManager.ejecutarBackupDiario(
                context = applicationContext,
                repository = repository,
                forzar = true
            )

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
