package com.gestorcuentas.app.utils

import com.gestorcuentas.app.data.entity.Cuenta
import com.gestorcuentas.app.data.entity.Venta
import org.json.JSONArray
import org.json.JSONObject

/**
 * Utilidades para serialización y deserialización JSON de la base de datos completa.
 * Permite copias de seguridad 100% seguras y portables entre versiones.
 */
object BackupUtils {

    /**
     * Convierte todas las cuentas y ventas a un archivo JSON estructurado.
     */
    fun exportarAJson(cuentas: List<Cuenta>, ventas: List<Venta>): String {
        val root = JSONObject()
        root.put("version", 2)
        root.put("fecha_exportacion", DateUtils.obtenerFechaHoy())
        root.put("total_cuentas", cuentas.size)
        root.put("total_ventas", ventas.size)

        val cuentasArray = JSONArray()
        cuentas.forEach { c ->
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("correo", c.correo)
            obj.put("contrasena_correo", c.contrasenaCorreo)
            obj.put("contrasena_plataforma", c.contrasenaPlataforma)
            obj.put("tipo_cuenta", c.tipoCuenta)
            obj.put("costo_compra", c.costoCompra)
            obj.put("fecha_creacion", c.fechaCreacion)
            obj.put("fecha_fin", c.fechaFin)
            obj.put("estado", c.estado)
            cuentasArray.put(obj)
        }
        root.put("cuentas", cuentasArray)

        val ventasArray = JSONArray()
        ventas.forEach { v ->
            val obj = JSONObject()
            obj.put("id", v.id)
            obj.put("cuenta_id", v.cuentaId)
            obj.put("numero_celular_cliente", v.numeroCelularCliente)
            obj.put("cantidad_perfiles", v.cantidadPerfiles)
            obj.put("precio_venta_por_perfil", v.precioVentaPorPerfil)
            obj.put("duracion_dias", v.duracionDias)
            obj.put("fecha_inicio", v.fechaInicio)
            obj.put("fecha_fin", v.fechaFin)
            ventasArray.put(obj)
        }
        root.put("ventas", ventasArray)

        return root.toString(2)
    }

    /**
     * Parsea un string JSON y extrae las listas de Cuenta y Venta.
     * Totalmente tolerante a versiones antiguas, campos ausentes, espacios y UTF-8 BOM.
     */
    fun parsearBackup(jsonString: String): Pair<List<Cuenta>, List<Venta>> {
        val clean = jsonString.trim().removePrefix("\uFEFF").trim()
        if (clean.isEmpty()) {
            throw IllegalArgumentException("El archivo de respaldo está vacío (0 bytes).")
        }
        if (!clean.startsWith("{")) {
            throw IllegalArgumentException("El archivo no tiene formato JSON válido.")
        }

        val root = JSONObject(clean)
        val cuentasList = mutableListOf<Cuenta>()
        val ventasList = mutableListOf<Venta>()

        if (root.has("cuentas")) {
            val cuentasArray = root.getJSONArray("cuentas")
            for (i in 0 until cuentasArray.length()) {
                val c = cuentasArray.getJSONObject(i)
                cuentasList.add(
                    Cuenta(
                        id = c.optInt("id", 0),
                        correo = c.optString("correo", ""),
                        contrasenaCorreo = c.optString("contrasena_correo", ""),
                        contrasenaPlataforma = c.optString("contrasena_plataforma", ""),
                        tipoCuenta = c.optString("tipo_cuenta", "Otros"),
                        costoCompra = c.optDouble("costo_compra", 0.0),
                        fechaCreacion = c.optString("fecha_creacion", DateUtils.obtenerFechaHoy()),
                        fechaFin = c.optString("fecha_fin", ""),
                        estado = c.optString("estado", "activa")
                    )
                )
            }
        }

        if (root.has("ventas")) {
            val ventasArray = root.getJSONArray("ventas")
            for (i in 0 until ventasArray.length()) {
                val v = ventasArray.getJSONObject(i)
                ventasList.add(
                    Venta(
                        id = v.optInt("id", 0),
                        cuentaId = v.optInt("cuenta_id", 0),
                        numeroCelularCliente = v.optString("numero_celular_cliente", ""),
                        cantidadPerfiles = v.optInt("cantidad_perfiles", 1),
                        precioVentaPorPerfil = v.optDouble("precio_venta_por_perfil", 0.0),
                        duracionDias = v.optInt("duracion_dias", 30),
                        fechaInicio = v.optString("fecha_inicio", DateUtils.obtenerFechaHoy()),
                        fechaFin = v.optString("fecha_fin", "")
                    )
                )
            }
        }

        return Pair(cuentasList, ventasList)
    }
}
