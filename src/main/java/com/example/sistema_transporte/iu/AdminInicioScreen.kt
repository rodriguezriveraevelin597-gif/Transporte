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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.sistema_transporte.ui.theme.Sistema_TransporteTheme

// Modelos de datos
data class RutaModelo(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val estaActiva: Boolean
)

data class AlertaChoferModelo(
    val id: Int,
    val mensaje: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminInicioScreen(
    zonasDesdeBD: List<String>,                       // 🚀 Nueva: Lista completa de zonas
    rutasDesdeBD: List<RutaModelo>,                   // Las rutas de la zona elegida
    zonaTrabajoActual: String,                        // 🚀 Nueva: Zona que se está trabajando
    rutaTrabajoActual: RutaModelo?,                   // 🚀 Nueva: Ruta seleccionada para hoy
    onZonaSeleccionada: (String) -> Unit,             // 🚀 Nueva: Evento al cambiar de zona
    onRutaSeleccionada: (RutaModelo) -> Unit,         // 🚀 Nueva: Evento al elegir la ruta
    alertasDesdeBD: List<AlertaChoferModelo>,
    onMenuClick: () -> Unit,
    onEstadoRutaChanged: (idRuta: Int, nuevoEstado: Boolean) -> Unit
) {
    var expandedZona by remember { mutableStateOf(false) }
    var expandedRuta by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Encabezado simplificado sin iconos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onMenuClick) {
                Text("☰", fontWeight = FontWeight.Bold)
            }
            Text(
                text = "Administrador",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Box(modifier = Modifier.size(40.dp)) // Espaciador
        }


        Text("Zona de Trabajo:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        ExposedDropdownMenuBox(
            expanded = expandedZona,
            onExpandedChange = { expandedZona = !expandedZona }
        ) {
            OutlinedTextField(
                value = if (zonaTrabajoActual.isEmpty()) "Seleccionar Zona" else zonaTrabajoActual,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedZona) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedZona,
                onDismissRequest = { expandedZona = false }
            ) {
                zonasDesdeBD.forEach { zona ->
                    DropdownMenuItem(
                        text = { Text(zona) },
                        onClick = {
                            onZonaSeleccionada(zona)
                            expandedZona = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))


        Text("Ruta Asignada para Hoy:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        ExposedDropdownMenuBox(
            expanded = expandedRuta,
            onExpandedChange = { if (zonaTrabajoActual.isNotEmpty()) expandedRuta = !expandedRuta }
        ) {
            OutlinedTextField(
                value = rutaTrabajoActual?.nombre ?: "Seleccionar Ruta",
                onValueChange = {},
                readOnly = true,
                enabled = zonaTrabajoActual.isNotEmpty(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRuta) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedRuta,
                onDismissRequest = { expandedRuta = false }
            ) {
                rutasDesdeBD.forEach { ruta ->
                    DropdownMenuItem(
                        text = { Text(ruta.nombre) },
                        onClick = {
                            onRutaSeleccionada(ruta)
                            expandedRuta = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Rutas bajo a tu cargo",
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 12.dp)
        )

        // TABLA DE RUTAS (Se mantiene tu diseño original con bordes)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Black)
            ) {
                Text("Ruta", modifier = Modifier.weight(1f).border(0.5.dp, Color.Black).padding(6.dp), fontWeight = FontWeight.Bold)
                Text("Nombre", modifier = Modifier.weight(1.5f).border(0.5.dp, Color.Black).padding(6.dp), fontWeight = FontWeight.Bold)
                Text("Estado", modifier = Modifier.weight(1.5f).border(0.5.dp, Color.Black).padding(6.dp), fontWeight = FontWeight.Bold)
            }


            if (rutaTrabajoActual == null) {
                Text(
                    text = "No hay rutas seleccionadas",
                    modifier = Modifier.padding(12.dp),
                    color = Color.Gray
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().border(0.5.dp, Color.Black),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(rutaTrabajoActual.codigo, modifier = Modifier.weight(1f).padding(6.dp))
                    Text(rutaTrabajoActual.nombre, modifier = Modifier.weight(1.5f).padding(6.dp))
                    Row(
                        modifier = Modifier.weight(1.5f).padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (rutaTrabajoActual.estaActiva) "Activa" else "Inactiva", fontSize = 14.sp)
                        Switch(
                            checked = rutaTrabajoActual.estaActiva,
                            onCheckedChange = { nuevoEstado -> onEstadoRutaChanged(rutaTrabajoActual.id, nuevoEstado) },
                            modifier = Modifier.scale(0.7f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECCIÓN DE ALERTAS (Se queda exactamente igual)
        Text(
            text = "Alertas de choferes",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .border(1.dp, Color.Black)
                .padding(8.dp)
        ) {
            if (alertasDesdeBD.isEmpty()) {
                Text(text = "Sin avisos")
            } else {
                LazyColumn {
                    items(alertasDesdeBD) { alerta ->
                        Text("• ${alerta.mensaje}")
                    }
                }
            }
        }
    }
}

fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
)

@Preview(showBackground = true)
@Composable
fun AdminInicioPreview() {
    Sistema_TransporteTheme {
        AdminInicioScreen(
            zonasDesdeBD = listOf("Centro", "Norte"),
            rutasDesdeBD = emptyList(),
            zonaTrabajoActual = "",
            rutaTrabajoActual = null,
            onZonaSeleccionada = {},
            onRutaSeleccionada = {},
            alertasDesdeBD = emptyList(),
            onMenuClick = {},
            onEstadoRutaChanged = { _, _ -> }
        )
    }
}