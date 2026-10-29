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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gestorcuentas.app.data.entity.Venta
import com.gestorcuentas.app.data.entity.VentaConCuenta
import com.gestorcuentas.app.ui.theme.AccentGreen
import com.gestorcuentas.app.ui.theme.DangerRed
import com.gestorcuentas.app.ui.theme.WarningAmber
import com.gestorcuentas.app.utils.DateUtils
import com.gestorcuentas.app.utils.MensajeEntregaUtils
import com.gestorcuentas.app.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialClienteScreen(
    viewModel: InventarioViewModel
) {
    val busquedaCelular by viewModel.busquedaCelular.collectAsState()
    val historial by viewModel.historialCliente.collectAsState()
    val context = LocalContext.current

    var ventaAEditar by remember { mutableStateOf<Venta?>(null) }
    var ventaAEliminar by remember { mutableStateOf<Venta?>(null) }

    val totalRecaudadoCliente = remember(historial) {
        historial.sumOf { it.venta.precioVentaPorPerfil }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Área de Clientes", fontWeight = FontWeight.Bold) },
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
            // Buscador por número de celular (Fijo en la parte superior)
            OutlinedTextField(
                value = busquedaCelular,
                onValueChange = { viewModel.actualizarBusquedaCelular(it) },
                placeholder = { Text("Escribe el celular del cliente...") },
                label = { Text("Buscar por Teléfono") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(12.dp)
            )

            if (busquedaCelular.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
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
                            onEditarVenta = {
                                ventaAEditar = item.venta
                            },
                            onEliminarVenta = {
                                ventaAEliminar = item.venta
                            },
                            onCopiarMensaje = {
                                val cuenta = item.cuenta
                                val venta = item.venta
                                if (cuenta != null) {
                                    val mensaje = MensajeEntregaUtils.generarMensaje(
                                        tipoCuenta = cuenta.tipoCuenta,
                                        duracionDias = venta.duracionDias,
                                        correo = cuenta.correo,
                                        contrasenaPlataforma = cuenta.contrasenaPlataforma,
                                        fechaInicio = venta.fechaInicio,
                                        fechaFin = venta.fechaFin
                                    )
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Datos Cuenta Cliente", mensaje)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "¡Mensaje de acceso copiado al portapapeles!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "No se encontraron datos de la cuenta", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo para editar la venta seleccionada
    ventaAEditar?.let { venta ->
        EditarVentaDialog(
            venta = venta,
            viewModel = viewModel,
            onDismiss = { ventaAEditar = null }
        )
    }

    // Diálogo de confirmación para eliminar venta
    ventaAEliminar?.let { venta ->
        AlertDialog(
            onDismissRequest = { ventaAEliminar = null },
            title = { Text("¿Eliminar Venta?") },
            text = { Text("Se eliminará la venta registrada para el cliente ${venta.numeroCelularCliente}. Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarVenta(venta) {
                            Toast.makeText(context, "Venta eliminada", Toast.LENGTH_SHORT).show()
                            ventaAEliminar = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { ventaAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun HistorialVentaCard(
    item: VentaConCuenta,
    onEditarVenta: () -> Unit,
    onEliminarVenta: () -> Unit,
    onCopiarMensaje: () -> Unit
) {
    val venta = item.venta
    val cuenta = item.cuenta
    val dias = DateUtils.calcularDiasRestantes(venta.fechaFin)

    val (badgeColor, badgeText) = when {
        dias < 0 -> DangerRed to "Vencida hace ${-dias}d"
        dias == 0L -> WarningAmber to "¡Vence Hoy!"
        dias in 1..3 -> WarningAmber to "$dias días disponibles"
        else -> AccentGreen to "$dias días disponibles"
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

            Text("Cliente: ${venta.numeroCelularCliente}", fontWeight = FontWeight.Bold)
            if (cuenta != null) {
                Text("Cuenta: ${cuenta.correo}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = DateUtils.formatearMoneda(venta.precioVentaPorPerfil),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AccentGreen
                )
                Text(
                    text = "${DateUtils.formatearFechaLegible(venta.fechaInicio)} al ${DateUtils.formatearFechaLegible(venta.fechaFin)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botones de acción: Copiar Mensaje, Editar y Eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onCopiarMensaje,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copiar Mensaje", style = MaterialTheme.typography.labelMedium)
                }

                OutlinedButton(
                    onClick = onEditarVenta,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(16.dp))
                }

                IconButton(
                    onClick = onEliminarVenta,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = DangerRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
