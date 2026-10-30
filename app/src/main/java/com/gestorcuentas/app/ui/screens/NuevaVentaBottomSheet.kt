package com.gestorcuentas.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gestorcuentas.app.ui.theme.AccentGreen
import com.gestorcuentas.app.utils.DateUtils
import com.gestorcuentas.app.utils.MensajeEntregaUtils
import com.gestorcuentas.app.utils.PhoneUtils
import com.gestorcuentas.app.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaVentaBottomSheet(
    initialCuentaId: Int? = null,
    viewModel: InventarioViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val cuentasActivasConVentas by viewModel.cuentasActivasConVentas.collectAsState()
    val context = LocalContext.current

    var selectedCuentaItem by remember {
        mutableStateOf(cuentasActivasConVentas.find { it.cuenta.id == initialCuentaId })
    }

    LaunchedEffect(cuentasActivasConVentas, initialCuentaId) {
        if (selectedCuentaItem == null && initialCuentaId != null) {
            selectedCuentaItem = cuentasActivasConVentas.find { it.cuenta.id == initialCuentaId }
        } else if (selectedCuentaItem == null && cuentasActivasConVentas.isNotEmpty()) {
            selectedCuentaItem = cuentasActivasConVentas.first()
        }
    }

    var numeroCelular by remember { mutableStateOf("") }
    var precioPorPerfilTexto by remember { mutableStateOf("") }
    var duracionDiasTexto by remember { mutableStateOf("30") }
    var fechaInicio by remember { mutableStateOf(DateUtils.obtenerFechaHoy()) }
    var expandedDropdown by remember { mutableStateOf(false) }

    val opcionesDuracion = listOf(30, 60, 90)
    val duracionDias = duracionDiasTexto.toIntOrNull() ?: 0
    val precioVenta = precioPorPerfilTexto.toDoubleOrNull() ?: 0.0
    val fechaFinCalculada = remember(fechaInicio, duracionDias) {
        if (duracionDias > 0) DateUtils.calcularFechaFin(fechaInicio, duracionDias) else fechaInicio
    }

    val selectedCuenta = selectedCuentaItem?.cuenta
    val formularioValido = selectedCuenta != null &&
            numeroCelular.isNotBlank() &&
            precioVenta > 0.0 &&
            duracionDias > 0

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
                text = "Registrar Nueva Venta",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // 1. Selector de Cuenta Maestra
            Text("1. Seleccionar Cuenta del Inventario", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            if (cuentasActivasConVentas.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f))
                ) {
                    Text(
                        "No tienes cuentas activas disponibles. Registra una cuenta o habilita una inhabilitada antes de vender.",
                        modifier = Modifier.padding(14.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            } else {
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedCuentaItem?.let { item ->
                            "${item.cuenta.tipoCuenta} - ${item.cuenta.correo} • ${item.ventas.size} ventas"
                        } ?: "Seleccione una cuenta...",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        cuentasActivasConVentas.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            "${item.cuenta.tipoCuenta} • ${item.ventas.size} ventas registradas",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            item.cuenta.correo,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                },
                                onClick = {
                                    selectedCuentaItem = item
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // 2. Número de Celular del Cliente con Pegado Rápido
            Text("2. Datos del Cliente", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = numeroCelular,
                onValueChange = { numeroCelular = it },
                label = { Text("Número de Celular") },
                placeholder = { Text("Pega o escribe (+591 76992490)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (numeroCelular.isNotEmpty()) {
                            IconButton(onClick = { numeroCelular = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Borrar celular")
                            }
                        }
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipText = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                            if (clipText.isNotBlank()) {
                                val limpio = PhoneUtils.extraerDigitosSignificativos(clipText)
                                numeroCelular = if (limpio.isNotBlank()) limpio else clipText.trim()
                            }
                        }) {
                            Icon(Icons.Default.ContentPaste, contentDescription = "Pegar número de WhatsApp")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp)
            )

            // 3. Precio de Venta
            Text("3. Precio del Acceso", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = precioPorPerfilTexto,
                onValueChange = { precioPorPerfilTexto = it },
                label = { Text("Precio de Venta ($)") },
                placeholder = { Text("0.00") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(14.dp)
            )

            // 4. Duración (Chips en la zona del pulgar)
            Text("4. Duración del Acceso", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                opcionesDuracion.forEach { dias ->
                    FilterChip(
                        selected = duracionDiasTexto == dias.toString(),
                        onClick = { duracionDiasTexto = dias.toString() },
                        label = { Text("${dias} días", fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = duracionDiasTexto,
                    onValueChange = { duracionDiasTexto = it },
                    label = { Text("Días (Personalizado)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = fechaInicio,
                    onValueChange = { fechaInicio = it },
                    label = { Text("Fecha Inicio") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    shape = RoundedCornerShape(14.dp)
                )
            }

            // Card Resumen Cálculo Automático
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Fecha de Vencimiento:", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            fechaFinCalculada,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total a Cobrar:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            DateUtils.formatearMoneda(precioVenta),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AccentGreen
                        )
                    }
                }
            }

            // Botón de Confirmación Pinned al Fondo (Thumb Zone)
            Button(
                onClick = {
                    val cuenta = selectedCuenta
                    if (cuenta != null && formularioValido) {
                        viewModel.registrarVenta(
                            cuentaId = cuenta.id,
                            numeroCelularCliente = numeroCelular,
                            cantidadPerfiles = 1,
                            precioVentaPorPerfil = precioVenta,
                            duracionDias = duracionDias,
                            fechaInicio = fechaInicio,
                            onSuccess = {
                                val mensaje = MensajeEntregaUtils.generarMensaje(
                                    tipoCuenta = cuenta.tipoCuenta,
                                    duracionDias = duracionDias,
                                    correo = cuenta.correo,
                                    contrasenaPlataforma = cuenta.contrasenaPlataforma,
                                    fechaInicio = fechaInicio,
                                    fechaFin = fechaFinCalculada
                                )
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Datos Cuenta Cliente", mensaje)
                                clipboard.setPrimaryClip(clip)

                                Toast.makeText(
                                    context,
                                    "✅ Venta registrada y datos copiados al portapapeles",
                                    Toast.LENGTH_LONG
                                ).show()

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
                Text("Registrar Venta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}
