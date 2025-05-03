package com.example.ag221353_ms172008_gg171680

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ag221353_ms172008_gg171680.database.DatabaseContract
import com.example.ag221353_ms172008_gg171680.database.DatabaseHelper

class CrearEvaluacionActivity : AppCompatActivity() {

    private lateinit var editTextNombreEvaluacion: EditText
    private lateinit var buttonGuardarEvaluacion: Button
    private lateinit var dbHelper: DatabaseHelper
    private var usuarioId: Int = -1
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_evaluacion)

        initViews()
        initDatabase()
        validateUser()
        setupButton()
    }

    private fun initViews() {
        editTextNombreEvaluacion = findViewById(R.id.editTextNombreEvaluacion)
        buttonGuardarEvaluacion = findViewById(R.id.buttonGuardarEvaluacion)
    }

    private fun initDatabase() {
        dbHelper = DatabaseHelper(this)
        db = dbHelper.writableDatabase // Inicializa la base de datos aquí
    }

    private fun validateUser() {
        usuarioId = intent.getIntExtra("USUARIO_ID", -1).takeIf { it != -1 } ?: run {
            Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_LONG).show()
            finish()
            return
        }
    }

    private fun setupButton() {
        buttonGuardarEvaluacion.setOnClickListener {
            try {
                guardarEvaluacion()
            } catch (e: Exception) {
                Toast.makeText(this, "Error crítico: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("CrearEvaluacion", "Error al guardar evaluación", e)
            }
        }
    }

    private fun guardarEvaluacion() {
        val nombreEvaluacion = editTextNombreEvaluacion.text.toString().trim()

        when {
            nombreEvaluacion.isEmpty() -> {
                editTextNombreEvaluacion.error = "Nombre requerido"
                return
            }
            nombreEvaluacion.length < 3 -> {
                editTextNombreEvaluacion.error = "Mínimo 3 caracteres"
                return
            }
        }

        val values = ContentValues().apply {
            put(DatabaseContract.EvaluacionEntry.COLUMN_NOMBRE, nombreEvaluacion)
            put(DatabaseContract.EvaluacionEntry.COLUMN_USUARIO_ID, usuarioId)
        }

        db.use { database -> // Usa use para manejo automático de recursos
            try {
                database.beginTransaction()
                val newRowId = database.insert(
                    DatabaseContract.EvaluacionEntry.TABLE_NAME,
                    null,
                    values
                )

                if (newRowId == -1L) {
                    Toast.makeText(this, "Error en base de datos", Toast.LENGTH_SHORT).show()
                } else {
                    database.setTransactionSuccessful()
                    Toast.makeText(this, "¡Evaluación creada!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK) // Para notificar a MainActivity
                    finish()
                }
            } catch (e: Exception) {
                Log.e("DB_ERROR", "Error al insertar evaluación", e)
                Toast.makeText(this, "Error técnico al guardar", Toast.LENGTH_LONG).show()
            } finally {
                database.endTransaction()
            }
        }
    }

    override fun onDestroy() {
        dbHelper.close() // Cierra la conexión a la BD
        super.onDestroy()
    }
}