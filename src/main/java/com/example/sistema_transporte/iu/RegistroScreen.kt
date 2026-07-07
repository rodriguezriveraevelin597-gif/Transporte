package com.example.sistema_transporte.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistema_transporte.ui.theme.Sistema_TransporteTheme

@Composable
fun RegistroScreen(
    // Añadimos la variable de 'rol' y 'token' a la función callback para mandarla al ViewModel/Flask
    onRegistroCompleto: (String, String, String, String, String, String, String, String) -> Unit,
    onVolver: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellidoP by remember { mutableStateOf("") }
    var apellidoM by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }

    // --- NUEVAS VARIABLES PARA EL CANDADO DE SEGURIDAD ---
    var rolSeleccionado by remember { mutableStateOf("PASAJERO") }
    var tokenAcceso by remember { mutableStateOf("") }
    var expandedMenu by remember { mutableStateOf(false) }

    var paso by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (paso) {
            1 -> {
                Text(
                    "SIRMEX",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = apellidoP,
                    onValueChange = { apellidoP = it },
                    label = { Text("Apellido paterno") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = apellidoM,
                    onValueChange = { apellidoM = it },
                    label = { Text("Apellido materno") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { paso = 2 },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Siguiente")
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(onClick = onVolver) {
                    Text("Cancelar y volver")
                }
            }

            2 -> {
                Text(
                    "Crear contraseña",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = confirmar,
                    onValueChange = { confirmar = it },
                    label = { Text("Confirmar contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (contrasena.isNotEmpty() && contrasena == confirmar) {
                            paso = 3
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continuar")
                }
            }

            3 -> {
                Text(
                    "Tipo de Cuenta",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text("Selecciona tu rol:")
                Spacer(modifier = Modifier.height(15.dp))

                // Selector de Rol (Exposed Dropdown Menu)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedMenu = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Rol: $rolSeleccionado")
                    }
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DropdownMenuItem(
                            text = { Text("PASAJERO") },
                            onClick = { rolSeleccionado = "PASAJERO"; expandedMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("CHOFER") },
                            onClick = { rolSeleccionado = "CHOFER"; expandedMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("ADMINISTRADOR") },
                            // CAMBIO AQUÍ: Ahora enviamos "ADMIN" en lugar de "ADMINISTRADOR"
                            onClick = { rolSeleccionado = "ADMIN"; expandedMenu = false }
                        )
                    }
                }

                // SI ES CHOFER O ADMIN, APARECE EL CAMPO OBLIGATORIO DEL TOKEN
                if (rolSeleccionado == "CHOFER" || rolSeleccionado == "ADMIN") {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        "Validación requerida",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )


                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tokenAcceso,
                        onValueChange = { tokenAcceso = it.uppercase() }, // Forzamos mayúsculas automáticas
                        label = { Text("Ingresa tu Token de Acceso") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Spacer(modifier = Modifier.height(25.dp))
                    Text("Los pasajeros tienen acceso inmediato sin necesidad de tokens.")
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        // Enviamos los 8 campos completos al backend a través del callback
                        onRegistroCompleto(
                            nombre,
                            apellidoP,
                            apellidoM,
                            correo,
                            telefono,
                            contrasena,
                            rolSeleccionado,
                            tokenAcceso
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    // Si es Chofer/Admin y no ha escrito nada en el token, se deshabilita el botón
                    enabled = (rolSeleccionado == "PASAJERO" || tokenAcceso.isNotEmpty())
                ) {
                    Text("Finalizar Registro")
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(onClick = { paso = 2 }) {
                    Text("Atrás")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegistroScreenPreview() {
    Sistema_TransporteTheme {
        RegistroScreen(
            onRegistroCompleto = { _, _, _, _, _, _, _, _ -> },
            onVolver = {}
        )
    }
}