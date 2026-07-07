package com.example.sistema_transporte.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistema_transporte.ui.theme.Sistema_TransporteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasajeroScreen(
    onMenuClick: () -> Unit
) {
    // --- LISTAS ESTRUCTURADAS (Simulación de BD) ---
    val listaZonas = listOf("Chimalhuacán Centro", "Piedras Negras")
    val rutasPorZona = mapOf(
        "Chimalhuacán Centro" to listOf("Ruta 04 - Directo", "Ruta 04 - Ampliación"),
        "Piedras Negras" to listOf("Ruta 12 - Caseta", "Ruta 12 - Vías")
    )
    val paradasPorRuta = mapOf(
        "Ruta 04 - Directo" to listOf("Parada Piedra", "Av. Central", "Zócalo"),
        "Ruta 04 - Ampliación" to listOf("Terminal Centro", "Calle 3", "La Cruz"),
        "Ruta 12 - Caseta" to listOf("Entronque", "Caseta Principal", "Bodega"),
        "Ruta 12 - Vías" to listOf("Estación Vías", "Mercado Local", "Piso de Piedra")
    )

    // --- VARIABLES DE ESTADO (Tus variables originales) ---
    var zona by remember { mutableStateOf("") }
    var ruta by remember { mutableStateOf("") }
    var puntoInicio by remember { mutableStateOf("") }
    var puntoFinal by remember { mutableStateOf("") }

    // --- CONTROL DE APERTURA DE CADA COMBOBOX ---
    var expandedZona by remember { mutableStateOf(false) }
    var expandedRuta by remember { mutableStateOf(false) }
    var expandedInicio by remember { mutableStateOf(false) }
    var expandedFin by remember { mutableStateOf(false) }

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
            Button(onClick = onMenuClick) {
                Text("☰")
            }

            Text(
                text = "SIRMEX",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(text = "Elige tu trayecto que recorrerás hoy")

        Spacer(modifier = Modifier.height(20.dp))

        // --- 1. COMBOBOX ZONA ---
        ExposedDropdownMenuBox(
            expanded = expandedZona,
            onExpandedChange = { expandedZona = !expandedZona }
        ) {
            OutlinedTextField(
                value = zona.ifEmpty { "Selecciona una zona" },
                onValueChange = {},
                readOnly = true,
                label = { Text("Zona") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedZona) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedZona, onDismissRequest = { expandedZona = false }) {
                listaZonas.forEach { itemZona ->
                    DropdownMenuItem(
                        text = { Text(itemZona) },
                        onClick = {
                            zona = itemZona
                            ruta = "" // Limpia la cascada hacia abajo
                            puntoInicio = ""
                            puntoFinal = ""
                            expandedZona = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 2. COMBOBOX RUTA (Habilitado sólo si ya elegiste Zona) ---
        ExposedDropdownMenuBox(
            expanded = expandedRuta,
            onExpandedChange = { if (zona.isNotEmpty()) expandedRuta = !expandedRuta }
        ) {
            OutlinedTextField(
                value = if (zona.isEmpty()) "Selecciona primero una zona" else ruta.ifEmpty { "Selecciona una ruta" },
                onValueChange = {},
                readOnly = true,
                enabled = zona.isNotEmpty(),
                label = { Text("Ruta") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRuta) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedRuta, onDismissRequest = { expandedRuta = false }) {
                rutasPorZona[zona]?.forEach { itemRuta ->
                    DropdownMenuItem(
                        text = { Text(itemRuta) },
                        onClick = {
                            ruta = itemRuta
                            puntoInicio = "" // Limpia las paradas
                            puntoFinal = ""
                            expandedRuta = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- FILA COMPARTIDA (INICIO Y FINAL LADO A LADO) ---
        Row(modifier = Modifier.fillMaxWidth()) {

            // --- 3. COMBOBOX INICIO (Lado Izquierdo) ---
            Box(modifier = Modifier.weight(1f)) {
                ExposedDropdownMenuBox(
                    expanded = expandedInicio,
                    onExpandedChange = { if (ruta.isNotEmpty()) expandedInicio = !expandedInicio }
                ) {
                    OutlinedTextField(
                        value = puntoInicio.ifEmpty { "Origen" },
                        onValueChange = {},
                        readOnly = true,
                        enabled = ruta.isNotEmpty(),
                        label = { Text("Inicio") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedInicio) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expandedInicio, onDismissRequest = { expandedInicio = false }) {
                        paradasPorRuta[ruta]?.forEach { parada ->
                            DropdownMenuItem(
                                text = { Text(parada) },
                                onClick = {
                                    puntoInicio = parada
                                    expandedInicio = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // --- 4. COMBOBOX FINAL (Lado Derecho) ---
            Box(modifier = Modifier.weight(1f)) {
                ExposedDropdownMenuBox(
                    expanded = expandedFin,
                    onExpandedChange = { if (ruta.isNotEmpty()) expandedFin = !expandedFin }
                ) {
                    OutlinedTextField(
                        value = puntoFinal.ifEmpty { "Destino" },
                        onValueChange = {},
                        readOnly = true,
                        enabled = ruta.isNotEmpty(),
                        label = { Text("Final") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFin) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expandedFin, onDismissRequest = { expandedFin = false }) {
                        paradasPorRuta[ruta]?.forEach { parada ->
                            DropdownMenuItem(
                                text = { Text(parada) },
                                onClick = {
                                    puntoFinal = parada
                                    expandedFin = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = { /* TODO: Calcular trayecto */ },
            enabled = puntoInicio.isNotEmpty() && puntoFinal.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calcular trayecto")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PasajeroPreview() {
    Sistema_TransporteTheme {
        PasajeroScreen(onMenuClick = {})
    }
}