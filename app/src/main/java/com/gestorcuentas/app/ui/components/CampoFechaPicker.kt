package com.gestorcuentas.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gestorcuentas.app.utils.DateUtils

/**
 * Componente ergonómico para seleccionar fechas mediante el DatePicker nativo de Material 3.
 * Al tocar cualquier parte del campo o el icono de calendario, se despliega el widget de selección de día.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFechaPicker(
    label: String,
    fechaIso: String,
    onFechaChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(14.dp),
    enabled: Boolean = true
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = fechaIso,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    onClick = { if (enabled) mostrarDialogo = true },
                    enabled = enabled
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Elegir $label",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            shape = shape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Overlay transparente para interceptar el click en cualquier parte del campo sin abrir teclado
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    mostrarDialogo = true
                }
        )
    }

    if (mostrarDialogo) {
        val initialMillis = remember(fechaIso) {
            DateUtils.fechaIsoToMillis(fechaIso)
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )

        DatePickerDialog(
            onDismissRequest = { mostrarDialogo = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val nuevaFecha = DateUtils.millisToFechaIso(millis)
                            onFechaChange(nuevaFecha)
                        }
                        mostrarDialogo = false
                    }
                ) {
                    Text("Aceptar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}
