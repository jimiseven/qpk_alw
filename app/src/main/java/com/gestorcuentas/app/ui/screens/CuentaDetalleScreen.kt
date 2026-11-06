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
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Autorenew
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
    onNavigateToNuevaVenta: (Int) -> Unit = {}
) {
    val todasLasCuentas by viewModel.cuentasConVentas.collectAsState()
    val cuentaConVentas = remember(todasLasCuentas, cuentaId) {
        todasLasCuentas.find { it.cuenta.id == cuentaId }
    }
    val context = LocalContext.current
    val hoy = remember { java.time.LocalDate.now() }

    var mostrarPasswordCorreo by remember { mutableStateOf(false) }
    var mostrarPasswordPlataforma by remember { mutableStateOf(false) }
    var mostrarDialogoEliminarCuenta by remember { mutableStateOf(false) }
    var mostrarDialogoEditarCuenta by remember { mutableStateOf(false) }
    var mostrarSheetNuevaVenta by remember { mutableStateOf(false) }
    var ventaAEditar by remember { mutableStateOf<Venta?>(null) }
    var ventaAEliminar by remember { mutableStateOf<Venta?>(null) }
    var mostrarDialogoRenovarCuenta by remember { mutableStateOf(false) }
    var ventaARenovar by remember { mutableStateOf<Venta?>(null) }

    fun copiarTexto(label: String, texto: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, texto)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copiado al portapapeles", Toast.LENGTH_SHORT).show()
    }

    val estaInhabilitada = cuentaConVentas?.cuenta?.estado == "inhabilitada"

    Scaffold(
        // TopAppBar Minimalista: Solo navegación de regreso y contexto visual
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        cuentaConVentas?.cuenta?.tipoCuenta?.uppercase() ?: "Detalle Cuenta",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        },
        // Barra Inferior de Acciones Ergonómicas (Thumb Zone) - Distribución Equilibrada solo con Iconos
        bottomBar = {
            if (cuentaConVentas != null) {
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 10.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Editar Cuenta
                        FilledTonalIconButton(
                            onClick = { mostrarDialogoEditarCuenta = true },
                            modifier = Modifier.size(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar Cuenta", modifier = Modifier.size(24.dp))
                        }

                        // 2. Renovar Cuenta Maestra
                        FilledTonalIconButton(
                            onClick = { mostrarDialogoRenovarCuenta = true },
                            modifier = Modifier.size(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Icon(Icons.Default.Autorenew, contentDescription = "Renovar Cuenta Maestra", modifier = Modifier.size(24.dp))
                        }

                        // 3. Eliminar Cuenta
                        FilledTonalIconButton(
                            onClick = { mostrarDialogoEliminarCuenta = true },
                            modifier = Modifier.size(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = DangerRed.copy(alpha = 0.12f),
                                contentColor = DangerRed
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar Cuenta", modifier = Modifier.size(24.dp), tint = DangerRed)
                        }

                        // 4. Vender Perfil (Acción Principal)
                        if (!estaInhabilitada) {
                            FilledIconButton(
                                onClick = { mostrarSheetNuevaVenta = true },
                                modifier = Modifier.size(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Icon(Icons.Default.AddShoppingCart, contentDescription = "Vender Perfil", modifier = Modifier.size(25.dp))
                            }
                        }
                    }
                }
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

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Vigencia Cuenta Maestra (Proveedor):", style = MaterialTheme.typography.labelSmall)
                                    val textoVigencia = if (cuenta.fechaCreacion.isNotBlank() || cuenta.fechaFin.isNotBlank()) {
                                        "${DateUtils.formatearFechaLegible(cuenta.fechaCreacion)} al ${if (cuenta.fechaFin.isNotBlank()) DateUtils.formatearFechaLegible(cuenta.fechaFin) else "Indefinida"}"
                                    } else {
                                        "Sin vigencia fijada con proveedor"
                                    }
                                    Text(
                                        textoVigencia,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (cuenta.renovacionesCount > 0) {
                                        Text(
                                            "🔄 Renovada ${cuenta.renovacionesCount} ${if (cuenta.renovacionesCount == 1) "vez" else "veces"} (Última: ${DateUtils.formatearFechaLegible(cuenta.ultimaRenovacion)})",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                FilledTonalButton(
                                    onClick = { mostrarDialogoRenovarCuenta = true },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.Autorenew, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Renovar", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Card Unificada de Rentabilidad
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

                // Lista de Perfiles Vendidos
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
                            hoy = hoy,
                            onEditar = { ventaAEditar = venta },
                            onEliminar = { ventaAEliminar = venta },
                            onRenovar = { ventaARenovar = venta },
                            onCopiarMensaje = {
                                val mensaje = MensajeEntregaUtils.generarMensaje(
                                    context = context,
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
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    // ModalBottomSheet para Editar Cuenta
    if (mostrarDialogoEditarCuenta && cuentaConVentas != null) {
        EditarCuentaDialog(
            cuenta = cuentaConVentas.cuenta,
            viewModel = viewModel,
            onDismiss = { mostrarDialogoEditarCuenta = false }
        )
    }

    // ModalBottomSheet para Registrar Nueva Venta sobre esta Cuenta
    if (mostrarSheetNuevaVenta && cuentaConVentas != null) {
        NuevaVentaBottomSheet(
            initialCuentaId = cuentaConVentas.cuenta.id,
            viewModel = viewModel,
            onDismiss = { mostrarSheetNuevaVenta = false }
        )
    }

    // ModalBottomSheet para Editar Venta
    ventaAEditar?.let { venta ->
        EditarVentaDialog(
            venta = venta,
            viewModel = viewModel,
            onDismiss = { ventaAEditar = null }
        )
    }

    // Confirmación Eliminar Venta
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

    // Confirmación Eliminar Cuenta
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

    // Diálogo Renovar Cuenta Maestra con Proveedor
    if (mostrarDialogoRenovarCuenta && cuentaConVentas != null) {
        RenovarCuentaDialog(
            cuenta = cuentaConVentas.cuenta,
            onDismiss = { mostrarDialogoRenovarCuenta = false },
            onConfirmar = { dias, costo ->
                viewModel.renovarCuentaMaestra(cuentaConVentas.cuenta, dias, costo) { nuevaFecha ->
                    Toast.makeText(context, "✅ Cuenta maestra renovada hasta $nuevaFecha", Toast.LENGTH_LONG).show()
                    mostrarDialogoRenovarCuenta = false
                }
            }
        )
    }

    // BottomSheet Renovar Venta de Cliente
    ventaARenovar?.let { venta ->
        RenovarVentaBottomSheet(
            ventaConCuenta = com.gestorcuentas.app.data.entity.VentaConCuenta(venta, cuentaConVentas?.cuenta),
            onDismiss = { ventaARenovar = null },
            onConfirmar = { dias, precio ->
                viewModel.renovarVenta(
                    venta = venta,
                    dias = dias,
                    montoCobrado = precio,
                    context = context,
                    onSuccess = { nuevaFecha, mensajeWhatsApp ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Renovación Cliente", mensajeWhatsApp)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "✅ Venta renovada hasta $nuevaFecha y mensaje copiado", Toast.LENGTH_LONG).show()
                        ventaARenovar = null
                    }
                )
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
    hoy: java.time.LocalDate = remember { java.time.LocalDate.now() },
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    onCopiarMensaje: () -> Unit,
    onRenovar: () -> Unit
) {
    val context = LocalContext.current
    val diasRestantes = remember(venta.fechaFin, hoy) {
        DateUtils.calcularDiasRestantes(venta.fechaFin, hoy)
    }

    val (colorBadge, textoBadge) = remember(diasRestantes) {
        when {
            diasRestantes < 0 -> DangerRed to "Vencida (${-diasRestantes}d)"
            diasRestantes == 0L -> WarningAmber to "Vence Hoy"
            diasRestantes in 1..3 -> WarningAmber to "Vence en ${diasRestantes}d"
            else -> AccentGreen to "Activa (${diasRestantes}d)"
        }
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
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Celular Cliente", venta.numeroCelularCliente)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "📋 Celular copiado: ${venta.numeroCelularCliente}", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                venta.numeroCelularCliente,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copiar Celular",
                                modifier = Modifier.size(13.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
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
                    if (venta.renovacionesCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "🔄 x${venta.renovacionesCount}",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = onRenovar,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(Icons.Default.Autorenew, contentDescription = "Renovar Venta", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onCopiarMensaje, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copiar Mensaje", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onEditar, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar Venta", modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onEliminar, modifier = Modifier.size(34.dp)) {
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
