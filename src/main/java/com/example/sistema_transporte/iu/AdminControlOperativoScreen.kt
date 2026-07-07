package com.example.sistema_transporte.ui

import android.app.TimePickerDialog
import android.icu.util.Calendar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistema_transporte.network.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminControlOperativoScreen(
    zonaTrabajoActual: String,
    rutasDeLaZona: List<RutaResponse>,
    viajesActivosDesdeBD: List<ViajeControlOperativo>,
    idAdminActual: Int,
    // CORRIGE ESTA LÍNEA EXACTAMENTE:
    onRegistrarSalida: (Int, Int, Int, String) -> Unit,
    onRegistrarLlegada: (Int, String) -> Unit,
    onMenuClick: () -> Unit
) {
    var tabIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current

    // Estados de Despacho
    var rutaSeleccionada by remember { mutableStateOf<RutaResponse?>(null) }
    var combiSeleccionada by remember { mutableStateOf<ChoferCombiVinculada?>(null) }
    var horaSalida by remember { mutableStateOf("") }

    // Lista dinámica de choferes para la ruta seleccionada
    var listaChoferesPorRuta by remember { mutableStateOf<List<ChoferCombiVinculada>>(emptyList()) }

    // Estados de expansión
    var expandedRuta by remember { mutableStateOf(false) }
    var expandedCombi by remember { mutableStateOf(false) }

    // Cargar choferes cuando cambia la ruta
    LaunchedEffect(rutaSeleccionada) {
        listaChoferesPorRuta = emptyList() // Limpiar al cambiar ruta
        combiSeleccionada = null
        if (rutaSeleccionada != null) {
            RetrofitClient.apiService.choferesPorRuta(rutaSeleccionada!!.id)
                .enqueue(object : retrofit2.Callback<List<ChoferCombiVinculada>> {
                    override fun onResponse(call: retrofit2.Call<List<ChoferCombiVinculada>>, response: retrofit2.Response<List<ChoferCombiVinculada>>) {
                        if (response.isSuccessful) {
                            listaChoferesPorRuta = response.body() ?: emptyList()
                        }
                    }
                    override fun onFailure(call: retrofit2.Call<List<ChoferCombiVinculada>>, t: Throwable) {}
                })
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onMenuClick) { Text("☰") }
            Text("Control: $zonaTrabajoActual", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        TabRow(selectedTabIndex = tabIndex) {
            Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Despachar") })
            Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Recibir") })
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (tabIndex) {
            0 -> { // PESTAÑA DESPACHAR
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    // Selector de Ruta
                    ExposedDropdownMenuBox(expanded = expandedRuta, onExpandedChange = { expandedRuta = !expandedRuta }) {
                        OutlinedTextField(
                            value = rutaSeleccionada?.nombre ?: "", onValueChange = {}, readOnly = true,
                            label = { Text("Ruta") }, modifier = Modifier.menuAnchor().fillMaxWidth(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRuta) })
                        ExposedDropdownMenu(expanded = expandedRuta, onDismissRequest = { expandedRuta = false }) {
                            rutasDeLaZona.forEach { ruta ->
                                DropdownMenuItem(text = { Text(ruta.nombre) }, onClick = { rutaSeleccionada = ruta; expandedRuta = false })
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Selector de Chofer - Unidad
                    ExposedDropdownMenuBox(expanded = expandedCombi, onExpandedChange = { expandedCombi = !expandedCombi }) {
                        OutlinedTextField(
                            value = combiSeleccionada?.infoDespliegue ?: "", onValueChange = {}, readOnly = true,
                            label = { Text("Chofer - Unidad") }, modifier = Modifier.menuAnchor().fillMaxWidth(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCombi) })
                        ExposedDropdownMenu(expanded = expandedCombi, onDismissRequest = { expandedCombi = false }) {
                            listaChoferesPorRuta.forEach { combo ->
                                DropdownMenuItem(text = { Text(combo.infoDespliegue) }, onClick = { combiSeleccionada = combo; expandedCombi = false })
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(modifier = Modifier.fillMaxWidth().height(56.dp), onClick = {
                        val c = Calendar.getInstance()
                        TimePickerDialog(context, { _, h, m -> horaSalida = String.format("%02d:%02d", h, m) },
                            c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
                    }) { Text("Hora Salida: ${if (horaSalida.isEmpty()) "Seleccionar" else horaSalida}") }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = rutaSeleccionada != null && combiSeleccionada != null && horaSalida.isNotEmpty(),
                        onClick = {
                            // Asegúrate de enviar los 4 parámetros en el mismo orden que definiste arriba
                            onRegistrarSalida(
                                combiSeleccionada!!.id_asignacion,
                                idAdminActual,
                                rutaSeleccionada!!.id, // Aquí pasamos el id_ruta
                                horaSalida
                            )
                        }
                    ) { Text("DESPACHAR SALIDA") }
                }
            }
            1 -> { // PESTAÑA RECIBIR
                Column(modifier = Modifier.fillMaxSize()) {
                    Text("Total: ${viajesActivosDesdeBD.size} viajes en curso")
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(viajesActivosDesdeBD) { viaje ->
                            var horaLlegada by remember { mutableStateOf("") }
                            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Ruta: ${viaje.nombreRuta}", fontWeight = FontWeight.Bold)
                                        Text("Unidad: ${viaje.numeroEconomico} | ${viaje.choferNombre}")
                                        Text("Salida: ${viaje.hora_salida}")
                                    }
                                    Button(onClick = { onRegistrarLlegada(viaje.id_control, "12:00") }) { Text("Registrar Llegada") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}