package com.example.sistema_transporte.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistema_transporte.ui.theme.Sistema_TransporteTheme

@Composable
fun GenerarTokenScreen(
    tokenGeneradoDesdeBD: String,
    mensajeError: String,
    estaCargando: Boolean,
    onMenuClick: () -> Unit,
    onSolicitarToken: (rol: String, folio: String) -> Unit
) {
    var rolSeleccionado by remember { mutableStateOf("") }
    var folioAcceso by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- BARRA SUPERIOR ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onMenuClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF804040))) {
                Text("≡", color = Color.White)
            }
            Text(text = "Tokens de Acceso", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(40.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Selecciona tu rol para validar acceso",
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- BOTONES DE SELECCIÓN DE ROL ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { rolSeleccionado = "CHOFER" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (rolSeleccionado == "CHOFER") MaterialTheme.colorScheme.primary else Color(0xFFF0F0F0),
                    contentColor = if (rolSeleccionado == "CHOFER") Color.White else Color(0xFF4D8AF4)
                ),
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Text("Chofer")
            }

            Button(
                onClick = { rolSeleccionado = "ADMINISTRADOR" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (rolSeleccionado == "ADMINISTRADOR") MaterialTheme.colorScheme.primary else Color(0xFFF0F0F0),
                    contentColor = if (rolSeleccionado == "ADMINISTRADOR") Color.White else Color(0xFF4D8AF4)
                ),
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Text("Administrador")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- CAMPO DE VALIDACIÓN ---
        OutlinedTextField(
            value = folioAcceso,
            onValueChange = { folioAcceso = it },
            enabled = rolSeleccionado.isNotEmpty(),
            label = { Text("Ingresa tu Folio o Datos de Acceso") },
            placeholder = { Text("Folio único de registro...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --- BOTÓN GENERAR TOKEN ---
        Button(
            onClick = { onSolicitarToken(rolSeleccionado, folioAcceso) },
            enabled = rolSeleccionado.isNotEmpty() && folioAcceso.trim().isNotEmpty() && !estaCargando,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4D8AF4)),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (estaCargando) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("GENERAR TOKEN DE ACCESO", color = Color.White)
            }
        }

        // --- VISUALIZACIÓN DE ERRORES ---
        if (mensajeError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = mensajeError, color = Color.Red, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }

        // --- RECUADRO DEL TOKEN GENERADO ---
        if (tokenGeneradoDesdeBD.isNotEmpty()) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "Tu Token Temporal Irrepetible:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFF4D8AF4), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = tokenGeneradoDesdeBD, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4D8AF4))
            }
        }
    }
}