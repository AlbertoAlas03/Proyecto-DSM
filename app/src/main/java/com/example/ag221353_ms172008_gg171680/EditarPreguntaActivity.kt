package com.example.ag221353_ms172008_gg171680

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ag221353_ms172008_gg171680.database.DatabaseContract
import com.example.ag221353_ms172008_gg171680.database.DatabaseHelper

class EditarPreguntaActivity : AppCompatActivity() {
    private var editTextPregunta: EditText? = null
    private var editTextOpcion1: EditText? = null
    private var editTextOpcion2: EditText? = null
    private var editTextOpcion3: EditText? = null
    private var radioGroupRespuestas: RadioGroup? = null
    private var buttonGuardarCambios: Button? = null
    private var buttonEliminarPregunta: Button? = null
    private var dbHelper: DatabaseHelper? = null
    private var preguntaId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_pregunta)

        preguntaId = intent.getIntExtra("PREGUNTA_ID", -1)
        dbHelper = DatabaseHelper(this)

        editTextPregunta = findViewById(R.id.editTextPregunta)
        editTextOpcion1 = findViewById(R.id.editTextOpcion1)
        editTextOpcion2 = findViewById(R.id.editTextOpcion2)
        editTextOpcion3 = findViewById(R.id.editTextOpcion3)
        radioGroupRespuestas = findViewById(R.id.radioGroupRespuestas)
        buttonGuardarCambios = findViewById<Button>(R.id.buttonGuardarCambios)
        buttonEliminarPregunta = findViewById<Button>(R.id.buttonEliminarPregunta)

        cargarPregunta()

        buttonGuardarCambios?.setOnClickListener(View.OnClickListener { v: View? -> guardarCambios() })
        buttonEliminarPregunta?.setOnClickListener(View.OnClickListener { v: View? -> eliminarPregunta() })
    }

    private fun cargarPregunta() {
        val db = dbHelper!!.readableDatabase

        // Cargar texto de la pregunta
        val cursorPregunta = db.query(
            DatabaseContract.PreguntaEntry.TABLE_NAME,
            arrayOf(DatabaseContract.PreguntaEntry.COLUMN_TEXTO),
            DatabaseContract.PreguntaEntry.COLUMN_ID + "=?",
            arrayOf(preguntaId.toString()),
            null, null, null
        )

        if (cursorPregunta.moveToFirst()) {
            editTextPregunta!!.setText(cursorPregunta.getString(0))
        }
        cursorPregunta.close()

        // Cargar opciones de respuesta
        val cursorRespuestas = db.query(
            DatabaseContract.RespuestaEntry.TABLE_NAME,
            arrayOf(
                DatabaseContract.RespuestaEntry.COLUMN_TEXTO,
                DatabaseContract.RespuestaEntry.COLUMN_CORRECTA
            ),
            DatabaseContract.RespuestaEntry.COLUMN_PREGUNTA_ID + "=?",
            arrayOf(preguntaId.toString()),
            null, null, null
        )

        if (cursorRespuestas.moveToFirst()) {
            editTextOpcion1!!.setText(cursorRespuestas.getString(0))
            if (cursorRespuestas.getInt(1) == 1) radioGroupRespuestas!!.check(R.id.radioOpcion1)

            if (cursorRespuestas.moveToNext()) {
                editTextOpcion2!!.setText(cursorRespuestas.getString(0))
                if (cursorRespuestas.getInt(1) == 1) radioGroupRespuestas!!.check(R.id.radioOpcion2)
            }

            if (cursorRespuestas.moveToNext()) {
                editTextOpcion3!!.setText(cursorRespuestas.getString(0))
                if (cursorRespuestas.getInt(1) == 1) radioGroupRespuestas!!.check(R.id.radioOpcion3)
            }
        }
        cursorRespuestas.close()
    }

    private fun guardarCambios() {

    }

    private fun eliminarPregunta() {
        val db = dbHelper!!.writableDatabase
        db.delete(
            DatabaseContract.PreguntaEntry.TABLE_NAME,
            DatabaseContract.PreguntaEntry.COLUMN_ID + "=?",
            arrayOf(preguntaId.toString())
        )
        Toast.makeText(this, "Pregunta eliminada", Toast.LENGTH_SHORT).show()
        finish()
    }
}