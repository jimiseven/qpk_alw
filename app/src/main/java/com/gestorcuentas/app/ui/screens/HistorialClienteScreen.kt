package com.gestorcuentas.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gestorcuentas.app.data.entity.VentaConCuenta
import com.gestorcuentas.app.ui.theme.AccentGreen
import com.gestorcuentas.app.ui.theme.DangerRed
import com.gestorcuentas.app.ui.theme.WarningAmber
import com.gestorcuentas.app.utils.DateUtils
import com.gestorcuentas.app.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialClienteScreen(
    viewModel: InventarioViewModel
) {
    val busquedaCelular by viewModel.busquedaCelular.collectAsState()
    val historial by viewModel.historialCliente.collectAsState()
    val context = LocalContext.current

    val totalRecaudadoCliente = remember(historial) {
        historial.sumOf { it.venta.cantidadPerfiles * it.venta.precioVentaPorPerfil }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Clientes", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Buscador por número de celular
            OutlinedTextField(
                value = busquedaCelular,
                onValueChange = { viewModel.actualizarBusquedaCelular(it) },
                placeholder = { Text("Escribe el celular del cliente...") },
                label = { Text("Buscar por Teléfono") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(12.dp)
            )

            if (busquedaCelular.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ventas: ${historial.size}", fontWeight = FontWeight.Medium)
                        Text(
                            "Total: ${DateUtils.formatearMoneda(totalRecaudadoCliente)}",
                            fontWeight = FontWeight.Bold,
                            color = AccentGreen
                        )
                    }
                }
            }

            if (historial.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (busquedaCelular.isBlank()) "No hay ventas registradas aún." else "No se encontraron ventas para el celular '$busquedaCelular'.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(historial, key = { it.venta.id }) { item ->
                        HistorialVentaCard(
                            item = item,
                            onCopiar = { texto ->
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Info Venta", texto)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistorialVentaCard(
    item: VentaConCuenta,
    onCopiar: (String) -> Unit
) {
    val venta = item.venta
    val cuenta = item.cuenta
    val estado = DateUtils.obtenerEstadoVencimiento(venta.fechaFin)
    val dias = DateUtils.calcularDiasRestantes(venta.fechaFin)

    val (badgeColor, badgeText) = when (estado) {
        DateUtils.EstadoVencimiento.VENCIDA -> DangerRed to "Vencida"
        DateUtils.EstadoVencimiento.VENCE_HOY -> WarningAmber to "¡Vence Hoy!"
        DateUtils.EstadoVencimiento.POR_VENCER -> WarningAmber to "Vence en ${dias}d"
        DateUtils.EstadoVencimiento.ACTIVA -> AccentGreen to "Activa (${dias}d)"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cuenta?.tipoCuenta?.uppercase() ?: "SERVICIO",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Cliente: ${venta.numeroCelularCliente}", fontWeight = FontWeight.SemiBold)
            if (cuenta != null) {
                Text("Cuenta: ${cuenta.correo}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${venta.cantidadPerfiles} perfil/es • ${DateUtils.formatearMoneda(item.totalVenta)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${venta.fechaInicio} al ${venta.fechaFin}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
