package com.gestorcuentas.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gestorcuentas.app.data.entity.Venta
import com.gestorcuentas.app.ui.theme.AccentGreen
import com.gestorcuentas.app.ui.theme.DangerRed
import com.gestorcuentas.app.ui.theme.WarningAmber
import com.gestorcuentas.app.utils.DateUtils
import com.gestorcuentas.app.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentaDetalleScreen(
    cuentaId: Int,
    viewModel: InventarioViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNuevaVenta: (Int) -> Unit
) {
    val cuentaConVentasState = viewModel.obtenerCuentaConVentas(cuentaId).collectAsState(initial = null)
    val cuentaConVentas = cuentaConVentasState.value
    val context = LocalContext.current

    var mostrarPasswordCorreo by remember { mutableStateOf(false) }
    var mostrarPasswordPlataforma by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    fun copiarTexto(label: String, texto: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, texto)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copiado al portapapeles", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cuentaConVentas?.cuenta?.tipoCuenta?.uppercase() ?: "Detalle Cuenta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    if (cuentaConVentas != null) {
                        IconButton(onClick = { mostrarDialogoEliminar = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar Cuenta", tint = DangerRed)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (cuentaConVentas != null) {
                ExtendedFloatingActionButton(
                    onClick = { onNavigateToNuevaVenta(cuentaConVentas.cuenta.id) },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Vender Perfil") },
                    containerColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { padding ->
        if (cuentaConVentas == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Cargando detalles...")
            }
        } else {
            val cuenta = cuentaConVentas.cuenta
            val esRentable = cuentaConVentas.esRentable
            val gananciaNeta = cuentaConVentas.gananciaNeta

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card de Credenciales
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Credenciales de Acceso",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Correo
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Correo Electrónico:", style = MaterialTheme.typography.labelSmall)
                                    Text(cuenta.correo, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                }
                                IconButton(onClick = { copiarTexto("Correo", cuenta.correo) }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copiar Correo", modifier = Modifier.size(18.dp))
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            // Contraseña Plataforma
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Contraseña Plataforma (${cuenta.tipoCuenta}):", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        if (mostrarPasswordPlataforma) cuenta.contrasenaPlataforma else "••••••••••••",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Row {
                                    IconButton(onClick = { mostrarPasswordPlataforma = !mostrarPasswordPlataforma }) {
                                        Icon(
                                            if (mostrarPasswordPlataforma) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Ver Contraseña",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(onClick = { copiarTexto("Contraseña Plataforma", cuenta.contrasenaPlataforma) }) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            // Contraseña Correo
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Contraseña Correo:", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        if (mostrarPasswordCorreo) cuenta.contrasenaCorreo else "••••••••••••",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Row {
                                    IconButton(onClick = { mostrarPasswordCorreo = !mostrarPasswordCorreo }) {
                                        Icon(
                                            if (mostrarPasswordCorreo) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Ver Contraseña",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(onClick = { copiarTexto("Contraseña Correo", cuenta.contrasenaCorreo) }) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Card de Rentabilidad de esta Cuenta
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Rendimiento Financiero de la Cuenta",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Costo de Compra:", style = MaterialTheme.typography.labelSmall)
                                    Text(DateUtils.formatearMoneda(cuenta.costoCompra), style = MaterialTheme.typography.titleMedium)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Total Recaudado:", style = MaterialTheme.typography.labelSmall)
                                    Text(DateUtils.formatearMoneda(cuentaConVentas.totalIngresos), style = MaterialTheme.typography.titleMedium, color = AccentGreen)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (esRentable) AccentGreen.copy(alpha = 0.15f) else WarningAmber.copy(alpha = 0.15f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (esRentable)
                                        "GANANCIA NETA: +${DateUtils.formatearMoneda(gananciaNeta)} (Superó el costo)"
                                    else
                                        "EN RECUPERACIÓN: Falta ${DateUtils.formatearMoneda(-gananciaNeta)} para cubrir costo",
                                    color = if (esRentable) AccentGreen else WarningAmber,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Lista de Perfiles Vendidos de esta cuenta
                item {
                    Text(
                        "Perfiles Vendidos (${cuentaConVentas.ventas.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (cuentaConVentas.ventas.isEmpty()) {
                    item {
                        Text(
                            "Aún no se han vendido perfiles de esta cuenta.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(cuentaConVentas.ventas, key = { it.id }) { venta ->
                        VentaItemCard(
                            venta = venta,
                            cuenta = cuenta,
                            onEliminar = { viewModel.eliminarVenta(venta) },
                            onCopiarMensaje = {
                                val mensaje = com.gestorcuentas.app.utils.MensajeEntregaUtils.generarMensaje(
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
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }
    }

    // Diálogo de confirmación de eliminación
    if (mostrarDialogoEliminar && cuentaConVentas != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("¿Eliminar Cuenta?") },
            text = { Text("Se eliminará esta cuenta maestra y todas las ventas de perfiles asociadas a ella. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarCuenta(cuentaConVentas.cuenta)
                        mostrarDialogoEliminar = false
                        onNavigateBack()
                    }
                ) {
                    Text("Eliminar", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun VentaItemCard(
    venta: Venta,
    cuenta: com.gestorcuentas.app.data.entity.Cuenta,
    onEliminar: () -> Unit,
    onCopiarMensaje: () -> Unit
) {
    val estado = DateUtils.obtenerEstadoVencimiento(venta.fechaFin)
    val diasRestantes = DateUtils.calcularDiasRestantes(venta.fechaFin)

    val (colorBadge, textoBadge) = when (estado) {
        DateUtils.EstadoVencimiento.VENCIDA -> DangerRed to "Vencida (${-diasRestantes}d)"
        DateUtils.EstadoVencimiento.VENCE_HOY -> WarningAmber to "Vence Hoy"
        DateUtils.EstadoVencimiento.POR_VENCER -> WarningAmber to "Vence en ${diasRestantes}d"
        DateUtils.EstadoVencimiento.ACTIVA -> AccentGreen to "Activa (${diasRestantes}d)"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        venta.numeroCelularCliente,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = colorBadge.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = textoBadge,
                            color = colorBadge,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row {
                    IconButton(onClick = onCopiarMensaje) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copiar Mensaje", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onEliminar) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar Venta", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${venta.cantidadPerfiles} perfil/es • ${DateUtils.formatearMoneda(venta.precioVentaPorPerfil * venta.cantidadPerfiles)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Desde ${DateUtils.formatearFechaLegible(venta.fechaInicio)} hasta ${DateUtils.formatearFechaLegible(venta.fechaFin)} (${venta.duracionDias} días)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
