package com.example.sistema_transporte.ui

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
fun SeleccionRolScreen(
    onRolSeleccionado: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "SIRMEX",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Selecciona tu rol",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                onRolSeleccionado("pasajero") // 🚀 Cambiado a minúsculas
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Pasajero")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onRolSeleccionado("chofer") // 🚀 Cambiado a minúsculas
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Chofer")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onRolSeleccionado("admin") // 🚀 Cambiado a minúsculas
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Administrador")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SeleccionRolScreenPreview() {
    Sistema_TransporteTheme {
        SeleccionRolScreen(
            onRolSeleccionado = {}
        )
    }
}
