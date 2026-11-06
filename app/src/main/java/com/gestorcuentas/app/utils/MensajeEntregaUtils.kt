package com.gestorcuentas.app.utils

import android.content.Context

/**
 * Generador del formato personalizable de mensaje para entrega de cuentas a clientes.
 */
object MensajeEntregaUtils {

    const val PLANTILLA_PREDETERMINADA = """Datos para ingresar a la cuenta de {servicio}

Cuenta {servicio} ({dias} dias)
Correo: {correo}
Contrasena: {contrasena}

Fecha ini: {fecha_inicio}
Fecha end: {fecha_fin}

Reglas para el uso de la cuenta:

- No modificar ningun dato de la cuenta, en caso de modificar algun dato de la cuenta, retiro la cuenta del grupo de trabajo y te quitare el acceso, no cubrire la garantia y el tiempo de servicio.
- Evita salirte de la cuenta.
- Referentemente, usa la aplicacion movil en el celular y en computadora navegador Google Chrome NO PESTAnA INCoGNITO 
- Link para pc preferentemente la pagina oficial en tu navegador

Ingresa ahora por favor y te paso los codigos de activacion"""

    /**
     * Construye el mensaje utilizando la plantilla guardada por el usuario en preferencias.
     */
    fun generarMensaje(
        context: Context,
        tipoCuenta: String,
        duracionDias: Int,
        correo: String,
        contrasenaPlataforma: String,
        fechaInicio: String,
        fechaFin: String
    ): String {
        val plantilla = try {
            AppPreferences(context).mensajePlantilla
        } catch (e: Exception) {
            PLANTILLA_PREDETERMINADA
        }
        return aplicarVariables(
            plantilla = plantilla,
            tipoCuenta = tipoCuenta,
            duracionDias = duracionDias,
            correo = correo,
            contrasenaPlataforma = contrasenaPlataforma,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin
        )
    }

    /**
     * Sobrecarga sin context que usa la plantilla predeterminada o la guardada.
     */
    fun generarMensaje(
        tipoCuenta: String,
        duracionDias: Int,
        correo: String,
        contrasenaPlataforma: String,
        fechaInicio: String,
        fechaFin: String
    ): String {
        return aplicarVariables(
            plantilla = PLANTILLA_PREDETERMINADA,
            tipoCuenta = tipoCuenta,
            duracionDias = duracionDias,
            correo = correo,
            contrasenaPlataforma = contrasenaPlataforma,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin
        )
    }

    fun aplicarVariables(
        plantilla: String,
        tipoCuenta: String,
        duracionDias: Int,
        correo: String,
        contrasenaPlataforma: String,
        fechaInicio: String,
        fechaFin: String
    ): String {
        val fechaIniFormateada = DateUtils.formatearFechaLegible(fechaInicio)
        val fechaFinFormateada = DateUtils.formatearFechaLegible(fechaFin)
        val plantillaFinal = if (plantilla.isBlank()) PLANTILLA_PREDETERMINADA else plantilla

        val textoPeriodo = when (duracionDias) {
            30 -> "1 mes"
            60 -> "2 meses"
            90 -> "3 meses"
            180 -> "6 meses"
            365 -> "1 año"
            else -> "$duracionDias días"
        }

        return plantillaFinal
            .replace("{servicio}", tipoCuenta)
            .replace("{periodo}", textoPeriodo)
            .replace("{dias}", duracionDias.toString())
            .replace("{correo}", correo)
            .replace("{contrasena}", contrasenaPlataforma)
            .replace("{fecha_inicio}", fechaIniFormateada)
            .replace("{fecha_fin}", fechaFinFormateada)
    }

    /**
     * Construye el mensaje de confirmación de renovación de servicio para enviar al cliente por WhatsApp.
     */
    fun generarMensajeRenovacion(
        servicio: String,
        correo: String,
        nuevaFechaFin: String,
        diasRenovados: Int
    ): String {
        val fechaFinFormateada = DateUtils.formatearFechaLegible(nuevaFechaFin)
        val periodoTexto = when (diasRenovados) {
            30 -> "1 mes"
            60 -> "2 meses"
            90 -> "3 meses"
            else -> "$diasRenovados días"
        }
        return """¡Hola! Tu servicio de $servicio ha sido renovado con éxito ✅

Detalles de tu renovación:
• Servicio: $servicio ($periodoTexto)
• Cuenta: $correo
• Nueva fecha de vencimiento: $fechaFinFormateada

Tu acceso continúa activo con las mismas credenciales. ¡Muchas gracias por tu preferencia y confianza!"""
    }
}
