package com.gestorcuentas.app.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gestorcuentas.app.CuentasApp
import com.gestorcuentas.app.ui.theme.AccentGreen
import com.gestorcuentas.app.utils.AppPreferences
import com.gestorcuentas.app.utils.AutoBackupManager
import com.gestorcuentas.app.utils.BackupItemInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CopiaSeguridadBottomSheet(
    onExportar: () -> Unit,
    onImportar: () -> Unit,
    onRestaurarTexto: (String) -> Unit = {},
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val preferences = remember { AppPreferences(context) }

    var autoBackupActivo by remember { mutableStateOf(preferences.isAutoBackupEnabled) }
    var listaBackups by remember { mutableStateOf(AutoBackupManager.listarBackups(context)) }
    var ejecutandoBackupAhora by remember { mutableStateOf(false) }

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
                .padding(bottom = 28.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Copia de Seguridad y Restauración",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // 1. TARJETA DE RESPALDO AUTOMÁTICO DIARIO (21:00)
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    "Respaldo Diario a las 21:00",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Guarda 1 copia diaria y conserva los últimos 14 días",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = autoBackupActivo,
                            onCheckedChange = { activo ->
                                autoBackupActivo = activo
                                preferences.isAutoBackupEnabled = activo
                                AutoBackupManager.programarWorkManager(context)
                                Toast.makeText(
                                    context,
                                    if (activo) "✅ Respaldo automático a las 21:00 activado" else "Respaldo automático desactivado",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }

                    // Botón para ejecutar respaldo inmediato
                    FilledTonalButton(
                        onClick = {
                            if (!ejecutandoBackupAhora) {
                                ejecutandoBackupAhora = true
                                coroutineScope.launch {
                                    val app = context.applicationContext as? CuentasApp
                                    if (app != null) {
                                        val archivo = AutoBackupManager.ejecutarBackupDiario(
                                            context = context,
                                            repository = app.repository,
                                            forzar = true
                                        )
                                        listaBackups = AutoBackupManager.listarBackups(context)
                                        ejecutandoBackupAhora = false
                                        if (archivo != null) {
                                            Toast.makeText(context, "✅ Respaldo generado con éxito", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "No se pudo generar el respaldo", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (ejecutandoBackupAhora) "Generando respaldo..." else "Hacer Respaldo Ahora")
                    }
                }
            }

            // 2. SECCIÓN: OPERACIONES MANUALES
            Text("Operaciones Manuales", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            // Opción Exportar Respaldo Manual
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onExportar()
                    },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            onDismiss()
                            onExportar()
                        },
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }

                    Column {
                        Text("Exportar Archivo JSON...", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text("Elige la carpeta del teléfono donde guardar la copia", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Opción Importar Respaldo Manual
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onImportar()
                    },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            onDismiss()
                            onImportar()
                        },
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }

                    Column {
                        Text("Importar Archivo JSON...", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text("Restaura tus datos seleccionando un archivo .json previo", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // 3. SECCIÓN: RESPALDOS AUTOMÁTICOS GUARDADOS
            Text(
                text = "Respaldos Guardados en el Teléfono (${listaBackups.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            if (listaBackups.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "Aún no hay respaldos automáticos generados. Se creará uno automáticamente a las 21:00 o puedes presionar 'Hacer Respaldo Ahora'.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else {
                listaBackups.forEach { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.fecha,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = "${(item.tamanoBytes / 1024) + 1} KB",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${item.totalCuentas} cuentas • ${item.totalVentas} ventas registradas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Botón Restaurar en 1 Toque
                                Button(
                                    onClick = {
                                        try {
                                            val texto = item.file.readText()
                                            onDismiss()
                                            onRestaurarTexto(texto)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error al leer respaldo: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Restaurar", style = MaterialTheme.typography.labelSmall)
                                }

                                // Botón Compartir (WhatsApp / Drive / Telegram)
                                OutlinedButton(
                                    onClick = {
                                        try {
                                            val texto = item.file.readText()
                                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "application/json"
                                                putExtra(Intent.EXTRA_SUBJECT, "Copia de Seguridad - ${item.fecha}")
                                                putExtra(Intent.EXTRA_TEXT, texto)
                                            }
                                            val shareIntent = Intent.createChooser(sendIntent, "Compartir Copia de Seguridad")
                                            context.startActivity(shareIntent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error al compartir: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Compartir", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
