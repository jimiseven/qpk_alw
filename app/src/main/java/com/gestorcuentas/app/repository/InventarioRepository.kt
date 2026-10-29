package com.gestorcuentas.app.repository

import com.gestorcuentas.app.data.dao.InventarioDao
import com.gestorcuentas.app.data.entity.Cuenta
import com.gestorcuentas.app.data.entity.CuentaConVentas
import com.gestorcuentas.app.data.entity.Venta
import com.gestorcuentas.app.data.entity.VentaConCuenta
import com.gestorcuentas.app.utils.DateUtils
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que desacopla la fuente de datos Room de los ViewModels.
 * Proporciona una API limpia y reactiva para toda la app.
 */
class InventarioRepository(private val dao: InventarioDao) {

    // Streams reactivos de Cuentas
    val allCuentas: Flow<List<Cuenta>> = dao.getAllCuentas()
    val cuentasActivas: Flow<List<Cuenta>> = dao.getCuentasActivas()
    val cuentasConVentas: Flow<List<CuentaConVentas>> = dao.getCuentasConVentas()

    // Streams reactivos de Ventas
    val allVentas: Flow<List<Venta>> = dao.getAllVentas()
    val allVentasConCuenta: Flow<List<VentaConCuenta>> = dao.getAllVentasConCuenta()

    /**
     * Consulta ventas que vencen desde hoy hasta X días en el futuro (por defecto 7 días).
     */
    fun getVentasPorVencer(diasEnAdelante: Int = 7): Flow<List<VentaConCuenta>> {
        val hoy = DateUtils.obtenerFechaHoy()
        val limite = DateUtils.obtenerFechaFutura(diasEnAdelante)
        return dao.getVentasConCuentaPorVencer(hoy, limite)
    }

    /**
     * Ventas asociadas a una cuenta maestra específica.
     */
    fun getVentasPorCuenta(cuentaId: Int): Flow<List<Venta>> {
        return dao.getVentasPorCuenta(cuentaId)
    }

    /**
     * Detalle de cuenta y sus ventas.
     */
    fun getCuentaConVentas(cuentaId: Int): Flow<CuentaConVentas?> {
        return dao.getCuentaConVentas(cuentaId)
    }

    /**
     * Historial de compras filtrando por número de celular del cliente.
     */
    fun getVentasPorCliente(numeroCelular: String): Flow<List<VentaConCuenta>> {
        return dao.getVentasConCuentaPorCliente(numeroCelular)
    }

    // Operaciones de escritura para Cuentas
    suspend fun insertCuenta(cuenta: Cuenta): Long = dao.insertCuenta(cuenta)
    suspend fun updateCuenta(cuenta: Cuenta) = dao.updateCuenta(cuenta)
    suspend fun deleteCuenta(cuenta: Cuenta) = dao.deleteCuenta(cuenta)

    // Operaciones de escritura para Ventas
    suspend fun insertVenta(venta: Venta): Long = dao.insertVenta(venta)
    suspend fun updateVenta(venta: Venta) = dao.updateVenta(venta)
    suspend fun deleteVenta(venta: Venta) = dao.deleteVenta(venta)
}
