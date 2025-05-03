package com.example.ag221353_ms172008_gg171680.models

data class Pregunta(
    val id: Int,
    val texto: String,
    val evaluacionId: Int, // ForeignKey a Evaluacion
    val respuestas: List<Respuesta> = emptyList() // Relación 1-N con Respuesta
)