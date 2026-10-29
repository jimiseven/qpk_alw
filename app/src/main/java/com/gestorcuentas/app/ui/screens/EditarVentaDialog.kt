package com.gestorcuentas.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gestorcuentas.app.data.entity.Venta
import com.gestorcuentas.app.ui.theme.AccentGreen
import com.gestorcuentas.app.utils.DateUtils
import com.gestorcuentas.app.viewmodel.InventarioViewModel

@Composable
fun EditarVentaDialog(
    venta: Venta,
    viewModel: InventarioViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var numeroCelular by remember { mutableStateOf(venta.numeroCelularCliente) }
    var precioVentaTexto by remember { mutableStateOf(venta.precioVentaPorPerfil.toString()) }
    var duracionDiasTexto by remember { mutableStateOf(venta.duracionDias.toString()) }
    var fechaInicio by remember { mutableStateOf(venta.fechaInicio) }
    var fechaFin by remember { mutableStateOf(venta.fechaFin) }

    val duracionDias = duracionDiasTexto.toIntOrNull() ?: 0
    val precioVenta = precioVentaTexto.toDoubleOrNull() ?: 0.0

    // Si cambia los días o fecha de inicio, sugerir actualización de fecha fin
    LaunchedEffect(duracionDias, fechaInicio) {
        if (duracionDias > 0) {
            fechaFin = DateUtils.calcularFechaFin(fechaInicio, duracionDias)
        }
    }

    val formularioValido = numeroCelular.isNotBlank() &&
            precioVenta > 0.0 &&
            duracionDias > 0 &&
            fechaInicio.isNotBlank() &&
            fechaFin.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Venta de Perfil", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = numeroCelular,
                    onValueChange = { numeroCelular = it },
                    label = { Text("Número de Celular") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = precioVentaTexto,
                    onValueChange = { precioVentaTexto = it },
                    label = { Text("Precio de Venta ($)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = duracionDiasTexto,
                    onValueChange = { duracionDiasTexto = it },
                    label = { Text("Duración en Días") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Text("Fechas de Vigencia", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = fechaInicio,
                        onValueChange = { fechaInicio = it },
                        label = { Text("Fecha Inicio") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = fechaFin,
                        onValueChange = { fechaFin = it },
                        label = { Text("Fecha Fin") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Vigencia calculada: $fechaInicio al $fechaFin", style = MaterialTheme.typography.bodySmall)
                        Text("Total a cobrar: ${DateUtils.formatearMoneda(precioVenta)}", style = MaterialTheme.typography.bodySmall, color = AccentGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (formularioValido) {
                        viewModel.actualizarVenta(
                            ventaId = venta.id,
                            cuentaId = venta.cuentaId,
                            numeroCelularCliente = numeroCelular,
                            cantidadPerfiles = 1, // Siempre 1 perfil
                            precioVentaPorPerfil = precioVenta,
                            duracionDias = duracionDias,
                            fechaInicio = fechaInicio,
                            onSuccess = {
                                Toast.makeText(context, "Venta actualizada correctamente", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            }
                        )
                    }
                },
                enabled = formularioValido
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
