
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
fun ConfiguracionScreen(
    onMenuClick: () -> Unit
) {
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
                text = "Configuración",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "",
                fontSize = 28.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Próximamente",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(text = "Aquí se mostrarán las opciones de configuración del usuario.")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ConfiguracionPreview() {
    Sistema_TransporteTheme {
        ConfiguracionScreen(onMenuClick = {})
    }
}