package com.gestorcuentas.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gestorcuentas.app.R
import com.gestorcuentas.app.data.entity.Venta
import com.gestorcuentas.app.data.entity.VentaConCuenta
import com.gestorcuentas.app.ui.theme.AccentGreen
import com.gestorcuentas.app.ui.theme.DangerRed
import com.gestorcuentas.app.ui.theme.WarningAmber
import com.gestorcuentas.app.utils.DateUtils
import com.gestorcuentas.app.utils.MensajeEntregaUtils
import com.gestorcuentas.app.utils.PhoneUtils
import com.gestorcuentas.app.viewmodel.InventarioViewModel

enum class FiltroHistorial {
    TODOS,
    ACTIVAS,
    VENCIDAS
}

enum class OrdenHistorial(val titulo: String) {
    ULTIMO_VENDIDO("Último vendido"),
    MAS_DIAS("Más días arriba"),
    MENOS_DIAS("Menos días arriba");

    fun siguiente(): OrdenHistorial = when (this) {
        ULTIMO_VENDIDO -> MAS_DIAS
        MAS_DIAS -> MENOS_DIAS
        MENOS_DIAS -> ULTIMO_VENDIDO
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialClienteScreen(
    viewModel: InventarioViewModel
) {
    val busquedaCelular by viewModel.busquedaCelular.collectAsState()
    val allVentasConCuenta by viewModel.allVentasConCuenta.collectAsState()
    val context = LocalContext.current
    val hoy = remember { java.time.LocalDate.now() }

    var ventaAEditar by remember { mutableStateOf<Venta?>(null) }
    var ventaAEliminar by remember { mutableStateOf<Venta?>(null) }
    var ventaARenovar by remember { mutableStateOf<VentaConCuenta?>(null) }
    var filtroEstado by remember { mutableStateOf(FiltroHistorial.TODOS) }
    var ordenVentas by remember { mutableStateOf(OrdenHistorial.ULTIMO_VENDIDO) }

    // Búsqueda inteligente con normalización y tolerancia de prefijos internacionales WhatsApp (+591, etc.)
    val historialFiltrado = remember(allVentasConCuenta, busquedaCelular) {
        if (busquedaCelular.isBlank()) {
            allVentasConCuenta
        } else {
            allVentasConCuenta.filter { item ->
                PhoneUtils.coincideNumero(item.venta.numeroCelularCliente, busquedaCelular)
            }
        }
    }

    val conteoActivas = remember(historialFiltrado, hoy) {
        historialFiltrado.count { DateUtils.calcularDiasRestantes(it.venta.fechaFin, hoy) >= 0 }
    }
    val conteoVencidas = remember(historialFiltrado, hoy) {
        historialFiltrado.count { DateUtils.calcularDiasRestantes(it.venta.fechaFin, hoy) < 0 }
    }

    val listaFinal = remember(historialFiltrado, filtroEstado, ordenVentas, hoy) {
        val filtrada = when (filtroEstado) {
            FiltroHistorial.TODOS -> historialFiltrado
            FiltroHistorial.ACTIVAS -> historialFiltrado.filter {
                DateUtils.calcularDiasRestantes(it.venta.fechaFin, hoy) >= 0
            }
            FiltroHistorial.VENCIDAS -> historialFiltrado.filter {
                DateUtils.calcularDiasRestantes(it.venta.fechaFin, hoy) < 0
            }
        }
        when (ordenVentas) {
            OrdenHistorial.ULTIMO_VENDIDO -> filtrada.sortedByDescending { it.venta.id }
            OrdenHistorial.MAS_DIAS -> filtrada.sortedByDescending {
                DateUtils.calcularDiasRestantes(it.venta.fechaFin, hoy)
            }
            OrdenHistorial.MENOS_DIAS -> filtrada.sortedBy {
                DateUtils.calcularDiasRestantes(it.venta.fechaFin, hoy)
            }
        }
    }

    val totalRecaudadoCliente = remember(listaFinal) {
        listaFinal.sumOf { it.venta.precioVentaPorPerfil }
    }

    Scaffold(
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
                        Text(
                            "Historial de Clientes",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
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
            // Buscador por número de celular con botón Borrar y botón Pegar
            OutlinedTextField(
                value = busquedaCelular,
                onValueChange = { viewModel.actualizarBusquedaCelular(it) },
                placeholder = { Text("Pega o escribe el celular (+591 ...)") },
                label = { Text("Buscar por Teléfono / WhatsApp") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (busquedaCelular.isNotEmpty()) {
                            IconButton(onClick = { viewModel.actualizarBusquedaCelular("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Borrar búsqueda")
                            }
                        }
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipText = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                            if (clipText.isNotBlank()) {
                                viewModel.actualizarBusquedaCelular(clipText.trim())
                            }
                        }) {
                            Icon(Icons.Default.ContentPaste, contentDescription = "Pegar número de WhatsApp")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 6.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(14.dp)
            )

            // Filtros de Estado en la Zona del Pulgar: Todos / Activas / Vencidas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filtroEstado == FiltroHistorial.TODOS,
                    onClick = { filtroEstado = FiltroHistorial.TODOS },
                    label = {
                        Text(
                            "Todos (${historialFiltrado.size})",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (filtroEstado == FiltroHistorial.TODOS) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = filtroEstado == FiltroHistorial.ACTIVAS,
                    onClick = { filtroEstado = FiltroHistorial.ACTIVAS },
                    label = {
                        Text(
                            "Activas ($conteoActivas)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (filtroEstado == FiltroHistorial.ACTIVAS) FontWeight.Bold else FontWeight.Medium,
                            color = if (filtroEstado == FiltroHistorial.ACTIVAS) MaterialTheme.colorScheme.onPrimaryContainer else AccentGreen
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = filtroEstado == FiltroHistorial.VENCIDAS,
                    onClick = { filtroEstado = FiltroHistorial.VENCIDAS },
                    label = {
                        Text(
                            "Vencidas ($conteoVencidas)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (filtroEstado == FiltroHistorial.VENCIDAS) FontWeight.Bold else FontWeight.Medium,
                            color = if (filtroEstado == FiltroHistorial.VENCIDAS) MaterialTheme.colorScheme.onPrimaryContainer else DangerRed
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Selector Cíclico de Orden de Ventas (3 Estados: Último vendido -> Más días arriba -> Menos días arriba)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Sort,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Orden:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                FilledTonalButton(
                    onClick = { ordenVentas = ordenVentas.siguiente() },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    val (icono, descripcion) = when (ordenVentas) {
                        OrdenHistorial.ULTIMO_VENDIDO -> Icons.Default.Schedule to "Último vendido"
                        OrdenHistorial.MAS_DIAS -> Icons.Default.ArrowUpward to "Más días arriba"
                        OrdenHistorial.MENOS_DIAS -> Icons.Default.ArrowDownward to "Menos días arriba"
                    }
                    Icon(icono, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        descripcion,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.SwapVert, contentDescription = "Cambiar orden", modifier = Modifier.size(16.dp))
                }
            }

            if (busquedaCelular.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ventas: ${listaFinal.size}", fontWeight = FontWeight.Medium)
                        Text(
                            "Total: ${DateUtils.formatearMoneda(totalRecaudadoCliente)}",
                            fontWeight = FontWeight.Bold,
                            color = AccentGreen
                        )
                    }
                }
            }

            if (listaFinal.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (busquedaCelular.isBlank()) {
                            when (filtroEstado) {
                                FiltroHistorial.TODOS -> "No hay ventas registradas aún."
                                FiltroHistorial.ACTIVAS -> "No hay ventas activas en este momento."
                                FiltroHistorial.VENCIDAS -> "No hay ventas vencidas."
                            }
                        } else {
                            "No se encontraron ventas con los filtros actuales para '$busquedaCelular'."
                        },
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
                    items(listaFinal, key = { it.venta.id }) { item ->
                        HistorialVentaCard(
                            item = item,
                            hoy = hoy,
                            onEditarVenta = { ventaAEditar = item.venta },
                            onEliminarVenta = { ventaAEliminar = item.venta },
                            onRenovarVenta = { ventaARenovar = item },
                            onCopiarMensaje = {
                                val cuenta = item.cuenta
                                val venta = item.venta
                                if (cuenta != null) {
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

    // BottomSheet Renovar Venta desde Historial
    ventaARenovar?.let { vConCuenta ->
        RenovarVentaBottomSheet(
            ventaConCuenta = vConCuenta,
            onDismiss = { ventaARenovar = null },
            onConfirmar = { dias, precio ->
                viewModel.renovarVenta(
                    venta = vConCuenta.venta,
                    dias = dias,
                    montoCobrado = precio,
                    context = context,
                    onSuccess = { nuevaFecha, mensajeWhatsApp ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Mensaje Cuenta", mensajeWhatsApp)
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
 * Item unificado de Venta en Historial de Cliente
 */
@Composable
fun HistorialVentaCard(
    item: VentaConCuenta,
    hoy: java.time.LocalDate = remember { java.time.LocalDate.now() },
    onEditarVenta: () -> Unit,
    onEliminarVenta: () -> Unit,
    onRenovarVenta: () -> Unit,
    onCopiarMensaje: () -> Unit
) {
    val venta = item.venta
    val cuenta = item.cuenta
    val context = LocalContext.current
    val dias = remember(venta.fechaFin, hoy) {
        DateUtils.calcularDiasRestantes(venta.fechaFin, hoy)
    }

    val (badgeColor, badgeText) = remember(dias) {
        when {
            dias < 0 -> DangerRed to "Vencida hace ${-dias}d"
            dias == 0L -> WarningAmber to "¡Vence Hoy!"
            dias in 1..3 -> WarningAmber to "$dias días disponibles"
            else -> AccentGreen to "$dias días disponibles"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = cuenta?.tipoCuenta?.uppercase() ?: "SERVICIO",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
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

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Cliente:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Celular Cliente", venta.numeroCelularCliente)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "📋 Celular copiado: ${venta.numeroCelularCliente}", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            venta.numeroCelularCliente,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Copiar celular",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

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

            Spacer(modifier = Modifier.height(10.dp))

            // Botones de acción: Renovar (solo icono), Copiar Número, Copiar Mensaje, Editar y Eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Renovar venta: SOLO ICONO (sin texto)
                FilledTonalIconButton(
                    onClick = onRenovarVenta,
                    modifier = Modifier.size(42.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Autorenew,
                        contentDescription = "Renovar Venta",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // 2. Copiar solo número de cliente al portapapeles
                FilledTonalIconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Celular Cliente", venta.numeroCelularCliente)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "📋 Celular copiado: ${venta.numeroCelularCliente}", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(42.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = "Copiar Teléfono del Cliente",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // 3. Copiar mensaje WhatsApp
                Button(
                    onClick = onCopiarMensaje,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    modifier = Modifier.height(42.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Mensaje", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }

                // 4. Editar Venta
                OutlinedIconButton(
                    onClick = onEditarVenta,
                    modifier = Modifier.size(42.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(18.dp))
                }

                // 5. Eliminar Venta
                IconButton(
                    onClick = onEliminarVenta,
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = DangerRed, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
