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
import com.gestorcuentas.app.data.entity.Cuenta
import com.gestorcuentas.app.ui.theme.DangerRed
import com.gestorcuentas.app.utils.DateUtils
import com.gestorcuentas.app.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarCuentaDialog(
    cuenta: Cuenta,
    viewModel: InventarioViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    val opcionesServicio = listOf("Chat GPT", "Gemini", "Otros")
    var opcionSeleccionada by remember {
        mutableStateOf(if (cuenta.tipoCuenta in listOf("Chat GPT", "Gemini")) cuenta.tipoCuenta else "Otros")
    }
    var otroServicioTexto by remember {
        mutableStateOf(if (cuenta.tipoCuenta in listOf("Chat GPT", "Gemini")) "" else cuenta.tipoCuenta)
    }

    var correo by remember { mutableStateOf(cuenta.correo) }
    var contrasenaPlataforma by remember { mutableStateOf(cuenta.contrasenaPlataforma) }
    var contrasenaCorreo by remember { mutableStateOf(cuenta.contrasenaCorreo) }
    var costoCompraTexto by remember { mutableStateOf(cuenta.costoCompra.toString()) }
    var fechaInicio by remember { mutableStateOf(cuenta.fechaCreacion) }
    var fechaFin by remember {
        mutableStateOf(
            if (cuenta.fechaFin.isNotBlank()) cuenta.fechaFin
            else DateUtils.calcularFechaFin(cuenta.fechaCreacion, 30)
        )
    }
    var estado by remember { mutableStateOf(cuenta.estado) }

    val tipoCuentaFinal = if (opcionSeleccionada == "Otros") otroServicioTexto.trim() else opcionSeleccionada
    val costoCompra = costoCompraTexto.toDoubleOrNull() ?: 0.0

    val formularioValido = tipoCuentaFinal.isNotBlank() &&
            correo.isNotBlank() &&
            contrasenaPlataforma.isNotBlank() &&
            fechaInicio.isNotBlank() &&
            costoCompra >= 0.0

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
                text = "Editar Cuenta Maestra",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text("Tipo de Servicio", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                opcionesServicio.forEach { opcion ->
                    FilterChip(
                        selected = opcionSeleccionada == opcion,
                        onClick = { opcionSeleccionada = opcion },
                        label = { Text(opcion, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            if (opcionSeleccionada == "Otros") {
                OutlinedTextField(
                    value = otroServicioTexto,
                    onValueChange = { otroServicioTexto = it },
                    label = { Text("Nombre del Servicio") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
            }

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(14.dp)
            )

            OutlinedTextField(
                value = contrasenaPlataforma,
                onValueChange = { contrasenaPlataforma = it },
                label = { Text("Contraseña de la Plataforma") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            OutlinedTextField(
                value = contrasenaCorreo,
                onValueChange = { contrasenaCorreo = it },
                label = { Text("Contraseña del Correo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            OutlinedTextField(
                value = costoCompraTexto,
                onValueChange = { costoCompraTexto = it },
                label = { Text("Costo de Compra ($)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = fechaInicio,
                    onValueChange = { fechaInicio = it },
                    label = { Text("Fecha Inicio") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = fechaFin,
                    onValueChange = { fechaFin = it },
                    label = { Text("Fecha Fin") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            Text("Estado de Disponibilidad", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = estado == "activa",
                    onClick = { estado = "activa" },
                    label = { Text("✅ Activa") }
                )

                FilterChip(
                    selected = estado == "inhabilitada",
                    onClick = { estado = "inhabilitada" },
                    label = { Text("🔴 Inhabilitada", color = if (estado == "inhabilitada") DangerRed else MaterialTheme.colorScheme.onSurface) }
                )
            }

            if (estado == "inhabilitada") {
                Text(
                    "⚠️ Esta cuenta NO estará disponible para registrar nuevas ventas de perfiles.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DangerRed
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Botón de Confirmación en la Zona del Pulgar
            Button(
                onClick = {
                    if (formularioValido) {
                        viewModel.actualizarCuenta(
                            cuenta.copy(
                                tipoCuenta = tipoCuentaFinal,
                                correo = correo.trim(),
                                contrasenaPlataforma = contrasenaPlataforma.trim(),
                                contrasenaCorreo = contrasenaCorreo.trim(),
                                costoCompra = costoCompra,
                                fechaCreacion = fechaInicio.trim(),
                                fechaFin = fechaFin.trim(),
                                estado = estado
                            ),
                            onSuccess = {
                                Toast.makeText(context, "✅ Cuenta actualizada correctamente", Toast.LENGTH_SHORT).show()
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
