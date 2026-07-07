package com.example.sistema_transporte.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConfirmacionRegistroScreen(
    rolSeleccionado: String, // "pasajero", "chofer", "admin"
    folioGeneradoPorBackend: String?, // Aquí le pasas el folio que te devuelva Flask (ej: "ADM-1234")
    onFinalizarRegistro: (String, String) -> Unit, // Pasa el rol y el token introducido
    onNavegarATerminos: () -> Unit, // Callback para ir a Términos y Condiciones
    onBack: () -> Unit
) {
    var tokenInput by remember { mutableStateOf("") }
    val rolMayuscula = rolSeleccionado.uppercase()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Verificación de Cuenta",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F5F5))
        ) {
            Text(
                text = "Rol actual: $rolMayuscula",
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // === LÓGICA DE CAMPOS SEGÚN EL ROL ===
        when (rolSeleccionado.lowercase()) {
            "pasajero" -> {
                Text(
                    text = "Los pasajeros no requieren validación de token. Puedes continuar.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            "chofer", "admin" -> {
                // Tanto Chofer como Admin verán este campo para meter el Token
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Este rol requiere un token de seguridad autorizado para poder registrarse en la plataforma.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = tokenInput,
                        onValueChange = { tokenInput = it },
                        label = { Text("Introduce el Token de Acceso") },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        singleLine = true
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón para procesar y validar en tu API
        Button(
            onClick = {
                onFinalizarRegistro(rolSeleccionado, tokenInput)
                // Nota: Tras el éxito en el ViewModel, llamarás a onNavegarATerminos()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4F4F))
        ) {
            Text("Validar y Continuar", color = Color.White)
        }

        // === 💥 SECCIÓN DINÁMICA: MOSTRAR EL FOLIO GENERADO AL ADMINISTRADOR ===
        // Si el rol es admin y el backend ya nos regresó el folio, lo pintamos abajo en grande
        if (rolSeleccionado.lowercase() == "admin" && !folioGeneradoPorBackend.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)) // Un verde clarito de éxito
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¡Administrador Registrado!",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        text = "Tu Folio de Acceso es:",
                        fontSize = 14.sp
                    )
                    Text(
                        text = folioGeneradoPorBackend,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("Atrás", color = Color(0xFF8B4F4F))
        }
    }
}

