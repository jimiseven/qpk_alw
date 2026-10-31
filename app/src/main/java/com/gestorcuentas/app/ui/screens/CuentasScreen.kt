package com.gestorcuentas.app.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gestorcuentas.app.R
import com.gestorcuentas.app.data.entity.CuentaConVentas
import com.gestorcuentas.app.ui.theme.AccentGreen
import com.gestorcuentas.app.ui.theme.WarningAmber
import com.gestorcuentas.app.utils.DateUtils
import com.gestorcuentas.app.utils.PhoneUtils
import com.gestorcuentas.app.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentasScreen(
    viewModel: InventarioViewModel,
    onCuentaClick: (Int) -> Unit,
    onNavigateToNuevaCuenta: () -> Unit = {}
) {
    val cuentasConVentas by viewModel.cuentasConVentas.collectAsState()
    var filtroTexto by remember { mutableStateOf("") }
    var mostrarSheetNuevaCuenta by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Búsqueda inteligente: tolera números de WhatsApp (+591 76992490 -> 76992490), correo o servicio
    val cuentasFiltradas = remember(cuentasConVentas, filtroTexto) {
        if (filtroTexto.isBlank()) {
            cuentasConVentas
        } else {
            cuentasConVentas.filter { item ->
                item.cuenta.tipoCuenta.contains(filtroTexto, ignoreCase = true) ||
                item.cuenta.correo.contains(filtroTexto, ignoreCase = true) ||
                item.ventas.any { venta ->
                    PhoneUtils.coincideNumero(venta.numeroCelularCliente, filtroTexto)
                }
            }
        }
    }

    Scaffold(
        // TopAppBar Minimalista: Solo título informativo y logo
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_mr_robot),
                            contentDescription = "Logo Mr Robot",
                            modifier = Modifier.size(36.dp)
                        )
                        Text("Inventario de Cuentas", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        // Floating Action Button en la esquina inferior derecha (Thumb Zone)
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { mostrarSheetNuevaCuenta = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nueva Cuenta", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Buscador con botón borrar y pegar
            OutlinedTextField(
                value = filtroTexto,
                onValueChange = { filtroTexto = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Buscar por servicio, correo o celular...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (filtroTexto.isNotEmpty()) {
                            IconButton(onClick = { filtroTexto = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Borrar búsqueda")
                            }
                        }
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipText = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                            if (clipText.isNotBlank()) {
                                filtroTexto = clipText.trim()
                            }
                        }) {
                            Icon(Icons.Default.ContentPaste, contentDescription = "Pegar número o texto")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            if (cuentasFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (filtroTexto.isEmpty()) "No hay cuentas registradas en el inventario.\nToca '+ Nueva Cuenta' para registrar la primera."
                               else "No se encontraron cuentas con '$filtroTexto'.",
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

    // ModalBottomSheet en la Zona del Pulgar para Nueva Cuenta
    if (mostrarSheetNuevaCuenta) {
        NuevaCuentaBottomSheet(
            viewModel = viewModel,
            onDismiss = { mostrarSheetNuevaCuenta = false }
        )
    }
}

/**
 * Item unificado de Cuenta en inventario con el mismo estilo de Card de toda la aplicación
 */
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
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = cuenta.tipoCuenta.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (cuenta.estado == "inhabilitada") {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "INHABILITADA",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (esRentable) AccentGreen.copy(alpha = 0.15f) else WarningAmber.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (esRentable) "✅ En Ganancia (+${DateUtils.formatearMoneda(gananciaNeta)})"
                               else "⏳ Recuperando (-${DateUtils.formatearMoneda(-gananciaNeta)})",
                        color = if (esRentable) AccentGreen else WarningAmber,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

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
                    text = "Ventas: ${DateUtils.formatearMoneda(cuentaConVentas.totalIngresos)} (${cuentaConVentas.ventas.size} ventas)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
