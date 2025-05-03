package com.example.ag221353_ms172008_gg171680.models

data class Usuario(
    val id: Int,
    val nombre: String,
    val contrasena: String // En producción, usa un hash (SHA-256/bcrypt)
)