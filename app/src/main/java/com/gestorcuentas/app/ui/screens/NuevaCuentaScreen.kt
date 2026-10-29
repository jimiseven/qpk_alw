package com.gestorcuentas.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun NuevaCuentaScreen(
    viewModel: InventarioViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    val opcionesServicio = listOf("Chat GPT", "Gemini", "Otros")
    var opcionSeleccionada by remember { mutableStateOf("Chat GPT") }
    var otroServicioTexto by remember { mutableStateOf("") }

    var correo by remember { mutableStateOf("") }
    var contrasenaCorreo by remember { mutableStateOf("") }
    var contrasenaPlataforma by remember { mutableStateOf("") }
    var costoCompraTexto by remember { mutableStateOf("") }
    var fechaCreacion by remember { mutableStateOf(DateUtils.obtenerFechaHoy()) }

    val tipoCuentaFinal = if (opcionSeleccionada == "Otros") otroServicioTexto.trim() else opcionSeleccionada

    val costoCompra = costoCompraTexto.toDoubleOrNull() ?: 0.0
    val formularioValido = tipoCuentaFinal.isNotBlank() &&
            correo.isNotBlank() &&
            contrasenaPlataforma.isNotBlank() &&
            costoCompraTexto.isNotBlank() &&
            costoCompra >= 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Cuenta Maestra", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Text("Tipo de Servicio", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            // Selector de los 3 tipos requeridos: Chat GPT, Gemini, Otros
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
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Text("Credenciales de la Cuenta", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo Electrónico") },
                placeholder = { Text("cuenta@correo.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = contrasenaPlataforma,
                onValueChange = { contrasenaPlataforma = it },
                label = { Text("Contraseña de la Plataforma") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = contrasenaCorreo,
                onValueChange = { contrasenaCorreo = it },
                label = { Text("Contraseña del Correo (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Text("Detalles de Adquisición", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = costoCompraTexto,
                onValueChange = { costoCompraTexto = it },
                label = { Text("Costo Total de Compra ($)") },
                placeholder = { Text("0.00") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = fechaCreacion,
                onValueChange = { fechaCreacion = it },
                label = { Text("Fecha de Adquisición (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

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
                            onSuccess = {
                                Toast.makeText(context, "Cuenta agregada al inventario", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            }
                        )
                    }
                },
                enabled = formularioValido,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar Cuenta Maestra", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
