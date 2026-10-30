package com.gestorcuentas.app.data.entity

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad que representa una Cuenta Maestra adquirida a un proveedor.
 * Contiene credenciales, tipo de servicio, costo de compra y estado actual.
 */
@Immutable
@Entity(
    tableName = "cuentas",
    indices = [
        Index(value = ["estado"]) // Índice para filtros instantáneos de inventario activo
    ]
)
data class Cuenta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val correo: String,

    @ColumnInfo(name = "contrasena_correo")
    val contrasenaCorreo: String,

    @ColumnInfo(name = "contrasena_plataforma")
    val contrasenaPlataforma: String,

    @ColumnInfo(name = "tipo_cuenta")
    val tipoCuenta: String, // Ej: "ChatGPT", "Netflix", "Gemini", "Spotify"

    @ColumnInfo(name = "costo_compra")
    val costoCompra: Double,

    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: String, // Formato YYYY-MM-DD

    @ColumnInfo(name = "fecha_fin", defaultValue = "''")
    val fechaFin: String = "", // Fecha de vencimiento de la cuenta con el proveedor

    val estado: String = "activa" // 'activa', 'inhabilitada'
)
