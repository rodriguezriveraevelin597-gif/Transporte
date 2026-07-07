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

// Modelo limpio para mapear las incidencias de tu base de datos
data class ReporteTransito(val id: Int, val chofer: String, val unidad: String, val motivo: String)

@Composable
fun AdminReportesScreen(
    reporteActual: ReporteTransito?, // Puede llegar null si tu tabla SQL está vacía
    onSiguienteReporte: () -> Unit,
    onIrASancionar: (ReporteTransito) -> Unit,
    onMenuClick: () -> Unit
) {
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
            Button(onClick = onMenuClick) { Text("☰") }
            Text(text = "Reportes", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(40.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --- MANEJO DE RECUADRO SEGÚN TU BD ---
        if (reporteActual == null) {
            // Recuadro completamente vacío si no hay reportes en la base de datos
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(1.dp, Color.Black)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Sin reportes", fontSize = 14.sp, color = Color.Gray)
            }
        } else {
            // Contenedor con los datos reales del reporte si existe registro
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Black)
                    .padding(16.dp)
            ) {
                Text("Reporte ID: ${reporteActual.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Chofer: ${reporteActual.chofer}", fontSize = 14.sp)
                Text("Unidad: ${reporteActual.unidad}", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Incidencia presentada:", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Text(text = reporteActual.motivo, fontSize = 13.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- BOTONES DE CONTROL DE FLUJO ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onSiguienteReporte,
                    modifier = Modifier.weight(1f).height(45.dp)
                ) {
                    Text("Siguiente", fontSize = 14.sp)
                }

                Button(
                    onClick = { onIrASancionar(reporteActual) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.weight(1f).height(45.dp)
                ) {
                    Text("Aplicar Sanción", fontSize = 14.sp)
                }
            }
        }
    }
}

// Preview limpio cargando el estado inicial vacío sin simular datos falsos
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminReportesPreview() {
    Sistema_TransporteTheme {
        AdminReportesScreen(
            reporteActual = null, // Inicializa en vacío para comprobar el recuadro limpio
            onSiguienteReporte = {},
            onIrASancionar = {},
            onMenuClick = {}
        )
    }
}