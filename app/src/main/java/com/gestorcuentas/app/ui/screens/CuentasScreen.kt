package com.gestorcuentas.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gestorcuentas.app.data.entity.CuentaConVentas
import com.gestorcuentas.app.ui.theme.AccentGreen
import com.gestorcuentas.app.ui.theme.WarningAmber
import com.gestorcuentas.app.utils.DateUtils
import com.gestorcuentas.app.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentasScreen(
    viewModel: InventarioViewModel,
    onCuentaClick: (Int) -> Unit,
    onNavigateToNuevaCuenta: () -> Unit
) {
    val cuentasConVentas by viewModel.cuentasConVentas.collectAsState()
    var filtroTexto by remember { mutableStateOf("") }

    val cuentasFiltradas = remember(cuentasConVentas, filtroTexto) {
        if (filtroTexto.isBlank()) {
            cuentasConVentas
        } else {
            cuentasConVentas.filter {
                it.cuenta.tipoCuenta.contains(filtroTexto, ignoreCase = true) ||
                it.cuenta.correo.contains(filtroTexto, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario de Cuentas", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNuevaCuenta,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Cuenta")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Buscador por tipo de cuenta o correo
            OutlinedTextField(
                value = filtroTexto,
                onValueChange = { filtroTexto = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Buscar por servicio o correo...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            if (cuentasFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (filtroTexto.isEmpty()) "No hay cuentas registradas en el inventario.\nToca el botón + para registrar la primera." else "No se encontraron cuentas con '$filtroTexto'.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
                ) {
                    items(cuentasFiltradas, key = { it.cuenta.id }) { item ->
                        CuentaItemCard(
                            cuentaConVentas = item,
                            onClick = { onCuentaClick(item.cuenta.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CuentaItemCard(
    cuentaConVentas: CuentaConVentas,
    onClick: () -> Unit
) {
    val cuenta = cuentaConVentas.cuenta
    val esRentable = cuentaConVentas.esRentable
    val gananciaNeta = cuentaConVentas.gananciaNeta

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cuenta.tipoCuenta.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (esRentable) AccentGreen.copy(alpha = 0.15f) else WarningAmber.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (esRentable) "✅ En Ganancia (+${DateUtils.formatearMoneda(gananciaNeta)})"
                               else "⏳ Recuperando (-${DateUtils.formatearMoneda(-gananciaNeta)})",
                        color = if (esRentable) AccentGreen else WarningAmber,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = cuenta.correo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Costo: ${DateUtils.formatearMoneda(cuenta.costoCompra)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Ventas: ${DateUtils.formatearMoneda(cuentaConVentas.totalIngresos)} (${cuentaConVentas.perfilesVendidosTotales} perfiles)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
