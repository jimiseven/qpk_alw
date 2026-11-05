package com.gestorcuentas.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gestorcuentas.app.data.entity.VentaConCuenta
import com.gestorcuentas.app.ui.theme.AccentGreen
import com.gestorcuentas.app.ui.theme.WarningAmber
import com.gestorcuentas.app.utils.DateUtils
import com.gestorcuentas.app.utils.PhoneUtils
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenovarVentaBottomSheet(
    ventaConCuenta: VentaConCuenta,
    onDismiss: () -> Unit,
    onConfirmar: (dias: Int, montoCobrado: Double) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val venta = ventaConCuenta.venta
    val cuenta = ventaConCuenta.cuenta

    val hoy = remember { LocalDate.now() }
    val hoyStr = remember { DateUtils.obtenerFechaHoy() }

    val estaActivo = remember(venta.fechaFin, hoyStr) {
        venta.fechaFin >= hoyStr
    }

    val diasRestantes = remember(venta.fechaFin, hoy) {
        DateUtils.calcularDiasRestantes(venta.fechaFin, hoy)
    }

    var duracionDias by remember { mutableStateOf(30) }
    var precioCobrado by remember {
        // Sugerir el precio original por perfil o precio proporcional
        mutableStateOf(venta.precioVentaPorPerfil.toString())
    }

    // Calcular en vivo la nueva fecha de vencimiento
    val nuevaFechaFin = remember(venta.fechaFin, duracionDias, estaActivo, hoyStr) {
        val fechaBase = if (estaActivo) venta.fechaFin else hoyStr
        DateUtils.calcularFechaFin(fechaBase, duracionDias)
    }

    // Comprobar si el nuevo vencimiento del cliente supera a la cuenta maestra
    val superaCuentaMaestra = remember(cuenta?.fechaFin, nuevaFechaFin) {
        val fFinCuenta = cuenta?.fechaFin ?: ""
        fFinCuenta.isNotBlank() && fFinCuenta < nuevaFechaFin
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
                .padding(bottom = 28.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Título y Cabecera
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Autorenew,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = "Renovar Servicio",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Cliente: ${venta.numeroCelularCliente}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Ficha de Cuenta Asignada y Estado Actual
            Card(
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Cuenta: ${cuenta?.tipoCuenta ?: "Servicio"} • ${cuenta?.correo ?: "Sin correo"}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Vencimiento actual: ${DateUtils.formatearFechaLegible(venta.fechaFin)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (estaActivo) AccentGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (estaActivo) "Activo ($diasRestantes d)" else "Vencido hace ${-diasRestantes} d",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (estaActivo) AccentGreen else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (venta.renovacionesCount > 0) {
                        Text(
                            text = "🔄 Ha renovado ${venta.renovacionesCount} ${if (venta.renovacionesCount == 1) "vez" else "veces"} (Última: ${DateUtils.formatearFechaLegible(venta.ultimaRenovacion)})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Selector de Duración de Renovación
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Período a Extender:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(30, 60, 90).forEach { dias ->
                        val isSelected = duracionDias == dias
                        FilterChip(
                            selected = isSelected,
                            onClick = { duracionDias = dias },
                            label = { Text("$dias días", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Proyección del Nuevo Vencimiento
            Card(
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Nuevo Vencimiento: ${DateUtils.formatearFechaLegible(nuevaFechaFin)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (estaActivo) {
                                "Se sumaron $duracionDias días a partir de su fecha fin actual."
                            } else {
                                "El cliente estaba vencido. Su nuevo ciclo arranca hoy ($duracionDias días)."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Alerta si supera la cuenta maestra del proveedor
            if (superaCuentaMaestra) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, WarningAmber),
                    colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(alpha = 0.12f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = WarningAmber
                        )
                        Text(
                            text = "Atención: La Cuenta Maestra con tu proveedor vence el ${DateUtils.formatearFechaLegible(cuenta?.fechaFin ?: "")}. Recuerda renovar la cuenta maestra para cubrir este período.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Campo para Precio Cobrado por esta Renovación
            OutlinedTextField(
                value = precioCobrado,
                onValueChange = { precioCobrado = it },
                label = { Text("Precio Cobrado por esta Renovación (Bs.)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Botones de Acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        val precio = precioCobrado.toDoubleOrNull() ?: 0.0
                        onConfirmar(duracionDias, precio)
                    },
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Autorenew, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Renovar y Copiar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
