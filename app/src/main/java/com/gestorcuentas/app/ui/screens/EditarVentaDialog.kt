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
    var cantidadPerfilesTexto by remember { mutableStateOf(venta.cantidadPerfiles.toString()) }
    var precioPorPerfilTexto by remember { mutableStateOf(venta.precioVentaPorPerfil.toString()) }
    var duracionDiasTexto by remember { mutableStateOf(venta.duracionDias.toString()) }
    var fechaInicio by remember { mutableStateOf(venta.fechaInicio) }

    val duracionDias = duracionDiasTexto.toIntOrNull() ?: 0
    val cantidadPerfiles = cantidadPerfilesTexto.toIntOrNull() ?: 0
    val precioPorPerfil = precioPorPerfilTexto.toDoubleOrNull() ?: 0.0
    val totalCobro = cantidadPerfiles * precioPorPerfil
    val fechaFinCalculada = remember(fechaInicio, duracionDias) {
        if (duracionDias > 0) DateUtils.calcularFechaFin(fechaInicio, duracionDias) else fechaInicio
    }

    val formularioValido = numeroCelular.isNotBlank() &&
            cantidadPerfiles > 0 &&
            precioPorPerfil > 0.0 &&
            duracionDias > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Venta", fontWeight = FontWeight.Bold) },
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = cantidadPerfilesTexto,
                        onValueChange = { cantidadPerfilesTexto = it },
                        label = { Text("Perfiles") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = precioPorPerfilTexto,
                        onValueChange = { precioPorPerfilTexto = it },
                        label = { Text("Precio c/u ($)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = duracionDiasTexto,
                        onValueChange = { duracionDiasTexto = it },
                        label = { Text("Días") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = fechaInicio,
                        onValueChange = { fechaInicio = it },
                        label = { Text("Fecha Inicio") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Nueva fecha fin: $fechaFinCalculada", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text("Nuevo cobro: ${DateUtils.formatearMoneda(totalCobro)}", style = MaterialTheme.typography.bodySmall, color = AccentGreen, fontWeight = FontWeight.Bold)
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
                            cantidadPerfiles = cantidadPerfiles,
                            precioVentaPorPerfil = precioPorPerfil,
                            duracionDias = duracionDias,
                            fechaInicio = fechaInicio,
                            onSuccess = {
                                Toast.makeText(context, "Venta actualizada", Toast.LENGTH_SHORT).show()
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
