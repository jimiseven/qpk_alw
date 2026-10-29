package com.gestorcuentas.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Rutas de navegación de la aplicación con títulos concisos para la barra de navegación.
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Dashboard : Screen("dashboard", "Inicio", Icons.Default.Home)
    object Cuentas : Screen("cuentas", "Cuentas", Icons.AutoMirrored.Filled.ListAlt)
    object NuevaVenta : Screen("nueva_venta?cuentaId={cuentaId}", "Vender", Icons.Default.AddShoppingCart) {
        fun createRoute(cuentaId: Int? = null): String {
            return if (cuentaId != null) "nueva_venta?cuentaId=$cuentaId" else "nueva_venta"
        }
    }
    object NuevaCuenta : Screen("nueva_cuenta", "Nueva Cuenta")
    object CuentaDetalle : Screen("cuenta_detalle/{cuentaId}", "Detalle de Cuenta") {
        fun createRoute(cuentaId: Int): String = "cuenta_detalle/$cuentaId"
    }
    object HistorialCliente : Screen("historial_cliente", "Clientes", Icons.Default.People)
}
