package com.example.ag221353_ms172008_gg171680

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ag221353_ms172008_gg171680.database.DatabaseContract
import com.example.ag221353_ms172008_gg171680.database.DatabaseHelper

class AgregarPreguntaActivity : AppCompatActivity() {
    private var editTextPregunta: EditText? = null
    private var editTextOpcion1: EditText? = null
    private var editTextOpcion2: EditText? = null
    private var editTextOpcion3: EditText? = null
    private var radioGroupRespuestas: RadioGroup? = null
    private var buttonGuardarPregunta: Button? = null
    private var dbHelper: DatabaseHelper? = null
    private var evaluacionId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_pregunta)

        // Obtener el ID de la evaluación desde el Intent
        evaluacionId = intent.getIntExtra("EVALUACION_ID", -1)

        editTextPregunta = findViewById<EditText>(R.id.editTextPregunta)
        editTextOpcion1 = findViewById<EditText>(R.id.editTextOpcion1)
        editTextOpcion2 = findViewById<EditText>(R.id.editTextOpcion2)
        editTextOpcion3 = findViewById<EditText>(R.id.editTextOpcion3)
        radioGroupRespuestas = findViewById<RadioGroup>(R.id.radioGroupRespuestas)
        buttonGuardarPregunta = findViewById<Button>(R.id.buttonGuardarPregunta)
        dbHelper = DatabaseHelper(this)

        buttonGuardarPregunta?.setOnClickListener(View.OnClickListener { v: View? -> guardarPregunta() })
    }

    private fun guardarPregunta() {
        val preguntaTexto = editTextPregunta!!.text.toString().trim { it <= ' ' }
        val opcion1 = editTextOpcion1!!.text.toString().trim { it <= ' ' }
        val opcion2 = editTextOpcion2!!.text.toString().trim { it <= ' ' }
        val opcion3 = editTextOpcion3!!.text.toString().trim { it <= ' ' }
        val respuestaSeleccionadaId = radioGroupRespuestas!!.checkedRadioButtonId

        if (preguntaTexto.isEmpty() || opcion1.isEmpty() || opcion2.isEmpty() || opcion3.isEmpty() || respuestaSeleccionadaId == -1) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }


        val respuestaCorrectaIndex =
            radioGroupRespuestas!!.indexOfChild(findViewById(respuestaSeleccionadaId))

        val db = dbHelper!!.writableDatabase

        // Insertar la pregunta
        val preguntaValues = ContentValues()
        preguntaValues.put(DatabaseContract.PreguntaEntry.COLUMN_TEXTO, preguntaTexto)
        preguntaValues.put(DatabaseContract.PreguntaEntry.COLUMN_EVALUACION_ID, evaluacionId)
        val preguntaId = db.insert(DatabaseContract.PreguntaEntry.TABLE_NAME, null, preguntaValues)

        if (preguntaId == -1L) {
            Toast.makeText(this, "Error al guardar la pregunta", Toast.LENGTH_SHORT).show()
            return
        }

        // Insertar las opciones de respuesta
        guardarRespuesta(db, preguntaId, opcion1, respuestaCorrectaIndex == 0)
        guardarRespuesta(db, preguntaId, opcion2, respuestaCorrectaIndex == 1)
        guardarRespuesta(db, preguntaId, opcion3, respuestaCorrectaIndex == 2)

        Toast.makeText(this, "Pregunta guardada", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun guardarRespuesta(
        db: SQLiteDatabase,
        preguntaId: Long,
        texto: String,
        esCorrecta: Boolean
    ) {
        val values = ContentValues()
        values.put(DatabaseContract.RespuestaEntry.COLUMN_TEXTO, texto)
        values.put(DatabaseContract.RespuestaEntry.COLUMN_CORRECTA, if (esCorrecta) 1 else 0)
        values.put(DatabaseContract.RespuestaEntry.COLUMN_PREGUNTA_ID, preguntaId)
        db.insert(DatabaseContract.RespuestaEntry.TABLE_NAME, null, values)
    }
}