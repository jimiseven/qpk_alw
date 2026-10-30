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
import com.gestorcuentas.app.utils.DateUtils
import com.gestorcuentas.app.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaCuentaBottomSheet(
    viewModel: InventarioViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    val opcionesServicio = listOf("Chat GPT", "Gemini", "Otros")
    var opcionSeleccionada by remember { mutableStateOf("Chat GPT") }
    var otroServicioTexto by remember { mutableStateOf("") }

    var correo by remember { mutableStateOf("") }
    var contrasenaCorreo by remember { mutableStateOf("") }
    var contrasenaPlataforma by remember { mutableStateOf("") }
    var costoCompraTexto by remember { mutableStateOf("") }
    var fechaCreacion by remember { mutableStateOf(DateUtils.obtenerFechaHoy()) }
    var fechaFin by remember { mutableStateOf(DateUtils.obtenerFechaFutura(30)) }

    val tipoCuentaFinal = if (opcionSeleccionada == "Otros") otroServicioTexto.trim() else opcionSeleccionada
    val costoCompra = costoCompraTexto.toDoubleOrNull() ?: 0.0
    val formularioValido = tipoCuentaFinal.isNotBlank() &&
            correo.isNotBlank() &&
            contrasenaPlataforma.isNotBlank() &&
            costoCompraTexto.isNotBlank() &&
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
                text = "Registrar Cuenta Maestra",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text("Tipo de Servicio", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            // Selector con chips ergonómicos en la zona del pulgar
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
                    label = { Text("Especificar Servicio") },
                    placeholder = { Text("Ej: Netflix, Canva, Spotify...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
            }

            Text("Credenciales de Acceso", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo Electrónico") },
                placeholder = { Text("ejemplo@correo.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(14.dp)
            )

            OutlinedTextField(
                value = contrasenaPlataforma,
                onValueChange = { contrasenaPlataforma = it },
                label = { Text("Contraseña Plataforma ($tipoCuentaFinal)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            OutlinedTextField(
                value = contrasenaCorreo,
                onValueChange = { contrasenaCorreo = it },
                label = { Text("Contraseña del Correo (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Text("Costos y Fechas", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = costoCompraTexto,
                onValueChange = { costoCompraTexto = it },
                label = { Text("Costo de Compra ($)") },
                placeholder = { Text("0.00") },
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
                    value = fechaCreacion,
                    onValueChange = { fechaCreacion = it },
                    label = { Text("Fecha Inicio") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = fechaFin,
                    onValueChange = { fechaFin = it },
                    label = { Text("Fecha Fin") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Botón de Confirmación al Fondo (Thumb Zone)
            Button(
                onClick = {
                    if (formularioValido) {
                        viewModel.registrarCuenta(
                            correo = correo,
                            contrasenaCorreo = contrasenaCorreo,
                            contrasenaPlataforma = contrasenaPlataforma,
                            tipoCuenta = tipoCuentaFinal,
                            costoCompra = costoCompra,
                            fechaCreacion = fechaCreacion,
                            fechaFin = fechaFin,
                            onSuccess = {
                                Toast.makeText(context, "✅ Cuenta agregada al inventario", Toast.LENGTH_SHORT).show()
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
                Text("Guardar Cuenta Maestra", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}
