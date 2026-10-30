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
import com.gestorcuentas.app.ui.components.CampoFechaPicker
import com.gestorcuentas.app.utils.DateUtils
import com.gestorcuentas.app.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarVentaDialog(
    venta: Venta,
    viewModel: InventarioViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    var numeroCelular by remember { mutableStateOf(venta.numeroCelularCliente) }
    var precioVentaTexto by remember { mutableStateOf(venta.precioVentaPorPerfil.toString()) }
    var duracionDiasTexto by remember { mutableStateOf(venta.duracionDias.toString()) }
    var fechaInicio by remember { mutableStateOf(venta.fechaInicio) }
    var fechaFin by remember { mutableStateOf(venta.fechaFin) }

    val duracionDias = duracionDiasTexto.toIntOrNull() ?: 0
    val precioVenta = precioVentaTexto.toDoubleOrNull() ?: 0.0

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
                text = "Editar Venta de Perfil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = numeroCelular,
                onValueChange = { numeroCelular = it },
                label = { Text("Número de Celular") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            OutlinedTextField(
                value = precioVentaTexto,
                onValueChange = { precioVentaTexto = it },
                label = { Text("Precio de Venta ($)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            OutlinedTextField(
                value = duracionDiasTexto,
                onValueChange = { duracionDiasTexto = it },
                label = { Text("Duración en Días") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Text("Fechas de Vigencia", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CampoFechaPicker(
                    label = "Fecha Inicio",
                    fechaIso = fechaInicio,
                    onFechaChange = { nuevaFecha ->
                        fechaInicio = nuevaFecha
                        if (duracionDias > 0) {
                            fechaFin = DateUtils.calcularFechaFin(nuevaFecha, duracionDias)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                CampoFechaPicker(
                    label = "Fecha Fin",
                    fechaIso = fechaFin,
                    onFechaChange = { fechaFin = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Botón de Confirmación en la Zona del Pulgar
            Button(
                onClick = {
                    if (formularioValido) {
                        viewModel.actualizarVenta(
                            ventaId = venta.id,
                            cuentaId = venta.cuentaId,
                            numeroCelularCliente = numeroCelular,
                            cantidadPerfiles = venta.cantidadPerfiles,
                            precioVentaPorPerfil = precioVenta,
                            duracionDias = duracionDias,
                            fechaInicio = fechaInicio,
                            fechaFin = fechaFin,
                            onSuccess = {
                                Toast.makeText(context, "✅ Venta actualizada correctamente", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            }
                        )
                    }
                },
                enabled = formularioValido,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Guardar Cambios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}
