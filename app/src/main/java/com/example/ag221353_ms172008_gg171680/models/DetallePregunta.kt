package com.example.ag221353_ms172008_gg171680.models

data class DetallePregunta(
    val textoPregunta: String,
    val respuestaSeleccionada: String,
    val respuestaCorrecta: String,
    val esCorrecta: Boolean
)