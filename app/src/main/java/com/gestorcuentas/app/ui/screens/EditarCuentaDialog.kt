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
import com.gestorcuentas.app.viewmodel.InventarioViewModel

@Composable
fun EditarCuentaDialog(
    cuenta: Cuenta,
    viewModel: InventarioViewModel,
    onDismiss: () -> Unit
) {
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
    var estado by remember { mutableStateOf(cuenta.estado) } // "activa" o "inhabilitada"

    val tipoCuentaFinal = if (opcionSeleccionada == "Otros") otroServicioTexto.trim() else opcionSeleccionada
    val costoCompra = costoCompraTexto.toDoubleOrNull() ?: 0.0

    val formularioValido = tipoCuentaFinal.isNotBlank() &&
            correo.isNotBlank() &&
            contrasenaPlataforma.isNotBlank() &&
            costoCompra >= 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Cuenta Maestra", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Tipo de Servicio", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    opcionesServicio.forEach { opcion ->
                        FilterChip(
                            selected = opcionSeleccionada == opcion,
                            onClick = { opcionSeleccionada = opcion },
                            label = { Text(opcion, style = MaterialTheme.typography.bodySmall) }
                        )
                    }
                }

                if (opcionSeleccionada == "Otros") {
                    OutlinedTextField(
                        value = otroServicioTexto,
                        onValueChange = { otroServicioTexto = it },
                        label = { Text("Nombre del Servicio") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = contrasenaPlataforma,
                    onValueChange = { contrasenaPlataforma = it },
                    label = { Text("Contraseña Plataforma") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = contrasenaCorreo,
                    onValueChange = { contrasenaCorreo = it },
                    label = { Text("Contraseña Correo (Opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = costoCompraTexto,
                    onValueChange = { costoCompraTexto = it },
                    label = { Text("Costo de Compra ($)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                // Estado de la cuenta: Activa vs Inhabilitada
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
            }
        },
        confirmButton = {
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
                                estado = estado
                            ),
                            onSuccess = {
                                Toast.makeText(context, "Cuenta actualizada correctamente", Toast.LENGTH_SHORT).show()
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
