package com.gestorcuentas.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Rutas de navegación principales de la aplicación optimizadas para la Barra Inferior (Thumb Zone).
 * Tres destinos claros y accesibles con el pulgar.
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Dashboard : Screen("dashboard", "Inicio", Icons.Default.Home)
    object Cuentas : Screen("cuentas", "Inventario", Icons.AutoMirrored.Filled.ListAlt)
    object HistorialCliente : Screen("historial_cliente", "Historial", Icons.Default.People)

    object CuentaDetalle : Screen("cuenta_detalle/{cuentaId}", "Detalle de Cuenta") {
        fun createRoute(cuentaId: Int): String = "cuenta_detalle/$cuentaId"
    }
}
