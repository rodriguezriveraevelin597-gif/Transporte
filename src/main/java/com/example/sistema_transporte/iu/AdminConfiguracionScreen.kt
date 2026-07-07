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

@Composable
fun AdminConfiguracionScreen(onMenuClick: () -> Unit) {
    // Estados para controlar los interruptores (Switches) de administración
    var permitirRegistros by remember { mutableStateOf(true) }
    var notificacionesCriticas by remember { mutableStateOf(true) }
    var modoMantenimiento by remember { mutableStateOf(false) }

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
            Button(onClick = onMenuClick) { Text("≡") }
            Text(text = "Configuración Administrador", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(40.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --- SECCIÓN 1: CONTROL DE USUARIOS ---
        Text(
            text = "Control del Sistema",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Permitir Nuevos Registros", fontWeight = FontWeight.Medium)
                        Text("Habilita o deshabilita la creación de cuentas públicas", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = permitirRegistros, onCheckedChange = { permitirRegistros = it })
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- SECCIÓN 2: SEGURIDAD Y MANTENIMIENTO ---
        Text(
            text = "Seguridad y Soporte",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Opción Modo Mantenimiento
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Modo Mantenimiento", fontWeight = FontWeight.Medium)
                        Text("Bloquea el acceso a choferes y pasajeros temporalmente", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = modoMantenimiento, onCheckedChange = { modoMantenimiento = it })
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider()
                Spacer(modifier = Modifier.height(14.dp))

                // Opción Alertas Globales
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Alertas Críticas en Tiempo Real", fontWeight = FontWeight.Medium)
                        Text("Recibir quejas graves de forma inmediata", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = notificacionesCriticas, onCheckedChange = { notificacionesCriticas = it })
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- BOTÓN CERRAR SESIÓN ---
        Button(
            onClick = { /* Lógica para limpiar el estado e ir a "inicio" */ },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("CERRAR SESIÓN DE ADMINISTRADOR", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Información de versión fija abajo
        Text(
            text = "SIRMEX Admin v1.0.0",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
@Preview(showBackground = true)
@Composable
fun AdminConfiguracionPreview() {
    AdminConfiguracionScreen(onMenuClick = {})
}
