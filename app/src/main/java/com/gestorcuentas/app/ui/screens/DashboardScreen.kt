package com.gestorcuentas.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gestorcuentas.app.data.entity.CuentaConVentas
import com.gestorcuentas.app.data.entity.VentaConCuenta
import com.gestorcuentas.app.ui.theme.AccentGreen
import com.gestorcuentas.app.ui.theme.DangerRed
import com.gestorcuentas.app.ui.theme.WarningAmber
import com.gestorcuentas.app.utils.DateUtils
import com.gestorcuentas.app.utils.MensajeEntregaUtils
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
    onNavigateToNuevaVenta: () -> Unit,
    onNavigateToNuevaCuenta: () -> Unit
) {
    val resumen by viewModel.resumenFinanciero.collectAsState()
    val cuentasConVentas by viewModel.cuentasConVentas.collectAsState()
    val allVentasConCuenta by viewModel.allVentasConCuenta.collectAsState()
    val context = LocalContext.current

    // Estado de la Card seleccionada: Por defecto "Cuentas Activas"
    var selectedTab by remember { mutableStateOf(DashboardTab.CUENTAS_ACTIVAS) }

    // Filtrar cuentas activas
    val cuentasActivas = remember(cuentasConVentas) {
        cuentasConVentas.filter { it.cuenta.estado.equals("activa", ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Panel Principal",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = onNavigateToNuevaCuenta) {
                        Icon(Icons.Default.Add, contentDescription = "Nueva Cuenta")
                    }
                    IconButton(onClick = onNavigateToNuevaVenta) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Nueva Venta")
                    }
                }
            )
        }
    ) { padding ->
        // Contenedor principal sin scroll global para máximo rendimiento y fluidez
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // ==========================================
            // 1. BANNER FINANCIERO COMPACTO (FIJO)
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
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

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // 2. SELECTOR DE VISTA TIPO CARDS (FIJO)
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // CARD 1: Cuentas Activas (Seleccionada por defecto)
                val isCuentasSelected = selectedTab == DashboardTab.CUENTAS_ACTIVAS
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = DashboardTab.CUENTAS_ACTIVAS },
                    shape = RoundedCornerShape(12.dp),
                    border = if (isCuentasSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCuentasSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
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

                // CARD 2: Ventas
                val isVentasSelected = selectedTab == DashboardTab.TODAS_LAS_VENTAS
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = DashboardTab.TODAS_LAS_VENTAS },
                    shape = RoundedCornerShape(12.dp),
                    border = if (isVentasSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                    colors = CardDefaults.cardColors(
                        containerColor = if (isVentasSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
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
            // 3. LISTADO DINÁMICO (ÚNICO ÁREA CON SCROLL)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (selectedTab == DashboardTab.CUENTAS_ACTIVAS) {
                    // Vista A: Cuentas Activas con cuántas ventas por cuenta
                    if (cuentasActivas.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "No hay cuentas activas registradas.\nToca el botón + para agregar una.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 12.dp)
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
                    // Vista B: Todas las Ventas con sus DÍAS DISPONIBLES y botón para copiar mensaje
                    if (allVentasConCuenta.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "No hay ventas registradas aún.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 12.dp)
                        ) {
                            items(allVentasConCuenta, key = { it.venta.id }) { item ->
                                VentaConDiasItem(
                                    item = item,
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

/**
 * Item de la lista de Cuentas Activas con contador de ventas
 */
@Composable
fun CuentaActivaItem(
    item: CuentaConVentas,
    onClick: () -> Unit
) {
    val cuenta = item.cuenta
    val totalVentas = item.ventas.size
    val totalPerfiles = item.perfilesVendidosTotales
    val esRentable = item.esRentable
    val ganancia = item.gananciaNeta

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
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
                Text(
                    text = cuenta.tipoCuenta.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Badge de Ventas de esta cuenta
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (totalVentas == 1) "1 venta ($totalPerfiles perfil)" else "$totalVentas ventas ($totalPerfiles perfiles)",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(cuenta.correo, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(6.dp))
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
 * Item de la lista de Todas las Ventas con DÍAS DISPONIBLES y botón para copiar mensaje
 */
@Composable
fun VentaConDiasItem(
    item: VentaConCuenta,
    onCopiarMensaje: () -> Unit
) {
    val venta = item.venta
    val cuenta = item.cuenta
    val diasRestantes = DateUtils.calcularDiasRestantes(venta.fechaFin)

    val (badgeColor, badgeText) = when {
        diasRestantes < 0 -> DangerRed to "Vencida hace ${-diasRestantes}d"
        diasRestantes == 0L -> WarningAmber to "¡Vence Hoy! (0 días)"
        diasRestantes in 1..3 -> WarningAmber to "$diasRestantes días disponibles"
        else -> AccentGreen to "$diasRestantes días disponibles"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
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
                Text(
                    text = cuenta?.tipoCuenta?.uppercase() ?: "SERVICIO",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Badge de Días Disponibles
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
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
                        "${venta.cantidadPerfiles} perfil/es • ${DateUtils.formatearMoneda(item.totalVenta)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Botón Copiar Mensaje Cliente
                FilledTonalIconButton(
                    onClick = onCopiarMensaje,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copiar Mensaje",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
