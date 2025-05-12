package com.example.ag221353_ms172008_gg171680

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ag221353_ms172008_gg171680.database.DatabaseContract.PreguntaEntry
import com.example.ag221353_ms172008_gg171680.database.DatabaseContract.RespuestaEntry
import com.example.ag221353_ms172008_gg171680.models.Pregunta
import com.example.ag221353_ms172008_gg171680.models.Respuesta
import com.example.ag221353_ms172008_gg171680.database.DatabaseHelper

class EditarPreguntasActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonVolver: Button
    private var preguntasList = mutableListOf<Pregunta>()
    private lateinit var adapter: PreguntaAdapter
    private var evaluacionId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_preguntas)

        evaluacionId = intent.getIntExtra("EVALUACION_ID", -1)
        if (evaluacionId == -1) {
            Toast.makeText(this, "Error: No se especificó la evaluación", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbHelper = DatabaseHelper(this)
        db = dbHelper.writableDatabase

        recyclerView = findViewById(R.id.recyclerViewPreguntas)
        buttonVolver = findViewById(R.id.buttonVolver)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PreguntaAdapter(preguntasList) { pregunta ->
            mostrarOpcionesPregunta(pregunta)
        }
        recyclerView.adapter = adapter

        buttonVolver.setOnClickListener {
            finish()
        }

        cargarPreguntas()
    }

    private fun cargarPreguntas() {
        preguntasList.clear()
        val cursor: Cursor = db.query(
            PreguntaEntry.TABLE_NAME,
            null,
            "${PreguntaEntry.COLUMN_EVALUACION_ID} = ?",
            arrayOf(evaluacionId.toString()),
            null, null, null
        )

        try {
            while (cursor.moveToNext()) {
                preguntasList.add(
                    Pregunta(
                        cursor.getInt(cursor.getColumnIndexOrThrow(PreguntaEntry.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PreguntaEntry.COLUMN_TEXTO)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PreguntaEntry.COLUMN_EVALUACION_ID))
                    )
                )
            }
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar preguntas", Toast.LENGTH_SHORT).show()
        } finally {
            cursor.close()
        }
    }

    private fun mostrarOpcionesPregunta(pregunta: Pregunta) {
        AlertDialog.Builder(this)
            .setTitle(pregunta.texto)
            .setItems(arrayOf("Editar pregunta y respuestas", "Eliminar")) { _, which ->
                when (which) {
                    0 -> mostrarDialogoEditarPregunta(pregunta)
                    1 -> confirmarEliminacionPregunta(pregunta.id)
                }
            }
            .show()
    }

    private fun mostrarDialogoEditarPregunta(pregunta: Pregunta) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_editar_pregunta_respuestas, null)
        val editTextPregunta = dialogView.findViewById<EditText>(R.id.editTextPregunta)
        val containerRespuestas = dialogView.findViewById<LinearLayout>(R.id.containerRespuestas)

        editTextPregunta.setText(pregunta.texto)
        val respuestas = obtenerRespuestas(pregunta.id).toMutableList()

        // Lista para mantener referencias a los EditText de las respuestas
        val respuestaEditTexts = mutableListOf<EditText>()

        fun actualizarVistaRespuestas() {
            containerRespuestas.removeAllViews()
            respuestaEditTexts.clear()

            respuestas.forEachIndexed { index, respuesta ->
                val view = LayoutInflater.from(this).inflate(R.layout.item_respuesta_editable, containerRespuestas, false)
                val editText = view.findViewById<EditText>(R.id.editTextRespuesta)
                val checkbox = view.findViewById<CheckBox>(R.id.checkboxCorrecta)

                editText.setText(respuesta.texto)
                checkbox.isChecked = respuesta.esCorrecta

                respuestaEditTexts.add(editText)

                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    respuestas[index] = respuestas[index].copy(esCorrecta = isChecked)
                }

                containerRespuestas.addView(view)
            }
        }

        actualizarVistaRespuestas()

        AlertDialog.Builder(this)
            .setTitle("Editar pregunta")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                // Actualizar los textos de todas las respuestas antes de guardar
                respuestaEditTexts.forEachIndexed { index, editText ->
                    respuestas[index] = respuestas[index].copy(texto = editText.text.toString())
                }

                guardarCambiosPregunta(
                    pregunta.id,
                    editTextPregunta.text.toString(),
                    respuestas
                )
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun obtenerRespuestas(preguntaId: Int): MutableList<Respuesta> {
        val respuestas = mutableListOf<Respuesta>()
        val cursor = db.query(
            RespuestaEntry.TABLE_NAME,
            null,
            "${RespuestaEntry.COLUMN_PREGUNTA_ID} = ?",
            arrayOf(preguntaId.toString()),
            null, null, null
        )

        cursor.use {
            while (it.moveToNext()) {
                respuestas.add(Respuesta(
                    it.getInt(it.getColumnIndexOrThrow(RespuestaEntry.COLUMN_ID)),
                    it.getString(it.getColumnIndexOrThrow(RespuestaEntry.COLUMN_TEXTO)),
                    it.getInt(it.getColumnIndexOrThrow(RespuestaEntry.COLUMN_CORRECTA)) == 1,
                    preguntaId
                ))
            }
        }
        return respuestas
    }

    private fun guardarCambiosPregunta(preguntaId: Int, nuevoTexto: String, respuestas: List<Respuesta>) {
        if (nuevoTexto.isEmpty()) {
            Toast.makeText(this, "El texto de la pregunta no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar que al menos una respuesta sea correcta
        if (respuestas.none { it.esCorrecta }) {
            Toast.makeText(this, "Debe haber al menos una respuesta correcta", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            db.beginTransaction()

            // Actualizar el texto de la pregunta
            val preguntaValues = ContentValues().apply {
                put(PreguntaEntry.COLUMN_TEXTO, nuevoTexto)
            }
            db.update(
                PreguntaEntry.TABLE_NAME,
                preguntaValues,
                "${PreguntaEntry.COLUMN_ID} = ?",
                arrayOf(preguntaId.toString())
            )

            // Eliminar respuestas antiguas
            db.delete(
                RespuestaEntry.TABLE_NAME,
                "${RespuestaEntry.COLUMN_PREGUNTA_ID} = ?",
                arrayOf(preguntaId.toString())
            )

            // Insertar respuestas actualizadas
            for (respuesta in respuestas) {
                if (respuesta.texto.isNotEmpty()) {
                    val respuestaValues = ContentValues().apply {
                        put(RespuestaEntry.COLUMN_TEXTO, respuesta.texto)
                        put(RespuestaEntry.COLUMN_CORRECTA, if (respuesta.esCorrecta) 1 else 0)
                        put(RespuestaEntry.COLUMN_PREGUNTA_ID, preguntaId)
                    }
                    db.insert(RespuestaEntry.TABLE_NAME, null, respuestaValues)
                }
            }

            db.setTransactionSuccessful()
            Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
            cargarPreguntas()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar cambios: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            db.endTransaction()
        }
    }

    private fun confirmarEliminacionPregunta(preguntaId: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Eliminar esta pregunta y todas sus respuestas?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarPregunta(preguntaId)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarPregunta(preguntaId: Int) {
        try {
            db.beginTransaction()

            // Eliminar respuestas primero
            db.delete(
                RespuestaEntry.TABLE_NAME,
                "${RespuestaEntry.COLUMN_PREGUNTA_ID} = ?",
                arrayOf(preguntaId.toString())
            )

            // Luego eliminar la pregunta
            val rowsDeleted = db.delete(
                PreguntaEntry.TABLE_NAME,
                "${PreguntaEntry.COLUMN_ID} = ?",
                arrayOf(preguntaId.toString())
            )

            if (rowsDeleted > 0) {
                db.setTransactionSuccessful()
                Toast.makeText(this, "Pregunta eliminada", Toast.LENGTH_SHORT).show()
                cargarPreguntas()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al eliminar pregunta", Toast.LENGTH_SHORT).show()
        } finally {
            db.endTransaction()
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}