package com.example.sistema_transporte.network

// ====================================================================
// 📦 MODELOS DE PETICIÓN (Lo que Android envía a Flask)
// ====================================================================
data class LoginRequest(val correo: String, val password: String)
data class ZonaRequest(val nombre: String)
data class RutaRequest(val nombre: String, val zona: String)
data class ParadaRequest(
    val nombre: String,
    val id_ruta: Int // <--- ¡Asegúrate de que este nombre sea idéntico al que usas en MainActivity!
)
data class EstadoRutaRequest(val estaActiva: Boolean)

data class TokenRegistroRequest(
    val folio_solicitante: String,
    val rol_destino: String
)

data class RegistroRequest(
    val nombre: String,
    val apellido_paterno: String,
    val apellido_materno: String,
    val correo: String,
    val telefono: String,
    val password: String,
    val rol: String,
    val token_acceso: String?
)

data class SalidaRequest(
    val id_asignacion: Int,
    val id_ruta: Int, // Agrega esta línea
    val id_admin: Int,
    val hora_salida: String
)
// ====================================================================
// 📦 MODELOS DE RESPUESTA (Lo que Flask devuelve a Android)
// ====================================================================
data class LoginResponse(
    val id_usuario: Int,
    val id_chofer: Int?, // <--- Agrega esta línea
    val nombre: String,
    val rol: String
)

data class RutaResponse(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val estaActiva: Boolean
)

data class GenericResponse(
    val mensaje: String?,
    val error: String?
)

data class ReporteModelo(
    val id: Int,
    val chofer: String,
    val unidad: String,
    val motivo: String
)

data class TokenRegistroResponse(
    val mensaje: String,
    val token_autorizacion: String,
    val rol: String? = null
)

data class RegistroResponse(
    val mensaje: String,
    val folio_asignado: String? = null
)

data class ParadaResponse(
    val id: Int, // Asegúrate que el JSON de Flask envíe 'id' o 'id_parada'
    val nombre_parada: String // Cambiado a como está en Flask
)

// ====================================================================
// 📦 MODELOS DE MONITOREO (Uso exclusivo en AdminControlOperativo)
// ====================================================================
data class ViajeControlOperativo(
    val id_control: Int,        // Cambiado a id_control
    val hora_salida: String,    // Cambiado a hora_salida
    val nombreRuta: String,
    val numeroEconomico: String,
    val choferNombre: String
)

data class ChoferCombiVinculada(
    val id_asignacion: Int,
    val infoDespliegue: String
)

// Agrega esto en tu archivo DataModels.kt
data class VinculacionUnidadRequest(
    val id_chofer: Int,
    val numero_economico: String,
    val placas: String
)

data class BajaUnidadRequest(
    val id_chofer: Int
)


data class UnidadInfo(
    val numero_economico: String?,
    val placa: String?,
    val nombre_ruta: String? // <--- ESTE ES EL CAMBIO NECESARIO
)

// Crea este archivo en tu paquete 'network'
data class LlegadaRequest(
    val id_control: Int,
    val hora_llegada: String
)
data class AsignarRutaRequest(val id_chofer: Int, val id_ruta: Int)


