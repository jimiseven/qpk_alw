package com.gestorcuentas.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Rutas de navegación de la aplicación.
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object Cuentas : Screen("cuentas", "Inventario", Icons.AutoMirrored.Filled.ListAlt)
    object NuevaVenta : Screen("nueva_venta?cuentaId={cuentaId}", "Nueva Venta", Icons.Default.AddShoppingCart) {
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
