package com.example.sistema_transporte.iu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAvisoScreen(onMenuClick: () -> Unit) {
    var rutaSeleccionada by remember { mutableStateOf("Selecciona una ruta") }
    var tituloAviso by remember { mutableStateOf("") }
    var mensajeAviso by remember { mutableStateOf("") }

    var expandedRuta by remember { mutableStateOf(false) }
    val listaRutas = listOf("Ruta 100 (San Pablo - Piedra)", "Ruta 102 (Loma - Santa M)")

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
            Text(text = "Nuevo Aviso", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(40.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --- 1. SELECCIONA LA RUTA (COMBOBOX) ---
        Text(text = "Selecciona la ruta para enviar el aviso", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(6.dp))

        ExposedDropdownMenuBox(
            expanded = expandedRuta,
            onExpandedChange = { expandedRuta = !expandedRuta }
        ) {
            OutlinedTextField(
                value = rutaSeleccionada,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRuta) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(8.dp)
            )
            ExposedDropdownMenu(
                expanded = expandedRuta,
                onDismissRequest = { expandedRuta = false }
            ) {
                listaRutas.forEach { ruta ->
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

        // --- 2. ASIGNA UN TÍTULO ---
        Text(text = "Describe el Asigna un título", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = tituloAviso,
            onValueChange = { tituloAviso = it },
            placeholder = { Text("Ej: Retraso por tráfico u obra...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- 3. MENSAJE ---
        Text(text = "Mensaje", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = mensajeAviso,
            onValueChange = { mensajeAviso = it },
            placeholder = { Text("Escribe los detalles del aviso aquí...") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // --- 4. BOTÓN ENVIAR ---
        Button(
            onClick = { /* Lógica de envío en Python futura */ },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.7f)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Enviar Mensaje", fontWeight = FontWeight.Bold)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun AdminAvisoPreview() {
    AdminAvisoScreen(onMenuClick = {})
}
