package com.gestorcuentas.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gestorcuentas.app.data.entity.Cuenta
import com.gestorcuentas.app.utils.DateUtils
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenovarCuentaDialog(
    cuenta: Cuenta,
    onDismiss: () -> Unit,
    onConfirmar: (dias: Int, costoRenovacion: Double) -> Unit
) {
    val hoy = remember { LocalDate.now() }
    val hoyStr = remember { DateUtils.obtenerFechaHoy() }

    val estaActiva = remember(cuenta.fechaFin, hoyStr) {
        cuenta.fechaFin.isNotBlank() && cuenta.fechaFin >= hoyStr
    }

    var duracionDias by remember { mutableStateOf(30) }
    var costoRenovacion by remember {
        // Sugerir el costo de compra previo como base editable
        mutableStateOf(cuenta.costoCompra.toString())
    }

    // Calcular en vivo la nueva fecha de vencimiento con el proveedor
    val nuevaFechaFin = remember(cuenta.fechaFin, duracionDias, estaActiva, hoyStr) {
        val fechaBase = if (estaActiva) cuenta.fechaFin else hoyStr
        DateUtils.calcularFechaFin(fechaBase, duracionDias)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver atrás"
                    )
                }
                Icon(
                    imageVector = Icons.Default.Autorenew,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text("Renovar con Proveedor", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Info de la cuenta
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "${cuenta.tipoCuenta} • ${cuenta.correo}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Vencimiento actual: ${if (cuenta.fechaFin.isNotBlank()) DateUtils.formatearFechaLegible(cuenta.fechaFin) else "Sin fecha"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Costo invertido hasta hoy: ${DateUtils.formatearMoneda(cuenta.costoCompra)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (cuenta.renovacionesCount > 0) {
                            Text(
                                text = "🔄 Meses renovados: ${cuenta.renovacionesCount} (Última: ${DateUtils.formatearFechaLegible(cuenta.ultimaRenovacion)})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Selector de Días
                Text("Período a Extender:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
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

                // Proyección de Nueva Fecha
                Card(
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Column {
                            Text("Nuevo Vencimiento Proveedor:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(DateUtils.formatearFechaLegible(nuevaFechaFin), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                // Costo de la Renovación (Permite mismo costo o superior)
                OutlinedTextField(
                    value = costoRenovacion,
                    onValueChange = { costoRenovacion = it },
                    label = { Text("Costo de esta Renovación (Bs.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    supportingText = {
                        Text("Puede ser igual o superior. Se sumará a los costos de la cuenta.")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val costo = costoRenovacion.toDoubleOrNull() ?: 0.0
                    onConfirmar(duracionDias, costo)
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Confirmar Renovación")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Volver atrás")
            }
        }
    )
}
