package com.gestorcuentas.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gestorcuentas.app.data.entity.Cuenta
import com.gestorcuentas.app.data.entity.CuentaConVentas
import com.gestorcuentas.app.data.entity.Venta
import com.gestorcuentas.app.data.entity.VentaConCuenta
import com.gestorcuentas.app.repository.InventarioRepository
import com.gestorcuentas.app.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Resumen de indicadores financieros y de operación.
 */
data class ResumenFinanciero(
    val totalIngresos: Double = 0.0,
    val totalCostos: Double = 0.0,
    val gananciaNeta: Double = 0.0,
    val totalCuentasActivas: Int = 0,
    val totalVentasActivas: Int = 0
)

/**
 * ViewModel principal encargado de la lógica de negocio y estado de la UI.
 * Totalmente reactivo mediante StateFlow y Coroutines.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class InventarioViewModel(private val repository: InventarioRepository) : ViewModel() {

    // Lista de cuentas con sus ventas para calcular rentabilidad por cuenta
    val cuentasConVentas: StateFlow<List<CuentaConVentas>> = repository.cuentasConVentas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cuentas activas disponibles para venta
    val cuentasActivas: StateFlow<List<Cuenta>> = repository.cuentasActivas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Ventas por vencer en los próximos 7 días
    val ventasPorVencer: StateFlow<List<VentaConCuenta>> = repository.getVentasPorVencer(7)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Todas las ventas con su respectiva cuenta maestra
    val allVentasConCuenta: StateFlow<List<VentaConCuenta>> = repository.allVentasConCuenta
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Resumen financiero consolidado (Ingresos vs Costos y Utilidad)
    val resumenFinanciero: StateFlow<ResumenFinanciero> = combine(
        repository.allCuentas,
        repository.allVentas
    ) { cuentas, ventas ->
        val totalIngresos = ventas.sumOf { it.cantidadPerfiles * it.precioVentaPorPerfil }
        val totalCostos = cuentas.sumOf { it.costoCompra }
        val gananciaNeta = totalIngresos - totalCostos
        val activas = cuentas.count { it.estado.equals("activa", ignoreCase = true) }
        val hoy = DateUtils.obtenerFechaHoy()
        val ventasVigentes = ventas.count { it.fechaFin >= hoy }

        ResumenFinanciero(
            totalIngresos = totalIngresos,
            totalCostos = totalCostos,
            gananciaNeta = gananciaNeta,
            totalCuentasActivas = activas,
            totalVentasActivas = ventasVigentes
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResumenFinanciero())

    // Estado para búsqueda de historial por celular
    private val _busquedaCelular = MutableStateFlow("")
    val busquedaCelular: StateFlow<String> = _busquedaCelular.asStateFlow()

    val historialCliente: StateFlow<List<VentaConCuenta>> = _busquedaCelular
        .flatMapLatest { celular ->
            if (celular.isBlank()) {
                repository.allVentasConCuenta
            } else {
                repository.getVentasPorCliente(celular)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun actualizarBusquedaCelular(query: String) {
        _busquedaCelular.value = query
    }

    // ==========================================
    // OPERACIONES DE INVENTARIO (CUENTAS)
    // ==========================================

    fun registrarCuenta(
        correo: String,
        contrasenaCorreo: String,
        contrasenaPlataforma: String,
        tipoCuenta: String,
        costoCompra: Double,
        fechaCreacion: String = DateUtils.obtenerFechaHoy(),
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val nuevaCuenta = Cuenta(
                correo = correo.trim(),
                contrasenaCorreo = contrasenaCorreo.trim(),
                contrasenaPlataforma = contrasenaPlataforma.trim(),
                tipoCuenta = tipoCuenta.trim(),
                costoCompra = costoCompra,
                fechaCreacion = fechaCreacion,
                estado = "activa"
            )
            repository.insertCuenta(nuevaCuenta)
            onSuccess()
        }
    }

    fun eliminarCuenta(cuenta: Cuenta) {
        viewModelScope.launch {
            repository.deleteCuenta(cuenta)
        }
    }

    fun cambiarEstadoCuenta(cuenta: Cuenta, nuevoEstado: String) {
        viewModelScope.launch {
            repository.updateCuenta(cuenta.copy(estado = nuevoEstado))
        }
    }

    fun obtenerCuentaConVentas(cuentaId: Int): StateFlow<CuentaConVentas?> {
        return repository.getCuentaConVentas(cuentaId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    // ==========================================
    // OPERACIONES DE VENTAS (CLIENTES)
    // ==========================================

    /**
     * Registra una venta a un cliente calculando automáticamente la fecha_fin
     * sumando la duración en días a la fecha de inicio.
     */
    fun registrarVenta(
        cuentaId: Int,
        numeroCelularCliente: String,
        cantidadPerfiles: Int,
        precioVentaPorPerfil: Double,
        duracionDias: Int,
        fechaInicio: String = DateUtils.obtenerFechaHoy(),
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val fechaFinCalculada = DateUtils.calcularFechaFin(fechaInicio, duracionDias)
            val nuevaVenta = Venta(
                cuentaId = cuentaId,
                numeroCelularCliente = numeroCelularCliente.trim(),
                cantidadPerfiles = cantidadPerfiles,
                precioVentaPorPerfil = precioVentaPorPerfil,
                duracionDias = duracionDias,
                fechaInicio = fechaInicio,
                fechaFin = fechaFinCalculada
            )
            repository.insertVenta(nuevaVenta)
            onSuccess()
        }
    }

    fun eliminarVenta(venta: Venta) {
        viewModelScope.launch {
            repository.deleteVenta(venta)
        }
    }
}

/**
 * Factory para instanciar InventarioViewModel inyectando el repositorio.
 */
class InventarioViewModelFactory(private val repository: InventarioRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventarioViewModel::class.java)) {
            return InventarioViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel Class Desconocida")
    }
}
