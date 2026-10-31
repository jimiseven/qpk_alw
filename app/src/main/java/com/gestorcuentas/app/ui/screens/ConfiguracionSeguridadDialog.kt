package com.gestorcuentas.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.gestorcuentas.app.R
import com.gestorcuentas.app.utils.AppPreferences
import com.gestorcuentas.app.utils.AppThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionSeguridadDialog(
    preferences: AppPreferences,
    onDismiss: () -> Unit,
    onThemeChanged: (AppThemeMode) -> Unit
) {
    val context = LocalContext.current

    var currentTheme by remember { mutableStateOf(preferences.themeMode) }
    var securityEnabled by remember { mutableStateOf(preferences.isSecurityEnabled) }
    var pinValue by remember { mutableStateOf(preferences.securityPin) }
    var mostrarEditorPin by remember { mutableStateOf(false) }
    var nuevoPin by remember { mutableStateOf("") }
    var mensajePlantilla by remember { mutableStateOf(preferences.mensajePlantilla) }
    var mostrarEditorPlantilla by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_mr_robot),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
                Text("Configuración y Estilo", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SECCIÓN 1: MODO OSCURO / TEMA
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.DarkMode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                "Tema de la Aplicación",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AppThemeMode.values().forEach { mode ->
                                val isSelected = currentTheme == mode
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        currentTheme = mode
                                        preferences.themeMode = mode
                                        onThemeChanged(mode)
                                    },
                                    label = {
                                        Text(
                                            when (mode) {
                                                AppThemeMode.SISTEMA -> "Auto"
                                                AppThemeMode.CLARO -> "Claro"
                                                AppThemeMode.OSCURO -> "Oscuro"
                                            },
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // SECCIÓN 2: BLOQUEO POR PIN (SOLO PIN)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Security,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        "Bloqueo por PIN",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Al abrir la aplicación",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = securityEnabled,
                                onCheckedChange = { checked ->
                                    if (checked && pinValue.length != 4) {
                                        mostrarEditorPin = true
                                    } else {
                                        securityEnabled = checked
                                        preferences.isSecurityEnabled = checked
                                    }
                                }
                            )
                        }

                        if (securityEnabled) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            // Configurar PIN de 4 dígitos
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Código de Seguridad",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        if (pinValue.isNotBlank()) "PIN configurado (****)" else "Sin PIN asignado",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                OutlinedButton(
                                    onClick = {
                                        nuevoPin = ""
                                        mostrarEditorPin = true
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(if (pinValue.isBlank()) "Crear PIN" else "Cambiar")
                                }
                            }
                        }
                    }
                }

                // SECCIÓN 3: PLANTILLA DE MENSAJE (PORTAPAPELES)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.ContentPaste,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    "Mensaje de Entrega",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Plantilla copiada al portapapeles",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                mensajePlantilla = preferences.mensajePlantilla
                                mostrarEditorPlantilla = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Personalizar Mensaje y Reglas")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Listo")
            }
        }
    )

    // Diálogo emergente para ingresar o cambiar el PIN de 4 dígitos
    if (mostrarEditorPin) {
        AlertDialog(
            onDismissRequest = { mostrarEditorPin = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Configurar PIN de 4 Dígitos", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Introduce un código de 4 números para acceder a tu inventario:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = nuevoPin,
                        onValueChange = {
                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                nuevoPin = it
                            }
                        },
                        label = { Text("Nuevo PIN (4 dígitos)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nuevoPin.length == 4) {
                            pinValue = nuevoPin
                            preferences.securityPin = nuevoPin
                            securityEnabled = true
                            preferences.isSecurityEnabled = true
                            mostrarEditorPin = false
                            Toast.makeText(context, "✅ PIN configurado con éxito", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "El PIN debe tener exactamente 4 dígitos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = nuevoPin.length == 4,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Guardar PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarEditorPin = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo para personalizar la plantilla del mensaje de entrega
    if (mostrarEditorPlantilla) {
        var textoEnEdicion by remember { mutableStateOf(mensajePlantilla) }

        AlertDialog(
            onDismissRequest = { mostrarEditorPlantilla = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.ContentPaste, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Editar Mensaje de Entrega", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Personaliza el texto copiado al portapapeles. Puedes usar estas variables automáticas:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "{servicio}  •  {dias}  •  {correo}\n{contrasena}  •  {fecha_inicio}  •  {fecha_fin}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    OutlinedTextField(
                        value = textoEnEdicion,
                        onValueChange = { textoEnEdicion = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 160.dp, max = 280.dp),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.bodySmall
                    )

                    TextButton(
                        onClick = {
                            textoEnEdicion = com.gestorcuentas.app.utils.MensajeEntregaUtils.PLANTILLA_PREDETERMINADA
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Restablecer Original", style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalTexto = textoEnEdicion.trim()
                        if (finalTexto.isNotBlank()) {
                            mensajePlantilla = finalTexto
                            preferences.mensajePlantilla = finalTexto
                            mostrarEditorPlantilla = false
                            Toast.makeText(context, "✅ Plantilla de mensaje actualizada", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Guardar Plantilla")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarEditorPlantilla = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
