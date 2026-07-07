package com.example.sistema_transporte.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistema_transporte.network.RutaResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCatalogosScreen(
    zonasDesdeBD: List<String>,
    rutasDesdeBD: List<RutaResponse>,
    onMenuClick: () -> Unit,
    onGuardarZona: (String) -> Unit,
    onGuardarRuta: (String, String) -> Unit, // Recibe (NombreRuta, NombreZona)
    onGuardarParada: (String, Int) -> Unit, // Recibe (NombreParada, IdRuta)
    onZonaSeleccionada: (String) -> Unit
) {
    var tabSeleccionada by remember { mutableStateOf(0) }
    val titulosTabs = listOf("Zonas", "Rutas", "Paradas")

    // Estados
    var nombreZona by remember { mutableStateOf("") }
    var nombreRuta by remember { mutableStateOf("") }
    var zonaSeleccionadaRuta by remember { mutableStateOf("") } // Para nueva ruta
    var nombreParada by remember { mutableStateOf("") }
    var rutaSeleccionadaParaParada by remember { mutableStateOf<RutaResponse?>(null) }

    var expandedZonaDropdown by remember { mutableStateOf(false) }
    var expandedRutaDropdown by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Encabezado
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onMenuClick) { Text("≡") }
            Text("Catálogo", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        TabRow(selectedTabIndex = tabSeleccionada) {
            titulosTabs.forEachIndexed { index, title ->
                Tab(selected = tabSeleccionada == index, onClick = { tabSeleccionada = index }, text = { Text(title) })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (tabSeleccionada) {
            0 -> { // ZONAS
                OutlinedTextField(value = nombreZona, onValueChange = { nombreZona = it }, label = { Text("Nombre de nueva Zona") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { onGuardarZona(nombreZona); nombreZona = "" }, modifier = Modifier.fillMaxWidth()) { Text("GUARDAR ZONA") }
            }

            1 -> { // RUTAS (Registro + Listado)
                Text("Registrar Ruta", fontWeight = FontWeight.Bold)
                OutlinedTextField(value = nombreRuta, onValueChange = { nombreRuta = it }, label = { Text("Nombre Ruta") }, modifier = Modifier.fillMaxWidth())

                // Dropdown para asignar zona al registrar
                ExposedDropdownMenuBox(expanded = expandedZonaDropdown, onExpandedChange = { expandedZonaDropdown = !expandedZonaDropdown }) {
                    OutlinedTextField(value = zonaSeleccionadaRuta, onValueChange = {}, readOnly = true, label = { Text("Zona") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedZonaDropdown) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(expanded = expandedZonaDropdown, onDismissRequest = { expandedZonaDropdown = false }) {
                        zonasDesdeBD.forEach { zona -> DropdownMenuItem(text = { Text(zona) }, onClick = { zonaSeleccionadaRuta = zona; expandedZonaDropdown = false }) }
                    }
                }
                Button(onClick = { onGuardarRuta(nombreRuta, zonaSeleccionadaRuta); nombreRuta = "" }, modifier = Modifier.fillMaxWidth(), enabled = nombreRuta.isNotEmpty() && zonaSeleccionadaRuta.isNotEmpty()) { Text("REGISTRAR RUTA") }

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                // Listado para ver
                OutlinedTextField(value = "Consultar rutas de zona...", onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                LazyColumn(modifier = Modifier.fillMaxSize().weight(1f)) {
                    items(rutasDesdeBD) { ruta -> Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Text("Ruta: ${ruta.nombre}", modifier = Modifier.padding(16.dp)) } }
                }
            }

            2 -> { // PARADAS (Registro)
                Text("Registrar Parada", fontWeight = FontWeight.Bold)
                OutlinedTextField(value = nombreParada, onValueChange = { nombreParada = it }, label = { Text("Nombre Parada") }, modifier = Modifier.fillMaxWidth())

                // Dropdown para seleccionar ruta existente
                ExposedDropdownMenuBox(expanded = expandedRutaDropdown, onExpandedChange = { expandedRutaDropdown = !expandedRutaDropdown }) {
                    OutlinedTextField(value = rutaSeleccionadaParaParada?.nombre ?: "", onValueChange = {}, readOnly = true, label = { Text("Asignar a Ruta") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRutaDropdown) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(expanded = expandedRutaDropdown, onDismissRequest = { expandedRutaDropdown = false }) {
                        rutasDesdeBD.forEach { ruta -> DropdownMenuItem(text = { Text(ruta.nombre) }, onClick = { rutaSeleccionadaParaParada = ruta; expandedRutaDropdown = false }) }
                    }
                }
                Button(onClick = { rutaSeleccionadaParaParada?.let { onGuardarParada(nombreParada, it.id); nombreParada = "" } }, modifier = Modifier.fillMaxWidth(), enabled = nombreParada.isNotEmpty() && rutaSeleccionadaParaParada != null) { Text("GUARDAR PARADA") }
            }
        }
    }
}