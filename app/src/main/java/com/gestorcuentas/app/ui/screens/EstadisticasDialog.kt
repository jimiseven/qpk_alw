package com.gestorcuentas.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gestorcuentas.app.ui.components.CampoFechaPicker
import com.gestorcuentas.app.ui.theme.AccentGreen
import com.gestorcuentas.app.ui.theme.DangerRed
import com.gestorcuentas.app.utils.DateUtils
import com.gestorcuentas.app.viewmodel.InventarioViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class ModoEstadistica {
    POR_MES,
    RANGO_FECHAS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasDialog(
    viewModel: InventarioViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val allVentasConCuenta by viewModel.allVentasConCuenta.collectAsState()
    val cuentasConVentas by viewModel.cuentasConVentas.collectAsState()

    var modo by remember { mutableStateOf(ModoEstadistica.POR_MES) }
    var mesSeleccionado by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    var fechaDesde by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1).format(DateTimeFormatter.ISO_LOCAL_DATE)) }
    var fechaHasta by remember { mutableStateOf(DateUtils.obtenerFechaHoy()) }

    val nombresMeses = arrayOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    val (ventasFiltradas, cuentasFiltradas) = remember(modo, mesSeleccionado, fechaDesde, fechaHasta, allVentasConCuenta, cuentasConVentas) {
        if (modo == ModoEstadistica.POR_MES) {
            val prefijoMes = mesSeleccionado.format(DateTimeFormatter.ofPattern("yyyy-MM", Locale.US))
            val v = allVentasConCuenta.filter { it.venta.fechaInicio.startsWith(prefijoMes) }
            val c = cuentasConVentas.map { it.cuenta }.filter { it.fechaCreacion.startsWith(prefijoMes) }
            v to c
        } else {
            val v = allVentasConCuenta.filter { it.venta.fechaInicio >= fechaDesde && it.venta.fechaInicio <= fechaHasta }
            val c = cuentasConVentas.map { it.cuenta }.filter { it.fechaCreacion >= fechaDesde && it.fechaCreacion <= fechaHasta }
            v to c
        }
    }

    val totalIngresos = remember(ventasFiltradas) {
        ventasFiltradas.sumOf { it.venta.precioVentaPorPerfil }
    }
    val totalCostos = remember(cuentasFiltradas) {
        cuentasFiltradas.sumOf { it.costoCompra }
    }
    val gananciaNeta = totalIngresos - totalCostos
    val clientesUnicos = remember(ventasFiltradas) {
        ventasFiltradas.map { it.venta.numeroCelularCliente }.distinct().size
    }
    val ventasPorServicio = remember(ventasFiltradas) {
        ventasFiltradas.groupBy { it.cuenta?.tipoCuenta?.uppercase() ?: "OTROS" }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "Estadísticas del Negocio",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Selector de Modo Simétrico: Por Mes vs Rango de Fechas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val esMes = modo == ModoEstadistica.POR_MES
                Surface(
                    onClick = { modo = ModoEstadistica.POR_MES },
                    shape = RoundedCornerShape(12.dp),
                    color = if (esMes) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = if (esMes) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = if (esMes) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Por Mes",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (esMes) FontWeight.Bold else FontWeight.Medium,
                            color = if (esMes) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val esRango = modo == ModoEstadistica.RANGO_FECHAS
                Surface(
                    onClick = { modo = ModoEstadistica.RANGO_FECHAS },
                    shape = RoundedCornerShape(12.dp),
                    color = if (esRango) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = if (esRango) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = if (esRango) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Rango Fechas",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (esRango) FontWeight.Bold else FontWeight.Medium,
                            color = if (esRango) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (modo == ModoEstadistica.POR_MES) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { mesSeleccionado = mesSeleccionado.minusMonths(1) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mes Anterior")
                        }

                        Text(
                            text = "${nombresMeses[mesSeleccionado.monthValue - 1]} ${mesSeleccionado.year}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        IconButton(onClick = { mesSeleccionado = mesSeleccionado.plusMonths(1) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Mes Siguiente")
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoFechaPicker(
                        label = "Desde",
                        fechaIso = fechaDesde,
                        onFechaChange = { fechaDesde = it },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    CampoFechaPicker(
                        label = "Hasta",
                        fechaIso = fechaHasta,
                        onFechaChange = { fechaHasta = it },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Cards de Resumen Financiero
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Ingresos", style = MaterialTheme.typography.labelSmall)
                        Text(
                            DateUtils.formatearMoneda(totalIngresos),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentGreen
                        )
                        Text("${ventasFiltradas.size} ventas", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Costos", style = MaterialTheme.typography.labelSmall)
                        Text(
                            DateUtils.formatearMoneda(totalCostos),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text("${cuentasFiltradas.size} compras", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (gananciaNeta >= 0) AccentGreen.copy(alpha = 0.12f) else DangerRed.copy(alpha = 0.12f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Utilidad Neta del Período", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = DateUtils.formatearMoneda(gananciaNeta),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (gananciaNeta >= 0) AccentGreen else DangerRed
                        )
                    }
                    Text(
                        text = "$clientesUnicos clientes",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Desglose por Servicio
            if (ventasPorServicio.isNotEmpty()) {
                Text("Ventas por Servicio", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                ventasPorServicio.forEach { (servicio, ventas) ->
                    val totalServicio = ventas.sumOf { it.venta.precioVentaPorPerfil }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(servicio, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "${ventas.size} ventas • ${DateUtils.formatearMoneda(totalServicio)}",
                                fontWeight = FontWeight.Bold,
                                color = AccentGreen
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cerrar")
            }
        }
    }
}
