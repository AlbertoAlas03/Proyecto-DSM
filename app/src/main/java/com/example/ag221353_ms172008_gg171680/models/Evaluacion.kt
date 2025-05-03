package com.example.ag221353_ms172008_gg171680.models

data class Evaluacion(
    val id: Int,
    val nombre: String,
    val usuarioId: Int // ForeignKey a Usuario
)