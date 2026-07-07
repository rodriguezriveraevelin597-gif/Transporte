package com.example.sistema_transporte.iu // O posiblemente .ui si se copió de otro lado

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistema_transporte.ui.theme.Sistema_TransporteTheme

@Composable
fun SancionesScreen(
    onMenuClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Barra Superior
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onMenuClick) {
                Text("☰")
            }

            Text(
                text = "Sanciones",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "SIRMEX",
                fontSize = 19.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "\"Observa el monitoreo de la ruta\"",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Identificador de la ruta bajo revisión
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = "Ruta:",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            OutlinedTextField(
                value = "Ruta 64 San Pablo-Piedras",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.weight(3f)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {

            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Sanciones Asignadas A Tu Unidad", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = "Ninguna Sancion Activa",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SancionesPreview() {
    Sistema_TransporteTheme {
        SancionesScreen(onMenuClick = {})
    }
}

