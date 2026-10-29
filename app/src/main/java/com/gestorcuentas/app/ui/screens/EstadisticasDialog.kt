package com.gestorcuentas.app.ui.screens

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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gestorcuentas.app.data.entity.CuentaConVentas
import com.gestorcuentas.app.data.entity.VentaConCuenta
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

@Composable
fun EstadisticasDialog(
    viewModel: InventarioViewModel,
    onDismiss: () -> Unit
) {
    val allVentasConCuenta by viewModel.allVentasConCuenta.collectAsState()
    val cuentasConVentas by viewModel.cuentasConVentas.collectAsState()

    var modo by remember { mutableStateOf(ModoEstadistica.POR_MES) } // Por defecto "Por Mes"

    // Estado para "Por Mes": Mes actual como base
    var mesSeleccionado by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }

    // Estado para "Rango de Fechas"
    var fechaDesde by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1).format(DateTimeFormatter.ISO_LOCAL_DATE)) }
    var fechaHasta by remember { mutableStateOf(DateUtils.obtenerFechaHoy()) }

    val nombresMeses = arrayOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    // Filtro de ventas y cuentas según el modo
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

    // Cálculos estadísticos
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

    // Desglose por servicio
    val ventasPorServicio = remember(ventasFiltradas) {
        ventasFiltradas.groupBy { it.cuenta?.tipoCuenta?.uppercase() ?: "OTROS" }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Cabecera del diálogo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Estadísticas del Negocio",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Text("✕", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selector de Modo: Por Mes (Predeterminado) vs Rango de Fechas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = modo == ModoEstadistica.POR_MES,
                        onClick = { modo = ModoEstadistica.POR_MES },
                        label = { Text("Por Mes (Defecto)") },
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = modo == ModoEstadistica.RANGO_FECHAS,
                        onClick = { modo = ModoEstadistica.RANGO_FECHAS },
                        label = { Text("Rango de Fechas") },
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Controles de fecha según el modo
                if (modo == ModoEstadistica.POR_MES) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
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
                        OutlinedTextField(
                            value = fechaDesde,
                            onValueChange = { fechaDesde = it },
                            label = { Text("Desde") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = fechaHasta,
                            onValueChange = { fechaHasta = it },
                            label = { Text("Hasta") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Métricas y Resultados
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card Principal de Ganancia Neta
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("UTILIDAD NETA DEL PERIODO", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = DateUtils.formatearMoneda(gananciaNeta),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (gananciaNeta >= 0) AccentGreen else DangerRed
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Ingresos:", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        DateUtils.formatearMoneda(totalIngresos),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentGreen
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Costos Cuentas:", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        DateUtils.formatearMoneda(totalCostos),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Resumen de Volumen
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Ventas Realizadas", style = MaterialTheme.typography.labelSmall)
                                Text("${ventasFiltradas.size}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Clientes Únicos", style = MaterialTheme.typography.labelSmall)
                                Text("$clientesUnicos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Desglose por Servicio
                    Text("Desglose por Servicio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    if (ventasPorServicio.isEmpty()) {
                        Text(
                            "No se registraron ventas en este periodo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        ventasPorServicio.forEach { (servicio, ventasDelServicio) ->
                            val recaudadoServicio = ventasDelServicio.sumOf { it.venta.precioVentaPorPerfil }
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(servicio, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Text("${ventasDelServicio.size} ventas", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text(
                                        DateUtils.formatearMoneda(recaudadoServicio),
                                        fontWeight = FontWeight.Bold,
                                        color = AccentGreen
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
