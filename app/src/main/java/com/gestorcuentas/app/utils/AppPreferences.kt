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
}
