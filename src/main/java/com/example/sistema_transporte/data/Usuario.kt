package com.example.sistema_transporte.data

data class Usuario(
// Aquí definiremos si es "Admin Principal" o "Chofer"
    val idUsuario: Int? = null,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val tipoUsuario: String,
    val tokenValidacion: String? = null
)
