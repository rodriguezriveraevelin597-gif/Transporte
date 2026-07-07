package com.example.sistema_transporte.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
fun AdminSancionarScreen(
    reporteProcedente: ReporteTransito?, // Trae los datos del reporte anterior o llega null desde el menú lateral
    onMenuClick: () -> Unit,
    onEnviarSancionChofer: (chofer: String, motivo: String) -> Unit
) {
    // Si hay reporte procedente lo precarga, de lo contrario inicia totalmente en blanco
    var choferInput by remember { mutableStateOf(reporteProcedente?.chofer ?: "") }
    var detallesSancion by remember { mutableStateOf(reporteProcedente?.motivo?.let { "Basado en reporte de incidencia: $it" } ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // --- BARRA SUPERIOR ORIGINAL ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onMenuClick) { Text("≡") }
            Text(text = "Sanciones", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(40.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --- CAMPO: CHOFER / UNIDAD ---
        Text(text = "Conductor o Unidad a sancionar", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = choferInput,
            onValueChange = { if (reporteProcedente == null) choferInput = it },
            readOnly = reporteProcedente != null, // Si viene de un reporte, no se puede editar el chofer
            placeholder = { Text("Ej. Carlos Mendoza / Eco-104") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- CAMPO: DETALLES ---
        Text(text = "Descripción de la infracción y sanción", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = detallesSancion,
            onValueChange = { detallesSancion = it },
            placeholder = { Text("Escribe la penalización o motivo detallado aquí...") },
            modifier = Modifier.fillMaxWidth().height(150.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --- BOTÓN EJECUTAR ---
        Button(
            onClick = { onEnviarSancionChofer(choferInput, detallesSancion) },
            enabled = choferInput.trim().isNotEmpty() && detallesSancion.trim().isNotEmpty(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Aplicar Sanción", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// Preview simulando la entrada desde el Menú Lateral para validar que aparezca completamente limpia
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminSancionarPreview() {
    Sistema_TransporteTheme {
        AdminSancionarScreen(
            reporteProcedente = null, // Pasamos null para comprobar que no pinte ninguna descripción falsa
            onMenuClick = {},
            onEnviarSancionChofer = { _, _ -> }
        )
    }
}