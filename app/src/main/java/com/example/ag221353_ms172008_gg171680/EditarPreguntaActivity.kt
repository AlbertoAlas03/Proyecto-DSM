package com.example.ag221353_ms172008_gg171680

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ag221353_ms172008_gg171680.database.DatabaseContract.PreguntaEntry
import com.example.ag221353_ms172008_gg171680.database.DatabaseHelper

class EditarPreguntaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase
    private lateinit var editTextPregunta: EditText
    private lateinit var buttonGuardar: Button
    private var preguntaId: Int = -1
    private var evaluacionId: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_pregunta)

        // Obtener IDs del Intent
        preguntaId = intent.getIntExtra("PREGUNTA_ID", -1)
        evaluacionId = intent.getIntExtra("EVALUACION_ID", -1)

        if (preguntaId == -1 || evaluacionId == -1) {
            Toast.makeText(this, "Error: No se especificó la pregunta", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inicializar base de datos
        dbHelper = DatabaseHelper(this)
        db = dbHelper.writableDatabase

        // Configurar UI
        editTextPregunta = findViewById(R.id.editTextPregunta)
        buttonGuardar = findViewById(R.id.buttonGuardar)

        // Cargar datos de la pregunta
        cargarPregunta()

        // Configurar botón
        buttonGuardar.setOnClickListener {
            guardarCambios()
        }
    }

    private fun cargarPregunta() {
        val cursor = db.query(
            PreguntaEntry.TABLE_NAME,
            null,
            "${PreguntaEntry.COLUMN_ID} = ?",
            arrayOf(preguntaId.toString()),
            null, null, null
        )

        try {
            if (cursor.moveToFirst()) {
                val textoPregunta = cursor.getString(
                    cursor.getColumnIndexOrThrow(PreguntaEntry.COLUMN_TEXTO)
                )
                editTextPregunta.setText(textoPregunta)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar la pregunta", Toast.LENGTH_SHORT).show()
        } finally {
            cursor.close()
        }
    }

    private fun guardarCambios() {
        val nuevoTexto = editTextPregunta.text.toString().trim()

        if (nuevoTexto.isEmpty()) {
            Toast.makeText(this, "El texto de la pregunta no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val values = ContentValues().apply {
            put(PreguntaEntry.COLUMN_TEXTO, nuevoTexto)
        }

        try {
            val rowsAffected = db.update(
                PreguntaEntry.TABLE_NAME,
                values,
                "${PreguntaEntry.COLUMN_ID} = ?",
                arrayOf(preguntaId.toString())
            )

            if (rowsAffected > 0) {
                Toast.makeText(this, "Pregunta actualizada", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al actualizar la pregunta", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar cambios", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}