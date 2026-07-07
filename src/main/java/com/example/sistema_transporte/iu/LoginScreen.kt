package com.example.sistema_transporte.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistema_transporte.ui.theme.Sistema_TransporteTheme

@Composable
fun LoginScreen(
    onLoginSuccess: (String, String) -> Unit, // 🚀 CAMBIADO: Ahora acepta los dos datos (correo y contraseña)
    onVolver: () -> Unit
) {

    var correoInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Ingrese sus credenciales",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = correoInput,
            onValueChange = {
                correoInput = it
            },
            label = {
                Text("Correo")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = passwordInput,
            onValueChange = {
                passwordInput = it
            },
            label = {
                Text("Contraseña")
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (mensajeError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (correoInput.isNotEmpty() && passwordInput.isNotEmpty()) {
                    // 🚀 CAMBIADO: Le pasamos los datos reales capturados en la pantalla al MainActivity
                    onLoginSuccess(correoInput, passwordInput)
                } else {
                    mensajeError = "Por favor llena todos los campos"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onVolver
        ) {
            Text("Regresar al inicio")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    Sistema_TransporteTheme {
        LoginScreen(
            onLoginSuccess = { _, _ -> }, // Adaptado para la vista previa
            onVolver = {}
        )
    }
}