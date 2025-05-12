package com.example.ag221353_ms172008_gg171680

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ag221353_ms172008_gg171680.adapters.DetallePreguntaAdapter
import com.example.ag221353_ms172008_gg171680.models.DetallePregunta
import java.text.DecimalFormat

class ResultadoEvaluacionActivity : AppCompatActivity() {

    private lateinit var tvNombreEvaluacion: TextView
    private lateinit var tvNotaFinal: TextView
    private lateinit var tvRespuestasCorrectas: TextView
    private lateinit var rvDetallePreguntas: RecyclerView
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_evaluacion)

        // Inicializar vistas
        tvNombreEvaluacion = findViewById(R.id.tvNombreEvaluacion)
        tvNotaFinal = findViewById(R.id.tvNotaFinal)
        tvRespuestasCorrectas = findViewById(R.id.tvRespuestasCorrectas)
        rvDetallePreguntas = findViewById(R.id.rvDetallePreguntas)
        btnVolver = findViewById(R.id.btnVolver)

        // Obtener datos del intent
        val evaluacionNombre = intent.getStringExtra("EVALUACION_NOMBRE") ?: "Evaluación"
        val totalPreguntas = intent.getIntExtra("TOTAL_PREGUNTAS", 0)
        val respuestasCorrectas = intent.getIntExtra("RESPUESTAS_CORRECTAS", 0)
        val notaFinal = intent.getFloatExtra("NOTA_FINAL", 0f)

        @Suppress("UNCHECKED_CAST")
        val detallesRespuestas = intent.getSerializableExtra("DETALLES_RESPUESTAS") as ArrayList<DetallePregunta>

        // Mostrar información
        tvNombreEvaluacion.text = evaluacionNombre

        // Formatear nota con un decimal
        val df = DecimalFormat("#.#")
        tvNotaFinal.text = df.format(notaFinal)

        tvRespuestasCorrectas.text = "Respuestas correctas: $respuestasCorrectas/$totalPreguntas"

        // Configurar RecyclerView
        rvDetallePreguntas.layoutManager = LinearLayoutManager(this)
        rvDetallePreguntas.adapter = DetallePreguntaAdapter(detallesRespuestas)

        // Configurar botón para volver al menú principal
        btnVolver.setOnClickListener {
            finish()
        }
    }
}