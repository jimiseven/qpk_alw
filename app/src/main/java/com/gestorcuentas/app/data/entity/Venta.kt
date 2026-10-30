package com.gestorcuentas.app.data.entity

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad que representa la venta de perfiles o accesos a un cliente.
 * Se vincula a una Cuenta maestra mediante ForeignKey (con borrado en cascada).
 */
@Immutable
@Entity(
    tableName = "ventas",
    foreignKeys = [
        ForeignKey(
            entity = Cuenta::class,
            parentColumns = ["id"],
            childColumns = ["cuenta_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["cuenta_id"]),
        Index(value = ["numero_celular_cliente"]),
        Index(value = ["fecha_fin"]) // Índice para ordenamiento y consultas por fecha sin full scan
    ]
)
data class Venta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "cuenta_id")
    val cuentaId: Int,

    @ColumnInfo(name = "numero_celular_cliente")
    val numeroCelularCliente: String,

    @ColumnInfo(name = "cantidad_perfiles")
    val cantidadPerfiles: Int = 1,

    @ColumnInfo(name = "precio_venta_por_perfil")
    val precioVentaPorPerfil: Double,

    @ColumnInfo(name = "duracion_dias")
    val duracionDias: Int,

    @ColumnInfo(name = "fecha_inicio")
    val fechaInicio: String, // Formato YYYY-MM-DD

    @ColumnInfo(name = "fecha_fin")
    val fechaFin: String // Calculada sumando duracionDias a fechaInicio
)
