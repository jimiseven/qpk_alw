package com.gestorcuentas.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gestorcuentas.app.data.entity.Cuenta
import com.gestorcuentas.app.data.entity.Venta
import com.gestorcuentas.app.ui.theme.AccentGreen
import com.gestorcuentas.app.ui.theme.DangerRed
import com.gestorcuentas.app.ui.theme.WarningAmber
import com.gestorcuentas.app.utils.DateUtils
import com.gestorcuentas.app.utils.MensajeEntregaUtils
import com.gestorcuentas.app.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentaDetalleScreen(
    cuentaId: Int,
    viewModel: InventarioViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNuevaVenta: (Int) -> Unit
) {
    // Obtenemos directamente de la lista reactiva de ViewModel sin crear flujos nuevos (EVITA EL PARPADEO)
    val todasLasCuentas by viewModel.cuentasConVentas.collectAsState()
    val cuentaConVentas = remember(todasLasCuentas, cuentaId) {
        todasLasCuentas.find { it.cuenta.id == cuentaId }
    }
    val context = LocalContext.current

    var mostrarPasswordCorreo by remember { mutableStateOf(false) }
    var mostrarPasswordPlataforma by remember { mutableStateOf(false) }
    var mostrarDialogoEliminarCuenta by remember { mutableStateOf(false) }
    var mostrarDialogoEditarCuenta by remember { mutableStateOf(false) }
    var ventaAEditar by remember { mutableStateOf<Venta?>(null) }
    var ventaAEliminar by remember { mutableStateOf<Venta?>(null) }

    fun copiarTexto(label: String, texto: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, texto)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copiado al portapapeles", Toast.LENGTH_SHORT).show()
    }

    val estaInhabilitada = cuentaConVentas?.cuenta?.estado == "inhabilitada"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cuentaConVentas?.cuenta?.tipoCuenta?.uppercase() ?: "Detalle Cuenta", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    if (cuentaConVentas != null) {
                        IconButton(onClick = { mostrarDialogoEditarCuenta = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar Cuenta")
                        }
                        IconButton(onClick = { mostrarDialogoEliminarCuenta = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar Cuenta y Ventas", tint = DangerRed)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (cuentaConVentas != null && !estaInhabilitada) {
                ExtendedFloatingActionButton(
                    onClick = { onNavigateToNuevaVenta(cuentaConVentas.cuenta.id) },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Vender Perfil", fontWeight = FontWeight.Bold) },
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
                Text("No se encontró la cuenta.", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item { Spacer(modifier = Modifier.height(2.dp)) }

                // Alerta si está inhabilitada
                if (estaInhabilitada) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.5f)),
                            colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.12f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "🔴 Cuenta Inhabilitada: No está disponible para registrar nuevas ventas.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DangerRed
                                )
                            }
                        }
                    }
                }

                // Card Unificada de Credenciales
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Credenciales de Acceso",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (estaInhabilitada) DangerRed.copy(alpha = 0.15f) else AccentGreen.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = if (estaInhabilitada) "INHABILITADA" else "ACTIVA",
                                        color = if (estaInhabilitada) DangerRed else AccentGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))

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

                            // Fechas de vigencia de la cuenta maestra con proveedor
                            if (cuenta.fechaCreacion.isNotBlank() || cuenta.fechaFin.isNotBlank()) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Text("Vigencia Cuenta Maestra (Proveedor):", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "${DateUtils.formatearFechaLegible(cuenta.fechaCreacion)} al ${if (cuenta.fechaFin.isNotBlank()) DateUtils.formatearFechaLegible(cuenta.fechaFin) else "Indefinida"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Card Unificada de Rentabilidad de esta Cuenta
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        "Ventas Registradas (${cuentaConVentas.ventas.size})",
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
                            onEditar = { ventaAEditar = venta },
                            onEliminar = { ventaAEliminar = venta },
                            onCopiarMensaje = {
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

    // Diálogo de Edición de Cuenta
    if (mostrarDialogoEditarCuenta && cuentaConVentas != null) {
        EditarCuentaDialog(
            cuenta = cuentaConVentas.cuenta,
            viewModel = viewModel,
            onDismiss = { mostrarDialogoEditarCuenta = false }
        )
    }

    // Diálogo de Edición de Venta
    ventaAEditar?.let { venta ->
        EditarVentaDialog(
            venta = venta,
            viewModel = viewModel,
            onDismiss = { ventaAEditar = null }
        )
    }

    // Diálogo de confirmación para eliminar VENTA
    ventaAEliminar?.let { venta ->
        AlertDialog(
            onDismissRequest = { ventaAEliminar = null },
            shape = RoundedCornerShape(16.dp),
            title = { Text("¿Eliminar Venta?", fontWeight = FontWeight.Bold) },
            text = { Text("Se eliminará la venta registrada para el cliente ${venta.numeroCelularCliente}. Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarVenta(venta) {
                            Toast.makeText(context, "Venta eliminada", Toast.LENGTH_SHORT).show()
                            ventaAEliminar = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                    shape = RoundedCornerShape(10.dp)
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

    // Diálogo de confirmación para eliminar CUENTA CON TODAS SUS VENTAS VINCULADAS
    if (mostrarDialogoEliminarCuenta && cuentaConVentas != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminarCuenta = false },
            shape = RoundedCornerShape(16.dp),
            title = { Text("¿Eliminar Cuenta y sus Ventas?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Se eliminará esta cuenta maestra y TODAS las ${cuentaConVentas.ventas.size} ventas de perfiles vinculadas a ella de forma permanente.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarCuenta(cuentaConVentas.cuenta) {
                            Toast.makeText(context, "Cuenta y ventas vinculadas eliminadas", Toast.LENGTH_SHORT).show()
                            mostrarDialogoEliminarCuenta = false
                            onNavigateBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Eliminar Todo")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminarCuenta = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Item unificado de Venta dentro del detalle de cuenta
 */
@Composable
fun VentaItemCard(
    venta: Venta,
    cuenta: Cuenta,
    onEditar: () -> Unit,
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
                        venta.numeroCelularCliente,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
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
                    IconButton(onClick = onEditar) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar Venta", modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onEliminar) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar Venta", tint = DangerRed, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = DateUtils.formatearMoneda(venta.precioVentaPorPerfil),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AccentGreen
            )
            Text(
                text = "Desde ${DateUtils.formatearFechaLegible(venta.fechaInicio)} hasta ${DateUtils.formatearFechaLegible(venta.fechaFin)} (${venta.duracionDias} días)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
