package com.gestorcuentas.app.utils

import android.content.Context
import android.content.SharedPreferences

enum class AppThemeMode {
    SISTEMA,
    CLARO,
    OSCURO
}

/**
 * Gestión ultra-ligera de preferencias locales (Tema Claro/Oscuro y Bloqueo por PIN).
 * 100% Offline, sin consumo de batería en segundo plano.
 */
class AppPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("gestor_cuentas_prefs", Context.MODE_PRIVATE)

    var themeMode: AppThemeMode
        get() {
            val name = prefs.getString("theme_mode", AppThemeMode.SISTEMA.name) ?: AppThemeMode.SISTEMA.name
            return try { AppThemeMode.valueOf(name) } catch (e: Exception) { AppThemeMode.SISTEMA }
        }
        set(value) {
            prefs.edit().putString("theme_mode", value.name).apply()
        }

    var isSecurityEnabled: Boolean
        get() = prefs.getBoolean("security_enabled", false)
        set(value) = prefs.edit().putBoolean("security_enabled", value).apply()

    var securityPin: String
        get() = prefs.getString("security_pin", "") ?: ""
        set(value) = prefs.edit().putString("security_pin", value).apply()

    var mensajePlantilla: String
        get() = prefs.getString("mensaje_plantilla", MensajeEntregaUtils.PLANTILLA_PREDETERMINADA)
            ?: MensajeEntregaUtils.PLANTILLA_PREDETERMINADA
        set(value) = prefs.edit().putString("mensaje_plantilla", value).apply()

    var isAutoBackupEnabled: Boolean
        get() = prefs.getBoolean("auto_backup_enabled", true)
        set(value) = prefs.edit().putBoolean("auto_backup_enabled", value).apply()

    var lastAutoBackupDate: String
        get() = prefs.getString("last_auto_backup_date", "") ?: ""
        set(value) = prefs.edit().putString("last_auto_backup_date", value).apply()

    var backupRetentionDays: Int
        get() = prefs.getInt("backup_retention_days", 14)
        set(value) = prefs.edit().putInt("backup_retention_days", value).apply()
}
