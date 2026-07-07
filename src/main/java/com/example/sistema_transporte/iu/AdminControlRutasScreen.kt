package com.example.sistema_transporte.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistema_transporte.network.RutaResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminControlRutasScreen(
    zonasDesdeBD: List<String>,
    rutasFiltradas: List<RutaResponse>,
    zonaTrabajoActual: String,
    onZonaSeleccionada: (String) -> Unit,
    onEstadoRutaChanged: (Int, Boolean) -> Unit,
    onMenuClick: () -> Unit
) {
    var expandedZonaDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // --- BARRA SUPERIOR COHERENTE CON TU CATÁLOGO ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onMenuClick) { Text("≡") }
            Text(text = "Estados de Rutas", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(40.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Filtrar Rutas por Zona", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        //  MODELO DE CATÁLOGO
        ExposedDropdownMenuBox(
            expanded = expandedZonaDropdown,
            onExpandedChange = { expandedZonaDropdown = !expandedZonaDropdown }
        ) {
            OutlinedTextField(
                // 🚀 El valor ahora lee directamente el estado global 'zonaTrabajoActual'
                value = if (zonaTrabajoActual.isEmpty()) "Seleccionar Zona" else zonaTrabajoActual,
                onValueChange = {},
                readOnly = true,
                label = { Text("Seleccionar Zona") },
                placeholder = { Text("Elige una zona para desplegar rutas") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedZonaDropdown) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedZonaDropdown,
                onDismissRequest = { expandedZonaDropdown = false }
            ) {
                zonasDesdeBD.forEach { zona ->
                    DropdownMenuItem(
                        text = { Text(zona) },
                        onClick = {
                            expandedZonaDropdown = false
                            onZonaSeleccionada(zona) // Despacha la búsqueda y actualiza la zona global
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- LISTADO DE RUTAS CON SWITCH ARRASTRABLE ---

        if (zonaTrabajoActual.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Por favor, selecciona una zona arriba o desde el Inicio.", color = Color.Gray)
            }
        } else if (rutasFiltradas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No existen rutas asignadas a esta zona.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(rutasFiltradas) { ruta ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = ruta.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(text = "Código: ${ruta.codigo}", fontSize = 13.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (ruta.estaActiva) "● Activa" else "○ Inactiva",
                                    fontWeight = FontWeight.Bold,
                                    color = if (ruta.estaActiva) Color(0xFF2E7D32) else Color.Red,
                                    fontSize = 14.sp
                                )
                            }

                            Switch(
                                checked = ruta.estaActiva,
                                onCheckedChange = { nuevoEstado ->
                                    onEstadoRutaChanged(ruta.id, nuevoEstado)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}