package com.gestorcuentas.app.utils

/**
 * Generador del formato estandarizado de mensaje para entrega de cuentas a clientes.
 */
object MensajeEntregaUtils {

    /**
     * Construye la plantilla exacta solicitada con los datos de la cuenta y fechas calculadas.
     */
    fun generarMensaje(
        tipoCuenta: String,
        duracionDias: Int,
        correo: String,
        contrasenaPlataforma: String,
        fechaInicio: String,
        fechaFin: String
    ): String {
        val fechaIniFormateada = DateUtils.formatearFechaLegible(fechaInicio)
        val fechaFinFormateada = DateUtils.formatearFechaLegible(fechaFin)

        return """
Datos para ingresar a la cuenta de $tipoCuenta

Cuenta $tipoCuenta ($duracionDias dias)
Correo: $correo
Contrasena: $contrasenaPlataforma

Fecha ini: $fechaIniFormateada
Fecha end: $fechaFinFormateada

Reglas para el uso de la cuenta:

- No modificar ningun dato de la cuenta, en caso de modificar algun dato de la cuenta, retiro la cuenta del grupo de trabajo y te quitare el acceso, no cubrire la garantia y el tiempo de servicio.
- Evita salirte de la cuenta.
- Referentemente, usa la aplicacion movil en el celular y en computadora navegador Google Chrome NO PESTAnA INCoGNITO 
- Link para pc preferentemente la pagina oficial en tu navegador

Ingresa ahora por favor y te paso los codigos de activacion
        """.trimIndent()
    }
}
