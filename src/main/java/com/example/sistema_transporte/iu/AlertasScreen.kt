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

// Modelo limpio de alerta para tu consulta SQL
data class AlertaSistema(val id: Int, val emisor: String, val tipo: String, val detalle: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertasScreen(
    zonasDesdeBD: List<String>,          // Lista limpia desde Flask
    rutasFiltradasDesdeBD: List<String>, // Lista filtrada dinámicamente según la zona elegida
    alertasDesdeBD: List<AlertaSistema>, // Historial de alertas activas en tiempo real
    onZonaSeleccionada: (String) -> Unit, // Avisa al ViewModel para traer las rutas de esa zona
    onMenuClick: () -> Unit
) {
    var zonaSeleccionada by remember { mutableStateOf("") }
    var rutaSeleccionada by remember { mutableStateOf("") }

    var expandedZona by remember { mutableStateOf(false) }
    var expandedRuta by remember { mutableStateOf(false) }

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
            Text(text = "Alertas Generales", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(40.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 1. COMBOBOX: ZONA ---
        ExposedDropdownMenuBox(
            expanded = expandedZona,
            onExpandedChange = { expandedZona = !expandedZona }
        ) {
            OutlinedTextField(
                value = zonaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Zona") },
                placeholder = { Text("Selecciona una zona") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedZona) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedZona, onDismissRequest = { expandedZona = false }) {
                zonasDesdeBD.forEach { zona ->
                    DropdownMenuItem(
                        text = { Text(zona) },
                        onClick = {
                            zonaSeleccionada = zona
                            rutaSeleccionada = "" // Reinicia el hijo en cascada
                            onZonaSeleccionada(zona) // Detona la petición a Flask para las rutas
                            expandedZona = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- 2. COMBOBOX: RUTA DEPENDIENTE ---
        ExposedDropdownMenuBox(
            expanded = expandedRuta,
            onExpandedChange = { if (zonaSeleccionada.isNotEmpty()) expandedRuta = !expandedRuta }
        ) {
            OutlinedTextField(
                value = rutaSeleccionada,
                onValueChange = {},
                readOnly = true,
                enabled = zonaSeleccionada.isNotEmpty(),
                label = { Text("Ruta") },
                placeholder = { Text(if (zonaSeleccionada.isEmpty()) "Primero selecciona una zona" else "Selecciona una ruta") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRuta) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedRuta, onDismissRequest = { expandedRuta = false }) {
                rutasFiltradasDesdeBD.forEach { ruta ->
                    DropdownMenuItem(
                        text = { Text(ruta) },
                        onClick = {
                            rutaSeleccionada = ruta
                            expandedRuta = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Alertas Activas en el Sistema", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(10.dp))

        // --- PANEL DE ALERTAS EN TIEMPO REAL ---
        if (alertasDesdeBD.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text("Sin alertas activas", fontSize = 13.sp, color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(alertasDesdeBD) { alerta ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = alerta.tipo, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                                Text(text = "De: ${alerta.emisor}", fontSize = 11.sp, color = Color.DarkGray)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = alerta.detalle, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

// Preview limpio arrancando con colecciones vacías
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AlertasPreview() {
    Sistema_TransporteTheme {
        AlertasScreen(
            zonasDesdeBD = emptyList(),
            rutasFiltradasDesdeBD = emptyList(),
            alertasDesdeBD = emptyList(),
            onZonaSeleccionada = {},
            onMenuClick = {}
        )
    }
}