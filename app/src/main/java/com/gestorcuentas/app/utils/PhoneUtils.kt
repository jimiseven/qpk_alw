package com.gestorcuentas.app.utils

/**
 * Utilidades para normalización y búsqueda inteligente de números telefónicos (WhatsApp).
 * Permite buscar números pegados con prefijo internacional (+591, etc.), espacios o guiones.
 */
object PhoneUtils {

    /**
     * Extrae los dígitos principales eliminando código de país común o tomando los dígitos nacionales.
     */
    fun extraerDigitosSignificativos(telefono: String): String {
        val soloDigitos = telefono.filter { it.isDigit() }
        return when {
            soloDigitos.startsWith("591") && soloDigitos.length >= 11 -> soloDigitos.removePrefix("591")
            soloDigitos.startsWith("58") && soloDigitos.length >= 12 -> soloDigitos.removePrefix("58")
            soloDigitos.startsWith("57") && soloDigitos.length >= 12 -> soloDigitos.removePrefix("57")
            soloDigitos.startsWith("54") && soloDigitos.length >= 12 -> soloDigitos.removePrefix("54")
            soloDigitos.startsWith("51") && soloDigitos.length >= 11 -> soloDigitos.removePrefix("51")
            soloDigitos.startsWith("52") && soloDigitos.length >= 12 -> soloDigitos.removePrefix("52")
            soloDigitos.length > 8 -> soloDigitos.takeLast(8)
            else -> soloDigitos
        }
    }

    /**
     * Compara si dos números coinciden, tolerando prefijos internacionales, espacios y guiones.
     */
    fun coincideNumero(telefonoGuardado: String, consultaBusqueda: String): Boolean {
        if (consultaBusqueda.isBlank()) return true
        if (telefonoGuardado.contains(consultaBusqueda, ignoreCase = true)) return true

        val digitosConsulta = consultaBusqueda.filter { it.isDigit() }
        val digitosGuardados = telefonoGuardado.filter { it.isDigit() }

        if (digitosConsulta.isNotBlank() && digitosGuardados.contains(digitosConsulta)) return true

        val claveConsulta = extraerDigitosSignificativos(consultaBusqueda)
        val claveGuardada = extraerDigitosSignificativos(telefonoGuardado)

        if (claveConsulta.isNotBlank() && (claveGuardada.contains(claveConsulta) || claveConsulta.contains(claveGuardada))) {
            return true
        }

        return false
    }
}
