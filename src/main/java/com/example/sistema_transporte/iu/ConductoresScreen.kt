package com.example.sistema_transporte.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistema_transporte.network.UnidadInfo

@Composable
fun ConductoresScreen(
    datosUnidad: UnidadInfo?,
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMenuClick) {
                    Text("☰", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("SIRMEX", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Bienvenido de nuevo, conductor", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            // --- Tarjeta de Ruta ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Ruta Asignada", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        text = datosUnidad?.nombre_ruta ?: "Sin ruta asignada",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Tarjetas de Unidad y Placas ---
            UnidadCard("Número de Unidad", datosUnidad?.numero_economico ?: "Sin asignar")
            Spacer(modifier = Modifier.height(16.dp))
            UnidadCard("Placas", datosUnidad?.placa ?: "N/A")
        }
    }
}

@Composable
fun UnidadCard(titulo: String, valor: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(titulo, fontSize = 14.sp, color = Color.DarkGray)
            Text(valor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}