package com.gestorcuentas.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gestorcuentas.app.data.entity.Cuenta
import com.gestorcuentas.app.data.entity.CuentaConVentas
import com.gestorcuentas.app.data.entity.Venta
import com.gestorcuentas.app.data.entity.VentaConCuenta
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para el acceso y manipulación de datos en SQLite vía Room.
 * Maneja operaciones reactivas con Flow y operaciones asíncronas con Coroutines (suspend).
 */
@Dao
interface InventarioDao {

    // ==========================================
    // OPERACIONES CRUD PARA CUENTA
    // ==========================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCuenta(cuenta: Cuenta): Long

    @Update
    suspend fun updateCuenta(cuenta: Cuenta)

    @Delete
    suspend fun deleteCuenta(cuenta: Cuenta)

    @Query("SELECT * FROM cuentas ORDER BY id DESC")
    fun getAllCuentas(): Flow<List<Cuenta>>

    @Query("SELECT * FROM cuentas WHERE id = :id LIMIT 1")
    fun getCuentaById(id: Int): Flow<Cuenta?>

    @Query("SELECT * FROM cuentas WHERE estado = 'activa' ORDER BY tipo_cuenta ASC")
    fun getCuentasActivas(): Flow<List<Cuenta>>

    @Query("SELECT * FROM cuentas")
    suspend fun getAllCuentasSnapshot(): List<Cuenta>

    @Query("SELECT * FROM ventas")
    suspend fun getAllVentasSnapshot(): List<Venta>

    @Query("DELETE FROM ventas")
    suspend fun deleteAllVentas()

    @Query("DELETE FROM cuentas")
    suspend fun deleteAllCuentas()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCuentas(cuentas: List<Cuenta>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVentas(ventas: List<Venta>)

    @Transaction
    suspend fun restaurarBaseDeDatos(cuentas: List<Cuenta>, ventas: List<Venta>) {
        deleteAllVentas()
        deleteAllCuentas()
        insertCuentas(cuentas)
        insertVentas(ventas)
    }

    // ==========================================
    // OPERACIONES CRUD PARA VENTA
    // ==========================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVenta(venta: Venta): Long

    @Update
    suspend fun updateVenta(venta: Venta)

    @Delete
    suspend fun deleteVenta(venta: Venta)

    @Query("SELECT * FROM ventas ORDER BY fecha_fin ASC")
    fun getAllVentas(): Flow<List<Venta>>

    @Query("SELECT * FROM ventas WHERE id = :id LIMIT 1")
    fun getVentaById(id: Int): Flow<Venta?>

    // ==========================================
    // QUERIES ESPECÍFICAS DE LÓGICA DE NEGOCIO
    // ==========================================

    /**
     * 1. Obtener todas las ventas asociadas a un cuenta_id específico.
     */
    @Query("SELECT * FROM ventas WHERE cuenta_id = :cuentaId ORDER BY fecha_inicio DESC")
    fun getVentasPorCuenta(cuentaId: Int): Flow<List<Venta>>

    /**
     * 2. Buscar el historial de compras de un cliente filtrando por su numero_celular_cliente.
     */
    @Query("SELECT * FROM ventas WHERE numero_celular_cliente LIKE '%' || :numeroCelular || '%' ORDER BY fecha_inicio DESC")
    fun getVentasPorCliente(numeroCelular: String): Flow<List<Venta>>

    /**
     * 3. Obtener perfiles/ventas que vencen hoy o en los próximos X días.
     */
    @Query("SELECT * FROM ventas WHERE fecha_fin >= :fechaHoy AND fecha_fin <= :fechaLimite ORDER BY fecha_fin ASC")
    fun getVentasPorVencer(fechaHoy: String, fechaLimite: String): Flow<List<Venta>>

    // ==========================================
    // QUERIES RELACIONALES OPTIMIZADAS (@Transaction)
    // ==========================================

    /**
     * Obtiene todas las cuentas junto a sus ventas registradas (para calcular ROI y estado de ganancia).
     */
    @Transaction
    @Query("SELECT * FROM cuentas ORDER BY id DESC")
    fun getCuentasConVentas(): Flow<List<CuentaConVentas>>

    /**
     * Obtiene el detalle de una cuenta con sus perfiles vendidos.
     */
    @Transaction
    @Query("SELECT * FROM cuentas WHERE id = :cuentaId LIMIT 1")
    fun getCuentaConVentas(cuentaId: Int): Flow<CuentaConVentas?>

    /**
     * Obtiene todas las ventas incluyendo los datos de su cuenta maestra.
     */
    @Transaction
    @Query("SELECT * FROM ventas ORDER BY fecha_fin ASC")
    fun getAllVentasConCuenta(): Flow<List<VentaConCuenta>>

    /**
     * Obtiene las ventas por vencer junto con los datos de la cuenta asociada.
     */
    @Transaction
    @Query("SELECT * FROM ventas WHERE fecha_fin >= :fechaHoy AND fecha_fin <= :fechaLimite ORDER BY fecha_fin ASC")
    fun getVentasConCuentaPorVencer(fechaHoy: String, fechaLimite: String): Flow<List<VentaConCuenta>>

    /**
     * Historial de compras de un cliente con el detalle de las cuentas vinculadas.
     */
    @Transaction
    @Query("SELECT * FROM ventas WHERE numero_celular_cliente LIKE '%' || :numeroCelular || '%' ORDER BY fecha_inicio DESC")
    fun getVentasConCuentaPorCliente(numeroCelular: String): Flow<List<VentaConCuenta>>
}
