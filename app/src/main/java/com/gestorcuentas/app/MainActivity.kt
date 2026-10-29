package com.gestorcuentas.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.gestorcuentas.app.ui.navigation.Screen
import com.gestorcuentas.app.ui.screens.*
import com.gestorcuentas.app.ui.theme.GestorCuentasTheme
import com.gestorcuentas.app.utils.AppPreferences
import com.gestorcuentas.app.utils.AppThemeMode
import com.gestorcuentas.app.viewmodel.InventarioViewModel
import com.gestorcuentas.app.viewmodel.InventarioViewModelFactory

class MainActivity : FragmentActivity() {

    private val viewModel: InventarioViewModel by viewModels {
        val app = application as CuentasApp
        InventarioViewModelFactory(app.repository)
    }

    private lateinit var preferences: AppPreferences
    private val isUnlockedState = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = AppPreferences(this)
        isUnlockedState.value = !preferences.isSecurityEnabled

        setContent {
            var themeMode by remember { mutableStateOf(preferences.themeMode) }
            var isUnlocked by remember { isUnlockedState }
            var mostrarConfiguracionSeguridad by remember { mutableStateOf(false) }

            GestorCuentasTheme(themeMode = themeMode) {
                if (!isUnlocked && preferences.isSecurityEnabled) {
                    LockScreen(
                        preferences = preferences,
                        onUnlocked = { isUnlocked = true }
                    )
                } else {
                    MainAppContent(
                        viewModel = viewModel,
                        onOpenConfiguracion = { mostrarConfiguracionSeguridad = true }
                    )

                    if (mostrarConfiguracionSeguridad) {
                        ConfiguracionSeguridadDialog(
                            preferences = preferences,
                            onDismiss = { mostrarConfiguracionSeguridad = false },
                            onThemeChanged = { newTheme ->
                                themeMode = newTheme
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Si el bloqueo está habilitado, bloquear al salir de la aplicación
        if (::preferences.isInitialized && preferences.isSecurityEnabled) {
            isUnlockedState.value = false
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: InventarioViewModel,
    onOpenConfiguracion: () -> Unit
) {
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
                NavigationBar(
                    tonalElevation = 6.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                screen.icon?.let {
                                    Icon(it, contentDescription = screen.title)
                                }
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    maxLines = 1,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            alwaysShowLabel = true,
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
                    },
                    onOpenConfiguracion = onOpenConfiguracion
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
