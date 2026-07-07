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
fun CambioRutaScreen(
    zonasDesdeBD: List<String>,
    rutasFiltradasDesdeBD: List<String>,
    onZonaSeleccionada: (String) -> Unit,
    onMenuClick: () -> Unit,
    onEnviarCambioSector: (zona: String, ruta: String, motivo: String) -> Unit
) {
    var zonaSeleccionada by remember { mutableStateOf("") }
    var rutaSeleccionada by remember { mutableStateOf("") }
    var motivoCambio by remember { mutableStateOf("") }

    var expandedZona by remember { mutableStateOf(false) }
    var expandedRuta by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // --- BARRA SUPERIOR ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onMenuClick) { Text("☰") }
            Text(text = "Cambio de Ruta", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(40.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- COMBOBOX: ZONA OPERATIVA ---
        ExposedDropdownMenuBox(
            expanded = expandedZona,
            onExpandedChange = { expandedZona = !expandedZona }
        ) {
            OutlinedTextField(
                value = zonaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Zona a asignar") },
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
                            rutaSeleccionada = ""
                            onZonaSeleccionada(zona) // Filtra las rutas en el ViewModel
                            expandedZona = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- COMBOBOX: RUTA ESPECÍFICA ---
        ExposedDropdownMenuBox(
            expanded = expandedRuta,
            onExpandedChange = { if (zonaSeleccionada.isNotEmpty()) expandedRuta = !expandedRuta }
        ) {
            OutlinedTextField(
                value = rutaSeleccionada,
                onValueChange = {},
                readOnly = true,
                enabled = zonaSeleccionada.isNotEmpty(),
                label = { Text("Nueva Ruta") },
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

        Spacer(modifier = Modifier.height(20.dp))

        // --- PANEL DE DESCRIPCIÓN VACÍO PARA REDACTAR ---
        Text(text = "Motivo del cambio de sector / Incidencia", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = motivoCambio,
            onValueChange = { motivoCambio = it },
            placeholder = { Text("Describe los detalles de la reasignación técnica o vial aquí...") },
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- BOTÓN DE ENVÍO DIRECTO A USUARIOS ---
        Button(
            onClick = { onEnviarCambioSector(zonaSeleccionada, rutaSeleccionada, motivoCambio) },
            enabled = zonaSeleccionada.isNotEmpty() && rutaSeleccionada.isNotEmpty() && motivoCambio.trim().isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Enviar a Usuarios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CambioRutaPreview() {
    Sistema_TransporteTheme {
        CambioRutaScreen(
            zonasDesdeBD = emptyList(),
            rutasFiltradasDesdeBD = emptyList(),
            onZonaSeleccionada = {},
            onMenuClick = {},
            onEnviarCambioSector = { _, _, _ -> }
        )
    }
}