package com.example.sistema_transporte.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistema_transporte.network.AsignarRutaRequest
import com.example.sistema_transporte.network.BajaUnidadRequest
import com.example.sistema_transporte.network.RetrofitClient
import com.example.sistema_transporte.network.GenericResponse
import com.example.sistema_transporte.network.RutaResponse
import com.example.sistema_transporte.network.VinculacionUnidadRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class) // <-- ESTO ES LO QUE TE FALTA
@Composable
fun ConductorConfiguracionScreen(
    idChoferActual: Int,
    listaRutas: List<RutaResponse>,
    onMenuClick: () -> Unit,
    onBajaExitosa: () -> Unit
) {
    val contexto = LocalContext.current
    var numEcon by remember { mutableStateOf("") }
    var placas by remember { mutableStateOf("") }
    var rutaSeleccionada by remember { mutableStateOf<RutaResponse?>(null) }
    var expandedRuta by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // --- AQUÍ ESTABA TU ERROR: FALTABA EL ENCABEZADO ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Button(onClick = onMenuClick) { Text("≡") }
            Text("Configuración", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        // ----------------------------------------------------

        OutlinedTextField(
            value = numEcon,
            onValueChange = { numEcon = it },
            label = { Text("Número Económico") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = placas,
            onValueChange = { placas = it },
            label = { Text("Placas") },
            modifier = Modifier.fillMaxWidth()
        )

        // Selector de Ruta
        // Selector de Ruta
        ExposedDropdownMenuBox(
            expanded = expandedRuta,
            onExpandedChange = { expandedRuta = !expandedRuta }
        ) {
            OutlinedTextField(
                value = rutaSeleccionada?.nombre ?: "Seleccionar Ruta",
                onValueChange = {},
                readOnly = true,
                label = { Text("Ruta") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRuta) },
                // Asegúrate de que este modifier sea EXACTAMENTE así
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            // El menú debe seguir inmediatamente al TextField
            ExposedDropdownMenu(
                expanded = expandedRuta,
                onDismissRequest = { expandedRuta = false }
            ) {
                listaRutas.forEach { ruta ->
                    DropdownMenuItem(
                        text = { Text(ruta.nombre) },
                        onClick = {
                            rutaSeleccionada = ruta
                            expandedRuta = false
                        },
                        // A veces es necesario añadir esto para evitar errores de despliegue
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        Button(
            onClick = {
                val ruta = rutaSeleccionada
                if (ruta != null) {
                    val reqUnidad = VinculacionUnidadRequest(
                        id_chofer = idChoferActual,
                        numero_economico = numEcon,
                        placas = placas
                    )
                    RetrofitClient.apiService.vincularUnidad(reqUnidad)
                        .enqueue(object : Callback<GenericResponse> {
                            override fun onResponse(
                                call: Call<GenericResponse>,
                                response: Response<GenericResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val reqRuta = AsignarRutaRequest(
                                        id_chofer = idChoferActual,
                                        id_ruta = ruta.id
                                    )
                                    RetrofitClient.apiService.asignarRutaAlChofer(reqRuta)
                                        .enqueue(object : Callback<GenericResponse> {
                                            override fun onResponse(
                                                call: Call<GenericResponse>,
                                                res: Response<GenericResponse>
                                            ) {
                                                Toast.makeText(
                                                    contexto,
                                                    "Unidad y Ruta registradas",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                            override fun onFailure(
                                                call: Call<GenericResponse>,
                                                t: Throwable
                                            ) {
                                                Toast.makeText(
                                                    contexto,
                                                    "Error al asignar ruta",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        })
                                } else {
                                    Toast.makeText(
                                        contexto,
                                        "Error al vincular unidad",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                                Toast.makeText(contexto, "Error de red", Toast.LENGTH_SHORT).show()
                            }
                        })
                }
            },
            enabled = numEcon.isNotEmpty() && placas.isNotEmpty() && rutaSeleccionada != null,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("REGISTRAR UNIDAD Y RUTA")
        }
        Button(
            onClick = {
                val ruta = rutaSeleccionada
                if (ruta != null) {
                    val reqUpdate = mapOf(
                        "id_chofer" to idChoferActual,
                        "numero_economico" to numEcon,
                        "placas" to placas,
                        "id_ruta" to ruta.id
                    )

                    RetrofitClient.apiService.actualizarRegistro(reqUpdate)
                        .enqueue(object : Callback<GenericResponse> {
                            override fun onResponse(
                                call: Call<GenericResponse>,
                                response: Response<GenericResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        contexto,
                                        "Configuración actualizada",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // 🚀 AQUÍ ESTÁ EL PASO FINAL:
                                    // Llamas a la función que limpia el MainActivity
                                    onBajaExitosa()
                                } else {
                                    Toast.makeText(contexto, "Error al guardar", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }

                            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                                Toast.makeText(contexto, "Error de red", Toast.LENGTH_SHORT).show()
                            }
                        })
                } else {
                    Toast.makeText(contexto, "Selecciona una ruta primero", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("ACTUALIZAR")
        }
    }
}