package com.example.ag221353_ms172008_gg171680.database

class DatabaseContract private constructor() {
    /* Tabla: Usuarios */
    object UsuarioEntry {
        const val TABLE_NAME: String = "usuarios"
        const val COLUMN_ID: String = "usuario_id"
        const val COLUMN_NOMBRE: String = "nombre"
        const val COLUMN_CONTRASENA: String = "contrasena"
    }

    /* Tabla: Evaluaciones */
    object EvaluacionEntry {
        const val TABLE_NAME: String = "evaluaciones"
        const val COLUMN_ID: String = "evaluacion_id"
        const val COLUMN_NOMBRE: String = "nombre"
        const val COLUMN_USUARIO_ID: String = "usuario_id" // FK
    }

    /* Tabla: Preguntas */
    object PreguntaEntry {
        const val TABLE_NAME: String = "preguntas"
        const val COLUMN_ID: String = "pregunta_id"
        const val COLUMN_TEXTO: String = "texto"
        const val COLUMN_EVALUACION_ID: String = "evaluacion_id" // FK
    }

    /* Tabla: Respuestas */
    object RespuestaEntry {
        const val TABLE_NAME: String = "respuestas"
        const val COLUMN_ID: String = "respuesta_id"
        const val COLUMN_TEXTO: String = "texto"
        const val COLUMN_CORRECTA: String = "correcta" // 1 = Verdadero, 0 = Falso
        const val COLUMN_PREGUNTA_ID: String = "pregunta_id" // FK
    }
}