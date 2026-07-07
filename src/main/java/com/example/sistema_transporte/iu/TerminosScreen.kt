package com.example.sistema_transporte.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistema_transporte.ui.theme.Sistema_TransporteTheme

@Composable
fun TerminosScreen(
    rol: String, // Recibe "Chofer" o "Administrador" para personalizar la pantalla
    onAceptar: () -> Unit
) {
    var aceptarTerminos by remember { mutableStateOf(false) }
    var aceptarPoliticas by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título principal con el Rol dinámico
        Text(
            text = "Términos y Condiciones",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Perfil operativo: $rol",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Contrato de Términos adaptado a SIRMEX
        OutlinedTextField(
            value = "Como usuario con rol de $rol en el sistema SIRMEX, usted acepta que el uso de la plataforma está estrictamente limitado a la gestión del transporte público. " +
                    "Los datos de control operativo, alertas de ruta y reportes son confidenciales y propiedad exclusiva del sistema central.",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Políticas de Privacidad",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = "En SIRMEX protegemos los datos de localización en tiempo real de nuestras unidades. " +
                    "La información recolectada de choferes y administradores se utilizará únicamente para el monitoreo de rutas, asignación de sectores y aplicación de sanciones pertinentes.",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Casillas de verificación
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = aceptarTerminos,
                onCheckedChange = { aceptarTerminos = it }
            )
            Text("Acepto términos y condiciones")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = aceptarPoliticas,
                onCheckedChange = { aceptarPoliticas = it }
            )
            Text("Acepto políticas de privacidad")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Siguiente: Se activa sólo si ambos checkboxes son verdaderos
        Button(
            onClick = { onAceptar() },
            enabled = aceptarTerminos && aceptarPoliticas,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Siguiente")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TerminosPreview() {
    Sistema_TransporteTheme {
        // En el preview simulamos que entra un Chofer para visualizarlo a la derecha
        TerminosScreen(
            rol = "Chofer",
            onAceptar = {}
        )
    }
}