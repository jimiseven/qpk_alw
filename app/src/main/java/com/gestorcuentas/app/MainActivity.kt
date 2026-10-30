package com.gestorcuentas.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.gestorcuentas.app.ui.navigation.Screen
import com.gestorcuentas.app.ui.screens.*
import com.gestorcuentas.app.ui.theme.GestorCuentasTheme
import com.gestorcuentas.app.utils.AppPreferences
import com.gestorcuentas.app.viewmodel.InventarioViewModel
import com.gestorcuentas.app.viewmodel.InventarioViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: InventarioViewModel by viewModels {
        val app = application as CuentasApp
        InventarioViewModelFactory(app.repository)
    }

    private lateinit var preferences: AppPreferences
    // Estado de desbloqueo: solo se solicita el PIN la primera vez al abrir la aplicación
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
}

@Composable
fun MainAppContent(
    viewModel: InventarioViewModel,
    onOpenConfiguracion: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Navegación Ergonómica: 3 pestañas principales en la Zona del Pulgar (Thumb Zone)
    val bottomNavItems = listOf(
        Screen.Dashboard,
        Screen.Cuentas,
        Screen.HistorialCliente
    )

    val shouldShowBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Cuentas.route,
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
            // 1. Dashboard (Inicio)
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onCuentaClick = { cuentaId ->
                        navController.navigate(Screen.CuentaDetalle.createRoute(cuentaId))
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
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 4. Historial de Clientes
            composable(Screen.HistorialCliente.route) {
                HistorialClienteScreen(viewModel = viewModel)
            }
        }
    }
}
