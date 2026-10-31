package com.gestorcuentas.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.WarningAmber
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
import com.gestorcuentas.app.data.entity.Venta
import com.gestorcuentas.app.data.entity.VentaConCuenta
import com.gestorcuentas.app.ui.theme.AccentGreen
import com.gestorcuentas.app.ui.theme.DangerRed
import com.gestorcuentas.app.ui.theme.WarningAmber
import com.gestorcuentas.app.utils.DateUtils
import com.gestorcuentas.app.utils.MensajeEntregaUtils
import com.gestorcuentas.app.utils.PhoneUtils
import com.gestorcuentas.app.viewmodel.InventarioViewModel

enum class DashboardTab {
    CUENTAS_ACTIVAS,
    TODAS_LAS_VENTAS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: InventarioViewModel,
    onCuentaClick: (Int) -> Unit = {},
    onNavigateToNuevaVenta: () -> Unit = {},
    onNavigateToNuevaCuenta: () -> Unit = {},
    onOpenConfiguracion: () -> Unit = {}
) {
    val resumen by viewModel.resumenFinanciero.collectAsState()
    val cuentasConVentas by viewModel.cuentasConVentas.collectAsState()
    val allVentasConCuenta by viewModel.allVentasConCuenta.collectAsState()
    val context = LocalContext.current
    val hoy = remember { java.time.LocalDate.now() }

    // Detección proactiva de ventas que vencen hoy (Alerta In-App)
    val ventasVencenHoy = remember(allVentasConCuenta, hoy) {
        allVentasConCuenta.filter {
            DateUtils.calcularDiasRestantes(it.venta.fechaFin, hoy) == 0L
        }
    }

    // Estado de la Card seleccionada: Por defecto "Cuentas Activas"
    var selectedTab by remember { mutableStateOf(DashboardTab.CUENTAS_ACTIVAS) }

    // Buscador para la lista de ventas
    var busquedaVentas by remember { mutableStateOf("") }

    // Estados para edición y eliminación de venta
    var ventaAEditar by remember { mutableStateOf<Venta?>(null) }
    var ventaAEliminar by remember { mutableStateOf<Venta?>(null) }

    // BottomSheets en la Zona del Pulgar (Thumb Zone)
    var mostrarSheetNuevaVenta by remember { mutableStateOf(false) }
    var mostrarSheetNuevaCuenta by remember { mutableStateOf(false) }
    var mostrarSheetEstadisticas by remember { mutableStateOf(false) }
    var mostrarSheetCopiaSeguridad by remember { mutableStateOf(false) }

    var jsonImportarPendiente by remember { mutableStateOf<String?>(null) }
    var resumenImportarCuentas by remember { mutableStateOf(0) }
    var resumenImportarVentas by remember { mutableStateOf(0) }

    // Launcher para Exportar Backup (Crear Documento JSON)
    val exportarBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            viewModel.exportarBackup { jsonString ->
                try {
                    context.contentResolver.openOutputStream(uri, "wt")?.use { output ->
                        val bytes = jsonString.toByteArray(Charsets.UTF_8)
                        output.write(bytes)
                        output.flush()
                        (output as? java.io.FileOutputStream)?.fd?.sync()
                    }
                    Toast.makeText(context, "✅ Copia de seguridad guardada exitosamente", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error al guardar archivo: ${e.localizedMessage ?: e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Launcher para Importar Backup (Lectura inmediata y segura de bytes)
    val importarBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                if (bytes == null || bytes.isEmpty()) {
                    Toast.makeText(context, "El archivo seleccionado está vacío (0 bytes)", Toast.LENGTH_LONG).show()
                    return@rememberLauncherForActivityResult
                }
                val text = String(bytes, Charsets.UTF_8).trim().removePrefix("\uFEFF").trim()
                if (text.isEmpty()) {
                    Toast.makeText(context, "El archivo no contiene texto legible", Toast.LENGTH_LONG).show()
                    return@rememberLauncherForActivityResult
                }
                val (cuentas, ventas) = com.gestorcuentas.app.utils.BackupUtils.parsearBackup(text)
                resumenImportarCuentas = cuentas.size
                resumenImportarVentas = ventas.size
                jsonImportarPendiente = text
            } catch (e: Exception) {
                Toast.makeText(context, "Error al leer respaldo: ${e.localizedMessage ?: e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Filtrar cuentas activas
    val cuentasActivas = remember(cuentasConVentas) {
        cuentasConVentas.filter { it.cuenta.estado.equals("activa", ignoreCase = true) }
    }

    // Filtrar ventas con búsqueda inteligente de teléfono / WhatsApp
    val ventasFiltradas = remember(allVentasConCuenta, busquedaVentas) {
        if (busquedaVentas.isBlank()) {
            allVentasConCuenta
        } else {
            allVentasConCuenta.filter { item ->
                PhoneUtils.coincideNumero(item.venta.numeroCelularCliente, busquedaVentas) ||
                (item.cuenta?.tipoCuenta?.contains(busquedaVentas, ignoreCase = true) == true) ||
                (item.cuenta?.correo?.contains(busquedaVentas, ignoreCase = true) == true)
            }
        }
    }

    Scaffold(
        // TopAppBar Minimalista: Solo Identidad Visual + Ajustes de bajo uso
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
                            "Panel Principal",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = onOpenConfiguracion) {
                        Icon(Icons.Default.Settings, contentDescription = "Configuración y Seguridad")
                    }
                }
            )
        },
        // Floating Action Button en la esquina inferior derecha (Thumb Zone)
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { mostrarSheetNuevaVenta = true },
                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                text = { Text("Nueva Venta", fontWeight = FontWeight.Bold) },
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
            // ==========================================
            // ALERTA IN-APP: CUENTAS QUE VENCEN HOY
            // ==========================================
            if (ventasVencenHoy.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedTab = DashboardTab.TODAS_LAS_VENTAS },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, WarningAmber),
                    colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(alpha = 0.12f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "¡${ventasVencenHoy.size} ${if (ventasVencenHoy.size == 1) "perfil vence" else "perfiles vencen"} hoy!",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Toca aquí para ver los clientes y enviar mensaje de renovación.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // ==========================================
            // 1. BANNER FINANCIERO COMPACTO
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
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
                    Column {
                        Text("Ingresos: ${DateUtils.formatearMoneda(resumen.totalIngresos)}", style = MaterialTheme.typography.bodySmall, color = AccentGreen, fontWeight = FontWeight.Bold)
                        Text("Costos: ${DateUtils.formatearMoneda(resumen.totalCostos)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Utilidad Neta",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = DateUtils.formatearMoneda(resumen.gananciaNeta),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (resumen.gananciaNeta >= 0) AccentGreen else DangerRed
                        )
                    }
                }
            }

            // ==========================================
            // 2. ACCIONES SECUNDARIAS ERGONÓMICAS Y SIMÉTRICAS
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Estadísticas
                Surface(
                    onClick = { mostrarSheetEstadisticas = true },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Estadísticas",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Botón Backup
                Surface(
                    onClick = { mostrarSheetCopiaSeguridad = true },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backup,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Backup",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Botón + Cuenta
                Surface(
                    onClick = { mostrarSheetNuevaCuenta = true },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "+ Cuenta",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ==========================================
            // 3. SELECTOR DE VISTA TIPO CARDS (FIJO)
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val isCuentasSelected = selectedTab == DashboardTab.CUENTAS_ACTIVAS
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = DashboardTab.CUENTAS_ACTIVAS },
                    shape = RoundedCornerShape(16.dp),
                    border = if (isCuentasSelected)
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    else
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCuentasSelected)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isCuentasSelected) 4.dp else 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Cuentas Activas",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isCuentasSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCuentasSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (isCuentasSelected) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "${cuentasActivas.size}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isCuentasSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "en inventario",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isCuentasSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val isVentasSelected = selectedTab == DashboardTab.TODAS_LAS_VENTAS
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = DashboardTab.TODAS_LAS_VENTAS },
                    shape = RoundedCornerShape(16.dp),
                    border = if (isVentasSelected)
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    else
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isVentasSelected)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isVentasSelected) 4.dp else 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Ventas",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isVentasSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isVentasSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (isVentasSelected) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "${allVentasConCuenta.size}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isVentasSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${resumen.totalVentasActivas} vigentes",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isVentasSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // 4. LISTADO DINÁMICO (ZONA MEDIA / BAJA)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (selectedTab == DashboardTab.CUENTAS_ACTIVAS) {
                    if (cuentasActivas.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "No hay cuentas activas registradas.\nToca '+ Cuenta' para registrar una.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(cuentasActivas, key = { it.cuenta.id }) { item ->
                                CuentaActivaItem(
                                    item = item,
                                    onClick = { onCuentaClick(item.cuenta.id) }
                                )
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        OutlinedTextField(
                            value = busquedaVentas,
                            onValueChange = { busquedaVentas = it },
                            placeholder = { Text("Buscar venta por celular o servicio...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (busquedaVentas.isNotEmpty()) {
                                        IconButton(onClick = { busquedaVentas = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Borrar búsqueda")
                                        }
                                    }
                                    IconButton(onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clipText = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                                        if (clipText.isNotBlank()) {
                                            busquedaVentas = clipText.trim()
                                        }
                                    }) {
                                        Icon(Icons.Default.ContentPaste, contentDescription = "Pegar número o texto")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        if (ventasFiltradas.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    if (busquedaVentas.isBlank()) "No hay ventas registradas aún." else "No se encontraron ventas para '$busquedaVentas'.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(ventasFiltradas, key = { it.venta.id }) { item ->
                                    VentaConDiasItem(
                                        item = item,
                                        hoy = hoy,
                                        onEditarVenta = { ventaAEditar = item.venta },
                                        onEliminarVenta = { ventaAEliminar = item.venta },
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
                                                val clip = ClipData.newPlainText("Datos Cuenta", mensaje)
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
            }
        }
    }

    // ==========================================
    // 5. MODAL BOTTOM SHEETS ERGONÓMICOS
    // ==========================================

    // Sheet Nueva Venta
    if (mostrarSheetNuevaVenta) {
        NuevaVentaBottomSheet(
            viewModel = viewModel,
            onDismiss = { mostrarSheetNuevaVenta = false }
        )
    }

    // Sheet Nueva Cuenta
    if (mostrarSheetNuevaCuenta) {
        NuevaCuentaBottomSheet(
            viewModel = viewModel,
            onDismiss = { mostrarSheetNuevaCuenta = false }
        )
    }

    // Sheet Estadísticas
    if (mostrarSheetEstadisticas) {
        EstadisticasDialog(
            viewModel = viewModel,
            onDismiss = { mostrarSheetEstadisticas = false }
        )
    }

    // Sheet Copia de Seguridad
    if (mostrarSheetCopiaSeguridad) {
        CopiaSeguridadBottomSheet(
            onExportar = {
                exportarBackupLauncher.launch("backup_cuentas_${DateUtils.obtenerFechaHoy()}.json")
            },
            onImportar = {
                importarBackupLauncher.launch(arrayOf("application/json", "*/*"))
            },
            onRestaurarTexto = { text ->
                try {
                    val (cuentas, ventas) = com.gestorcuentas.app.utils.BackupUtils.parsearBackup(text)
                    resumenImportarCuentas = cuentas.size
                    resumenImportarVentas = ventas.size
                    jsonImportarPendiente = text
                } catch (e: Exception) {
                    Toast.makeText(context, "Error en el archivo de respaldo: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            },
            onDismiss = { mostrarSheetCopiaSeguridad = false }
        )
    }

    // Sheet Editar Venta
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

    // Confirmación Restaurar Backup
    jsonImportarPendiente?.let { jsonString ->
        AlertDialog(
            onDismissRequest = { jsonImportarPendiente = null },
            shape = RoundedCornerShape(16.dp),
            title = { Text("¿Restaurar Copia de Seguridad?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Se importarán $resumenImportarCuentas cuentas y $resumenImportarVentas ventas. La base de datos actual se actualizará con estos datos.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.restaurarBackup(
                            jsonString = jsonString,
                            onSuccess = { cuentasRest, ventasRest ->
                                Toast.makeText(context, "✅ Base de datos restaurada: $cuentasRest cuentas y $ventasRest ventas", Toast.LENGTH_LONG).show()
                                jsonImportarPendiente = null
                            },
                            onError = { errorMsg ->
                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                jsonImportarPendiente = null
                            }
                        )
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Restaurar")
                }
            },
            dismissButton = {
                TextButton(onClick = { jsonImportarPendiente = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Item unificado de la lista de Cuentas Activas con contador de ventas
 */
@Composable
fun CuentaActivaItem(
    item: CuentaConVentas,
    onClick: () -> Unit
) {
    val cuenta = item.cuenta
    val totalVentas = item.ventas.size
    val esRentable = item.esRentable
    val ganancia = item.gananciaNeta

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
                Text(
                    text = cuenta.tipoCuenta.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (totalVentas == 1) "1 venta" else "$totalVentas ventas",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(cuenta.correo, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Costo: ${DateUtils.formatearMoneda(cuenta.costoCompra)} | Recaudado: ${DateUtils.formatearMoneda(item.totalIngresos)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (esRentable) "+${DateUtils.formatearMoneda(ganancia)}" else "-${DateUtils.formatearMoneda(-ganancia)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (esRentable) AccentGreen else WarningAmber
                )
            }
        }
    }
}

/**
 * Item unificado de la lista de Todas las Ventas con DÍAS DISPONIBLES
 */
@Composable
fun VentaConDiasItem(
    item: VentaConCuenta,
    hoy: java.time.LocalDate = remember { java.time.LocalDate.now() },
    onEditarVenta: () -> Unit,
    onEliminarVenta: () -> Unit,
    onCopiarMensaje: () -> Unit
) {
    val venta = item.venta
    val cuenta = item.cuenta
    val diasRestantes = remember(venta.fechaFin, hoy) {
        DateUtils.calcularDiasRestantes(venta.fechaFin, hoy)
    }

    val (badgeColor, badgeText) = remember(diasRestantes) {
        when {
            diasRestantes < 0 -> DangerRed to "Vencida hace ${-diasRestantes}d"
            diasRestantes == 0L -> WarningAmber to "¡Vence Hoy! (0 días)"
            diasRestantes in 1..3 -> WarningAmber to "$diasRestantes días disponibles"
            else -> AccentGreen to "$diasRestantes días disponibles"
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
                Text(
                    text = cuenta?.tipoCuenta?.uppercase() ?: "SERVICIO",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Cliente: ${venta.numeroCelularCliente}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "${DateUtils.formatearFechaLegible(venta.fechaInicio)} al ${DateUtils.formatearFechaLegible(venta.fechaFin)} (${venta.duracionDias}d)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        DateUtils.formatearMoneda(venta.precioVentaPorPerfil),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AccentGreen
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilledTonalIconButton(
                        onClick = onCopiarMensaje,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Copiar Mensaje",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onEditarVenta,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar Venta",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onEliminarVenta,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar Venta",
                            tint = DangerRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
