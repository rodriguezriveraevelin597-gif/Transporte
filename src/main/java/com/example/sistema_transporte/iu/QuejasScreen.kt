package com.example.sistema_transporte.iu // O posiblemente .ui si se copió de otro lado

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.sistema_transporte.ui.theme.Sistema_TransporteTheme

@Composable
fun QuejasScreen(
    onMenuClick: () -> Unit
) {
    var ruta by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onMenuClick
            ) {
                Text("☰")
            }

            Text(
                text = "Quejas",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "\"Tu reporte ayuda a mejorar el servicio\"")

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = ruta,
            onValueChange = { ruta = it },
            label = { Text("Ruta del incidente") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = tipo,
            onValueChange = { tipo = it },
            label = { Text("Tipo de problema") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = unidad,
            onValueChange = { unidad = it },
            label = { Text("Número económico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Describe el problema") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { /* TODO: Enviar queja */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuejasPreview() {
    Sistema_TransporteTheme {
        QuejasScreen(onMenuClick = {})
    }
}