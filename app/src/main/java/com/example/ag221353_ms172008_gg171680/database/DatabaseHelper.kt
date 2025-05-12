package com.example.ag221353_ms172008_gg171680.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ag221353_ms172008_gg171680.database.DatabaseContract.EvaluacionEntry
import com.example.ag221353_ms172008_gg171680.database.DatabaseContract.PreguntaEntry
import com.example.ag221353_ms172008_gg171680.database.DatabaseContract.RespuestaEntry
import com.example.ag221353_ms172008_gg171680.database.DatabaseContract.UsuarioEntry

class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(SQL_CREATE_USUARIOS)
        db.execSQL(SQL_CREATE_EVALUACIONES)
        db.execSQL(SQL_CREATE_PREGUNTAS)
        db.execSQL(SQL_CREATE_RESPUESTAS)

        // Usuario de prueba
        db.execSQL(
            "INSERT INTO " + UsuarioEntry.TABLE_NAME + " (" +
                    UsuarioEntry.COLUMN_NOMBRE + ", " + UsuarioEntry.COLUMN_CONTRASENA + ") " +
                    "VALUES ('estudiante', '1234')"
        )

        // Crear una evaluación de prueba
        db.execSQL(
            "INSERT INTO " + EvaluacionEntry.TABLE_NAME + " (" +
                    EvaluacionEntry.COLUMN_NOMBRE + ", " +
                    EvaluacionEntry.COLUMN_USUARIO_ID + ") " +
                    "VALUES ('Evaluación de Programación Android', 1)"
        )

        // Crear preguntas de prueba para la evaluación
        // Pregunta 1
        db.execSQL(
            "INSERT INTO " + PreguntaEntry.TABLE_NAME + " (" +
                    PreguntaEntry.COLUMN_TEXTO + ", " +
                    PreguntaEntry.COLUMN_EVALUACION_ID + ") " +
                    "VALUES ('¿Cuál es el lenguaje de programación oficial para Android?', 1)"
        )

        // Respuestas para la pregunta 1
        db.execSQL("INSERT INTO " + RespuestaEntry.TABLE_NAME + " (" +
                RespuestaEntry.COLUMN_TEXTO + ", " +
                RespuestaEntry.COLUMN_CORRECTA + ", " +
                RespuestaEntry.COLUMN_PREGUNTA_ID + ") VALUES " +
                "('Java', 0, 1), " +
                "('Kotlin', 1, 1), " +
                "('Swift', 0, 1), " +
                "('C#', 0, 1)")

        // Pregunta 2
        db.execSQL(
            "INSERT INTO " + PreguntaEntry.TABLE_NAME + " (" +
                    PreguntaEntry.COLUMN_TEXTO + ", " +
                    PreguntaEntry.COLUMN_EVALUACION_ID + ") " +
                    "VALUES ('¿Qué componente se utiliza para almacenar datos estructurados en Android?', 1)"
        )

        // Respuestas para la pregunta 2
        db.execSQL("INSERT INTO " + RespuestaEntry.TABLE_NAME + " (" +
                RespuestaEntry.COLUMN_TEXTO + ", " +
                RespuestaEntry.COLUMN_CORRECTA + ", " +
                RespuestaEntry.COLUMN_PREGUNTA_ID + ") VALUES " +
                "('SharedPreferences', 0, 2), " +
                "('Bundle', 0, 2), " +
                "('SQLiteDatabase', 1, 2), " +
                "('Intent', 0, 2)")

        // Pregunta 3
        db.execSQL(
            "INSERT INTO " + PreguntaEntry.TABLE_NAME + " (" +
                    PreguntaEntry.COLUMN_TEXTO + ", " +
                    PreguntaEntry.COLUMN_EVALUACION_ID + ") " +
                    "VALUES ('¿Qué método se llama al crear una Activity por primera vez?', 1)"
        )

        // Respuestas para la pregunta 3
        db.execSQL("INSERT INTO " + RespuestaEntry.TABLE_NAME + " (" +
                RespuestaEntry.COLUMN_TEXTO + ", " +
                RespuestaEntry.COLUMN_CORRECTA + ", " +
                RespuestaEntry.COLUMN_PREGUNTA_ID + ") VALUES " +
                "('onStart()', 0, 3), " +
                "('onCreate()', 1, 3), " +
                "('onResume()', 0, 3), " +
                "('onPause()', 0, 3)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + RespuestaEntry.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + PreguntaEntry.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + EvaluacionEntry.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + UsuarioEntry.TABLE_NAME)
        onCreate(db)  // Se recrearán las tablas con las nuevas reglas CASCADE
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)

        db.setForeignKeyConstraintsEnabled(true)
    }

    companion object {
        private const val DATABASE_NAME = "evaluaciones.db"
        private const val DATABASE_VERSION = 6 // Incrementamos la versión para que se ejecuten los cambios


        private const val SQL_CREATE_USUARIOS = "CREATE TABLE " + UsuarioEntry.TABLE_NAME + " (" +
                UsuarioEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UsuarioEntry.COLUMN_NOMBRE + " TEXT NOT NULL, " +
                UsuarioEntry.COLUMN_CONTRASENA + " TEXT NOT NULL)"

        private const val SQL_CREATE_EVALUACIONES =
            "CREATE TABLE " + EvaluacionEntry.TABLE_NAME + " (" +
                    EvaluacionEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    EvaluacionEntry.COLUMN_NOMBRE + " TEXT NOT NULL, " +
                    EvaluacionEntry.COLUMN_USUARIO_ID + " INTEGER, " +
                    "FOREIGN KEY(" + EvaluacionEntry.COLUMN_USUARIO_ID + ") REFERENCES " +
                    UsuarioEntry.TABLE_NAME + "(" + UsuarioEntry.COLUMN_ID + ") ON DELETE CASCADE)"

        private const val SQL_CREATE_PREGUNTAS =
            "CREATE TABLE " + PreguntaEntry.TABLE_NAME + " (" +
                    PreguntaEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PreguntaEntry.COLUMN_TEXTO + " TEXT NOT NULL, " +
                    PreguntaEntry.COLUMN_EVALUACION_ID + " INTEGER, " +
                    "FOREIGN KEY(" + PreguntaEntry.COLUMN_EVALUACION_ID + ") REFERENCES " +
                    EvaluacionEntry.TABLE_NAME + "(" + EvaluacionEntry.COLUMN_ID + ") ON DELETE CASCADE)"

        private const val SQL_CREATE_RESPUESTAS =
            "CREATE TABLE " + RespuestaEntry.TABLE_NAME + " (" +
                    RespuestaEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RespuestaEntry.COLUMN_TEXTO + " TEXT NOT NULL, " +
                    RespuestaEntry.COLUMN_CORRECTA + " INTEGER DEFAULT 0, " +
                    RespuestaEntry.COLUMN_PREGUNTA_ID + " INTEGER, " +
                    "FOREIGN KEY(" + RespuestaEntry.COLUMN_PREGUNTA_ID + ") REFERENCES " +
                    PreguntaEntry.TABLE_NAME + "(" + PreguntaEntry.COLUMN_ID + ") ON DELETE CASCADE)"
    }
}