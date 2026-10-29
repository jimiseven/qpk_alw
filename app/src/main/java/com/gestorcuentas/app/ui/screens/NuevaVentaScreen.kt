package com.gestorcuentas.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gestorcuentas.app.data.entity.Cuenta
import com.gestorcuentas.app.ui.theme.AccentGreen
import com.gestorcuentas.app.utils.DateUtils
import com.gestorcuentas.app.utils.MensajeEntregaUtils
import com.gestorcuentas.app.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaVentaScreen(
    initialCuentaId: Int? = null,
    viewModel: InventarioViewModel,
    onNavigateBack: () -> Unit
) {
    val cuentasActivas by viewModel.cuentasActivas.collectAsState()
    val context = LocalContext.current

    var selectedCuenta by remember {
        mutableStateOf(cuentasActivas.find { it.id == initialCuentaId })
    }

    LaunchedEffect(cuentasActivas, initialCuentaId) {
        if (selectedCuenta == null && initialCuentaId != null) {
            selectedCuenta = cuentasActivas.find { it.id == initialCuentaId }
        } else if (selectedCuenta == null && cuentasActivas.isNotEmpty()) {
            selectedCuenta = cuentasActivas.first()
        }
    }

    var numeroCelular by remember { mutableStateOf("") }
    var cantidadPerfilesTexto by remember { mutableStateOf("1") }
    var precioPorPerfilTexto by remember { mutableStateOf("") }
    var duracionDiasTexto by remember { mutableStateOf("30") }
    var fechaInicio by remember { mutableStateOf(DateUtils.obtenerFechaHoy()) }

    var expandedDropdown by remember { mutableStateOf(false) }

    // Estado del diálogo con el mensaje de entrega generado
    var mensajeEntregaGenerado by remember { mutableStateOf<String?>(null) }
    var mostrarDialogoMensaje by remember { mutableStateOf(false) }

    // Cálculos en tiempo real
    val duracionDias = duracionDiasTexto.toIntOrNull() ?: 0
    val cantidadPerfiles = cantidadPerfilesTexto.toIntOrNull() ?: 0
    val precioPorPerfil = precioPorPerfilTexto.toDoubleOrNull() ?: 0.0
    val totalCobro = cantidadPerfiles * precioPorPerfil
    val fechaFinCalculada = remember(fechaInicio, duracionDias) {
        if (duracionDias > 0) DateUtils.calcularFechaFin(fechaInicio, duracionDias) else fechaInicio
    }

    val formularioValido = selectedCuenta != null &&
            numeroCelular.isNotBlank() &&
            cantidadPerfiles > 0 &&
            precioPorPerfil > 0.0 &&
            duracionDias > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Nueva Venta", fontWeight = FontWeight.Bold) },
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
            Spacer(modifier = Modifier.height(2.dp))

            // 1. Selector de Cuenta Maestra
            Text("1. Seleccionar Cuenta del Inventario", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            if (cuentasActivas.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        "No tienes cuentas registradas en el inventario. Registra una cuenta antes de vender.",
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            } else {
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedCuenta?.let { "${it.tipoCuenta.uppercase()} - ${it.correo}" } ?: "Seleccione una cuenta...",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        cuentasActivas.forEach { cuenta ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(cuenta.tipoCuenta.uppercase(), fontWeight = FontWeight.Bold)
                                        Text(cuenta.correo, style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                onClick = {
                                    selectedCuenta = cuenta
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // 2. Número de Celular del Cliente
            Text("2. Datos del Cliente", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = numeroCelular,
                onValueChange = { numeroCelular = it },
                label = { Text("Número de Celular") },
                placeholder = { Text("Ej: +58 412 1234567") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(12.dp)
            )

            // 3. Cantidad y Precio
            Text("3. Perfiles y Cobro", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = cantidadPerfilesTexto,
                    onValueChange = { cantidadPerfilesTexto = it },
                    label = { Text("Cantidad Perfiles") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = precioPorPerfilTexto,
                    onValueChange = { precioPorPerfilTexto = it },
                    label = { Text("Precio x Perfil ($)") },
                    placeholder = { Text("0.00") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // 4. Duración y Fecha de Vencimiento
            Text("4. Duración del Acceso", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(7, 15, 30, 60, 90).forEach { dias ->
                    FilterChip(
                        selected = duracionDiasTexto == dias.toString(),
                        onClick = { duracionDiasTexto = dias.toString() },
                        label = { Text("${dias}d") }
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
                    label = { Text("Duración (Días)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = fechaInicio,
                    onValueChange = { fechaInicio = it },
                    label = { Text("Fecha Inicio") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Card Informativo del Cálculo Automático
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
                            DateUtils.formatearMoneda(totalCobro),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AccentGreen
                        )
                    }
                }
            }

            // Botón de Guardar
            Button(
                onClick = {
                    val cuenta = selectedCuenta
                    if (cuenta != null && formularioValido) {
                        viewModel.registrarVenta(
                            cuentaId = cuenta.id,
                            numeroCelularCliente = numeroCelular,
                            cantidadPerfiles = cantidadPerfiles,
                            precioVentaPorPerfil = precioPorPerfil,
                            duracionDias = duracionDias,
                            fechaInicio = fechaInicio,
                            onSuccess = {
                                // Generar mensaje formateado para el cliente
                                val mensaje = MensajeEntregaUtils.generarMensaje(
                                    tipoCuenta = cuenta.tipoCuenta,
                                    duracionDias = duracionDias,
                                    correo = cuenta.correo,
                                    contrasenaPlataforma = cuenta.contrasenaPlataforma,
                                    fechaInicio = fechaInicio,
                                    fechaFin = fechaFinCalculada
                                )
                                mensajeEntregaGenerado = mensaje
                                mostrarDialogoMensaje = true
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
                Text("Registrar Venta", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ==========================================
    // DIÁLOGO CON EL MENSAJE GENERADO
    // ==========================================
    if (mostrarDialogoMensaje && mensajeEntregaGenerado != null) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoMensaje = false
                onNavigateBack()
            },
            title = {
                Text("¡Venta Registrada!", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                ) {
                    Text(
                        "Mensaje generado para el cliente:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = mensajeEntregaGenerado!!,
                            modifier = Modifier.padding(10.dp),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Datos Cuenta Cliente", mensajeEntregaGenerado)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "¡Mensaje copiado al portapapeles!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copiar Mensaje")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoMensaje = false
                        onNavigateBack()
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}
