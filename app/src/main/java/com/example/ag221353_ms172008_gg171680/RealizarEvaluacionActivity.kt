package com.example.ag221353_ms172008_gg171680

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.ag221353_ms172008_gg171680.database.DatabaseContract.EvaluacionEntry
import com.example.ag221353_ms172008_gg171680.database.DatabaseContract.PreguntaEntry
import com.example.ag221353_ms172008_gg171680.database.DatabaseContract.RespuestaEntry
import com.example.ag221353_ms172008_gg171680.database.DatabaseHelper
import com.example.ag221353_ms172008_gg171680.models.DetallePregunta
import com.example.ag221353_ms172008_gg171680.models.Evaluacion
import com.example.ag221353_ms172008_gg171680.models.Pregunta
import com.example.ag221353_ms172008_gg171680.models.Respuesta
import java.io.Serializable

class RealizarEvaluacionActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase

    private lateinit var tvTituloEvaluacion: TextView
    private lateinit var tvProgreso: TextView
    private lateinit var tvPregunta: TextView
    private lateinit var rgRespuestas: RadioGroup
    private lateinit var tvFeedback: TextView
    private lateinit var btnSiguiente: Button
    private lateinit var cardPregunta: CardView

    private lateinit var evaluacion: Evaluacion
    private lateinit var preguntas: List<Pregunta>
    private var preguntaActual = 0
    private val respuestasUsuario = mutableListOf<Int>() // Almacena los IDs de las respuestas seleccionadas
    private val detallesRespuestas = mutableListOf<DetallePregunta>() // Para mostrar en el resultado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realizar_evaluacion)

        dbHelper = DatabaseHelper(this)
        db = dbHelper.readableDatabase

        tvTituloEvaluacion = findViewById(R.id.tvTituloEvaluacion)
        tvProgreso = findViewById(R.id.tvProgreso)
        tvPregunta = findViewById(R.id.tvPregunta)
        rgRespuestas = findViewById(R.id.rgRespuestas)
        tvFeedback = findViewById(R.id.tvFeedback)
        btnSiguiente = findViewById(R.id.btnSiguiente)
        cardPregunta = findViewById(R.id.cardPregunta)

        // Obtener la primera evaluación de la base de datos
        cargarPrimeraEvaluacion()

        // Configurar el botón siguiente
        btnSiguiente.setOnClickListener {
            if (rgRespuestas.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Debes seleccionar una respuesta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Si estamos mostrando feedback, pasar a la siguiente pregunta
            if (tvFeedback.visibility == View.VISIBLE) {
                if (preguntaActual < preguntas.size - 1) {
                    preguntaActual++
                    mostrarPregunta()
                } else {
                    // Mostrar resultados finales
                    mostrarResultadosFinales()
                }
            } else {
                // Verificar respuesta
                verificarRespuesta()
            }
        }
    }

    private fun cargarPrimeraEvaluacion() {
        // Consultar la primera evaluación
        val cursor = db.query(
            EvaluacionEntry.TABLE_NAME,
            arrayOf(EvaluacionEntry.COLUMN_ID, EvaluacionEntry.COLUMN_NOMBRE),
            null, null, null, null, EvaluacionEntry.COLUMN_ID + " ASC", "1"
        )

        if (cursor.moveToFirst()) {
            val evaluacionId = cursor.getInt(cursor.getColumnIndexOrThrow(EvaluacionEntry.COLUMN_ID))
            val evaluacionNombre = cursor.getString(cursor.getColumnIndexOrThrow(EvaluacionEntry.COLUMN_NOMBRE))

            evaluacion = Evaluacion(evaluacionId, evaluacionNombre, 1) // Asumimos usuario_id = 1
            tvTituloEvaluacion.text = evaluacionNombre

            // Cargar preguntas de esta evaluación
            cargarPreguntas(evaluacionId)
        } else {
            // No hay evaluaciones
            Toast.makeText(this, "No hay evaluaciones disponibles", Toast.LENGTH_SHORT).show()
            finish()
        }

        cursor.close()
    }

    private fun cargarPreguntas(evaluacionId: Int) {
        val preguntasList = mutableListOf<Pregunta>()

        // Consultar preguntas
        val cursor = db.query(
            PreguntaEntry.TABLE_NAME,
            arrayOf(PreguntaEntry.COLUMN_ID, PreguntaEntry.COLUMN_TEXTO),
            "${PreguntaEntry.COLUMN_EVALUACION_ID} = ?",
            arrayOf(evaluacionId.toString()),
            null, null, null
        )

        while (cursor.moveToNext()) {
            val preguntaId = cursor.getInt(cursor.getColumnIndexOrThrow(PreguntaEntry.COLUMN_ID))
            val preguntaTexto = cursor.getString(cursor.getColumnIndexOrThrow(PreguntaEntry.COLUMN_TEXTO))

            // Cargar respuestas para esta pregunta
            val respuestas = cargarRespuestas(preguntaId)

            preguntasList.add(Pregunta(preguntaId, preguntaTexto, evaluacionId, respuestas))
        }

        cursor.close()

        if (preguntasList.isEmpty()) {
            Toast.makeText(this, "Esta evaluación no tiene preguntas", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            preguntas = preguntasList
            mostrarPregunta()
        }
    }

    private fun cargarRespuestas(preguntaId: Int): List<Respuesta> {
        val respuestasList = mutableListOf<Respuesta>()

        // Consultar respuestas
        val cursor = db.query(
            RespuestaEntry.TABLE_NAME,
            arrayOf(RespuestaEntry.COLUMN_ID, RespuestaEntry.COLUMN_TEXTO, RespuestaEntry.COLUMN_CORRECTA),
            "${RespuestaEntry.COLUMN_PREGUNTA_ID} = ?",
            arrayOf(preguntaId.toString()),
            null, null, null
        )

        while (cursor.moveToNext()) {
            val respuestaId = cursor.getInt(cursor.getColumnIndexOrThrow(RespuestaEntry.COLUMN_ID))
            val respuestaTexto = cursor.getString(cursor.getColumnIndexOrThrow(RespuestaEntry.COLUMN_TEXTO))
            val esCorrecta = cursor.getInt(cursor.getColumnIndexOrThrow(RespuestaEntry.COLUMN_CORRECTA)) == 1

            respuestasList.add(Respuesta(respuestaId, respuestaTexto, esCorrecta, preguntaId))
        }

        cursor.close()
        return respuestasList
    }

    private fun mostrarPregunta() {
        // Limpiar estado anterior
        rgRespuestas.removeAllViews()
        rgRespuestas.clearCheck()
        tvFeedback.visibility = View.GONE

        // Mostrar información de la pregunta actual
        val pregunta = preguntas[preguntaActual]
        tvPregunta.text = pregunta.texto
        tvProgreso.text = "Pregunta ${preguntaActual + 1}/${preguntas.size}"

        // Cambiar texto del botón según la etapa
        btnSiguiente.text = "Verificar"

        // Agregar opciones de respuesta
        for (respuesta in pregunta.respuestas) {
            val radioButton = RadioButton(this)
            radioButton.id = respuesta.id
            radioButton.text = respuesta.texto
            radioButton.textSize = 16f
            rgRespuestas.addView(radioButton)
        }
    }

    private fun verificarRespuesta() {
        val pregunta = preguntas[preguntaActual]
        val respuestaSeleccionadaId = rgRespuestas.checkedRadioButtonId

        // Guardar la respuesta del usuario
        respuestasUsuario.add(respuestaSeleccionadaId)

        // Encontrar la respuesta seleccionada y la respuesta correcta
        val respuestaSeleccionada = pregunta.respuestas.find { it.id == respuestaSeleccionadaId }
        val respuestaCorrecta = pregunta.respuestas.find { it.esCorrecta }

        if (respuestaSeleccionada != null && respuestaCorrecta != null) {
            // Guardar detalle de respuesta para mostrar en resultados
            detallesRespuestas.add(
                DetallePregunta(
                    pregunta.texto,
                    respuestaSeleccionada.texto,
                    respuestaCorrecta.texto,
                    respuestaSeleccionada.esCorrecta
                )
            )

            // Mostrar feedback
            if (respuestaSeleccionada.esCorrecta) {
                tvFeedback.text = "¡Correcto!"
                tvFeedback.setTextColor(getColor(R.color.correcta))
            } else {
                tvFeedback.text = "Incorrecto. La respuesta correcta es: ${respuestaCorrecta.texto}"
                tvFeedback.setTextColor(getColor(R.color.incorrecta))
            }

            tvFeedback.visibility = View.VISIBLE

            // Deshabilitar selección de respuestas
            for (i in 0 until rgRespuestas.childCount) {
                rgRespuestas.getChildAt(i).isEnabled = false

                // Colorear respuestas
                val radioButton = rgRespuestas.getChildAt(i) as RadioButton
                if (radioButton.id == respuestaCorrecta.id) {
                    radioButton.setTextColor(getColor(R.color.correcta))
                } else if (radioButton.id == respuestaSeleccionadaId && !respuestaSeleccionada.esCorrecta) {
                    radioButton.setTextColor(getColor(R.color.incorrecta))
                }
            }

            // Cambiar texto del botón
            if (preguntaActual < preguntas.size - 1) {
                btnSiguiente.text = "Siguiente Pregunta"
            } else {
                btnSiguiente.text = "Ver Resultados"
            }
        }
    }

    private fun mostrarResultadosFinales() {
        // Calcular nota
        val totalPreguntas = preguntas.size
        val respuestasCorrectas = detallesRespuestas.count { it.esCorrecta }
        val notaFinal = (respuestasCorrectas.toFloat() / totalPreguntas) * 10

        // Crear intent para mostrar resultados
        val intent = Intent(this, ResultadoEvaluacionActivity::class.java).apply {
            putExtra("EVALUACION_NOMBRE", evaluacion.nombre)
            putExtra("TOTAL_PREGUNTAS", totalPreguntas)
            putExtra("RESPUESTAS_CORRECTAS", respuestasCorrectas)
            putExtra("NOTA_FINAL", notaFinal)
            putExtra("DETALLES_RESPUESTAS", ArrayList(detallesRespuestas) as Serializable)
        }

        startActivity(intent)
        finish() // Cerrar esta actividad
    }
}