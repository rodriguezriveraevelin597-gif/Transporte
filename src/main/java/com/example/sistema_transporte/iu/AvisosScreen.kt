package com.example.sistema_transporte.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

// Modelo de datos para las incidencias de cambios operativos en ruta
data class CambioOperativoModelo(val id: Int, val unidad: String, val detalleCambio: String)

@Composable
fun AvisosScreen(
    cambiosRutaDesdeBD: List<CambioOperativoModelo>, // Llega desde tu tabla de logs operativos en Flask
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
            Text(text = "Panel de Avisos", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(40.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Cambios Operativos y de Sector de Trabajo", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(10.dp))

        // --- CONTENEDOR DE AVISOS Y REASIGNACIONES ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, Color.Black)
                .padding(8.dp)
        ) {
            if (cambiosRutaDesdeBD.isEmpty()) {
                Text("Sin avisos operativos de rutas", fontSize = 13.sp, color = Color.Gray)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(cambiosRutaDesdeBD) { cambio ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = " Unidad/Eco: ${cambio.unidad}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = cambio.detalleCambio, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Preview limpio sin simulación de datos falsos para desarrollo base
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AvisosPreview() {
    Sistema_TransporteTheme {
        AvisosScreen(
            cambiosRutaDesdeBD = emptyList(), // Inicializa limpio
            onMenuClick = {}
        )
    }
}