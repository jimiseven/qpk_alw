package com.gestorcuentas.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CopiaSeguridadBottomSheet(
    onExportar: () -> Unit,
    onImportar: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Copia de Seguridad y Restauración",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Tus datos se almacenan 100% de forma local. Puedes exportar un respaldo portátil o restaurar uno previo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Opción 1: Exportar Respaldo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onExportar()
                    },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            onDismiss()
                            onExportar()
                        },
                        modifier = Modifier.size(46.dp)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }

                    Column {
                        Text("Exportar Copia de Seguridad", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Guarda un archivo .json con tus cuentas y ventas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Opción 2: Importar Respaldo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onImportar()
                    },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            onDismiss()
                            onImportar()
                        },
                        modifier = Modifier.size(46.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    }

                    Column {
                        Text("Importar Copia de Seguridad", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Restaura tus datos desde un archivo .json previo", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
