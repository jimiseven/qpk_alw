package com.gestorcuentas.app.data.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Representa una cuenta maestra junto a todas las ventas de perfiles asociadas.
 * Facilita el cálculo automático de rentabilidad (Ingresos vs Costo).
 */
data class CuentaConVentas(
    @Embedded val cuenta: Cuenta,
    @Relation(
        parentColumn = "id",
        entityColumn = "cuenta_id"
    )
    val ventas: List<Venta>
) {
    /** Suma total recaudada por la venta de perfiles de esta cuenta */
    val totalIngresos: Double
        get() = ventas.sumOf { it.cantidadPerfiles * it.precioVentaPorPerfil }

    /** Ganancia neta (positiva = beneficio, negativa = déficit / recuperando inversión) */
    val gananciaNeta: Double
        get() = totalIngresos - cuenta.costoCompra

    /** Indica si las ventas ya superaron o igualaron el costo de compra */
    val esRentable: Boolean
        get() = totalIngresos >= cuenta.costoCompra

    /** Total acumulado de perfiles vendidos de esta cuenta */
    val perfilesVendidosTotales: Int
        get() = ventas.sumOf { it.cantidadPerfiles }
}

/**
 * Representa una venta junto a los datos de la cuenta maestra asignada.
 * Ideal para el Dashboard y listados de vencimiento.
 */
data class VentaConCuenta(
    @Embedded val venta: Venta,
    @Relation(
        parentColumn = "cuenta_id",
        entityColumn = "id"
    )
    val cuenta: Cuenta?
) {
    val totalVenta: Double
        get() = venta.cantidadPerfiles * venta.precioVentaPorPerfil
}
