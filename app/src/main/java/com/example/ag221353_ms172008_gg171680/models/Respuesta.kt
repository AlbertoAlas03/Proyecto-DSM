package com.example.ag221353_ms172008_gg171680.models

data class Respuesta(
    val id: Int,
    val texto: String,
    val esCorrecta: Boolean, // 1 = true, 0 = false en SQLite
    val preguntaId: Int // ForeignKey a Pregunta
)