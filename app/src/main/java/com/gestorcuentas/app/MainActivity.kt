package com.gestorcuentas.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.gestorcuentas.app.ui.navigation.Screen
import com.gestorcuentas.app.ui.screens.*
import com.gestorcuentas.app.ui.theme.GestorCuentasTheme
import com.gestorcuentas.app.viewmodel.InventarioViewModel
import com.gestorcuentas.app.viewmodel.InventarioViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: InventarioViewModel by viewModels {
        val app = application as CuentasApp
        InventarioViewModelFactory(app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GestorCuentasTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: InventarioViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Ítems de la barra inferior de navegación
    val bottomNavItems = listOf(
        Screen.Dashboard,
        Screen.Cuentas,
        Screen.NuevaVenta,
        Screen.HistorialCliente
    )

    // Mostrar barra inferior en pantallas principales
    val shouldShowBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Cuentas.route,
        Screen.NuevaVenta.route,
        Screen.HistorialCliente.route
    )

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                screen.icon?.let {
                                    Icon(it, contentDescription = screen.title)
                                }
                            },
                            label = { Text(screen.title) },
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Dashboard
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onCuentaClick = { cuentaId ->
                        navController.navigate(Screen.CuentaDetalle.createRoute(cuentaId))
                    },
                    onNavigateToNuevaVenta = {
                        navController.navigate(Screen.NuevaVenta.createRoute())
                    },
                    onNavigateToNuevaCuenta = {
                        navController.navigate(Screen.NuevaCuenta.route)
                    }
                )
            }

            // 2. Inventario de Cuentas
            composable(Screen.Cuentas.route) {
                CuentasScreen(
                    viewModel = viewModel,
                    onCuentaClick = { cuentaId ->
                        navController.navigate(Screen.CuentaDetalle.createRoute(cuentaId))
                    },
                    onNavigateToNuevaCuenta = {
                        navController.navigate(Screen.NuevaCuenta.route)
                    }
                )
            }

            // 3. Detalle de Cuenta
            composable(
                route = Screen.CuentaDetalle.route,
                arguments = listOf(
                    navArgument("cuentaId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val cuentaId = backStackEntry.arguments?.getInt("cuentaId") ?: 0
                CuentaDetalleScreen(
                    cuentaId = cuentaId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToNuevaVenta = { cId ->
                        navController.navigate(Screen.NuevaVenta.createRoute(cId))
                    }
                )
            }

            // 4. Nueva Venta
            composable(
                route = Screen.NuevaVenta.route,
                arguments = listOf(
                    navArgument("cuentaId") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { backStackEntry ->
                val cuentaIdArg = backStackEntry.arguments?.getInt("cuentaId")
                val cuentaId = if (cuentaIdArg != null && cuentaIdArg != -1) cuentaIdArg else null
                NuevaVentaScreen(
                    initialCuentaId = cuentaId,
                    viewModel = viewModel,
                    onNavigateBack = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Screen.Dashboard.route)
                        }
                    }
                )
            }

            // 5. Nueva Cuenta
            composable(Screen.NuevaCuenta.route) {
                NuevaCuentaScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 6. Historial de Clientes
            composable(Screen.HistorialCliente.route) {
                HistorialClienteScreen(viewModel = viewModel)
            }
        }
    }
}
