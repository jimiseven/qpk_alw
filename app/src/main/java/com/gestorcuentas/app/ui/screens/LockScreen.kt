package com.gestorcuentas.app.ui.screens

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.gestorcuentas.app.R
import com.gestorcuentas.app.ui.theme.DangerRed
import com.gestorcuentas.app.utils.AppPreferences

@Composable
fun LockScreen(
    preferences: AppPreferences,
    onUnlocked: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Verificar si el dispositivo (Honor Android 13) tiene soporte biométrico
    val biometricManager = remember { BiometricManager.from(context) }
    val canAuthenticateWithBiometrics = remember {
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    val launcherBiometrics: () -> Unit = {
        if (activity != null && canAuthenticateWithBiometrics && preferences.useBiometrics) {
            val executor = ContextCompat.getMainExecutor(activity)
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Gestor de Cuentas")
                .setSubtitle("Toca el sensor de huella dactilar para acceder")
                .setNegativeButtonText("Usar PIN")
                .build()

            val biometricPrompt = BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        onUnlocked()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        // Si el usuario cancela o pulsa "Usar PIN", no mostramos error ruidoso
                        if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                            errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                        ) {
                            errorMessage = errString.toString()
                        }
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        errorMessage = "Huella no reconocida"
                    }
                }
            )
            biometricPrompt.authenticate(promptInfo)
        }
    }

    // Al iniciar la pantalla, disparar huella dactilar si está habilitada
    LaunchedEffect(Unit) {
        if (preferences.useBiometrics && canAuthenticateWithBiometrics) {
            launcherBiometrics()
        }
    }

    // Validar PIN al completar los 4 dígitos
    LaunchedEffect(enteredPin) {
        if (enteredPin.length == 4) {
            val correctPin = preferences.securityPin
            if (correctPin.isEmpty() || enteredPin == correctPin) {
                onUnlocked()
            } else {
                errorMessage = "PIN incorrecto"
                enteredPin = ""
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Cabecera con Vector de Gatito
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_cat_logo),
                        contentDescription = "Logo Gatito",
                        modifier = Modifier
                            .size(96.dp)
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Gestor de Cuentas",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (preferences.useBiometrics && canAuthenticateWithBiometrics)
                        "Ingresa tu PIN de 4 dígitos o usa tu huella"
                    else
                        "Ingresa tu PIN de 4 dígitos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Indicador visual de los 4 dígitos del PIN
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFilled) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (isFilled) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(visible = errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = DangerRed,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Teclado Numérico con estilo moderno
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Fila 1: 1, 2, 3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    KeypadButton(text = "1") { if (enteredPin.length < 4) enteredPin += "1"; errorMessage = null }
                    KeypadButton(text = "2") { if (enteredPin.length < 4) enteredPin += "2"; errorMessage = null }
                    KeypadButton(text = "3") { if (enteredPin.length < 4) enteredPin += "3"; errorMessage = null }
                }

                // Fila 2: 4, 5, 6
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    KeypadButton(text = "4") { if (enteredPin.length < 4) enteredPin += "4"; errorMessage = null }
                    KeypadButton(text = "5") { if (enteredPin.length < 4) enteredPin += "5"; errorMessage = null }
                    KeypadButton(text = "6") { if (enteredPin.length < 4) enteredPin += "6"; errorMessage = null }
                }

                // Fila 3: 7, 8, 9
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    KeypadButton(text = "7") { if (enteredPin.length < 4) enteredPin += "7"; errorMessage = null }
                    KeypadButton(text = "8") { if (enteredPin.length < 4) enteredPin += "8"; errorMessage = null }
                    KeypadButton(text = "9") { if (enteredPin.length < 4) enteredPin += "9"; errorMessage = null }
                }

                // Fila 4: Huella, 0, Borrar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (canAuthenticateWithBiometrics && preferences.useBiometrics) {
                        IconButton(
                            onClick = launcherBiometrics,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                Icons.Default.Fingerprint,
                                contentDescription = "Desbloquear con Huella",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(64.dp))
                    }

                    KeypadButton(text = "0") { if (enteredPin.length < 4) enteredPin += "0"; errorMessage = null }

                    IconButton(
                        onClick = {
                            if (enteredPin.isNotEmpty()) {
                                enteredPin = enteredPin.dropLast(1)
                                errorMessage = null
                            }
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Backspace,
                            contentDescription = "Borrar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun KeypadButton(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
