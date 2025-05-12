package com.example.ag221353_ms172008_gg171680.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ag221353_ms172008_gg171680.R
import com.example.ag221353_ms172008_gg171680.models.DetallePregunta

class DetallePreguntaAdapter(private val detalles: List<DetallePregunta>) :
    RecyclerView.Adapter<DetallePreguntaAdapter.DetallePreguntaViewHolder>() {

    class DetallePreguntaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNumeroPregunta: TextView = view.findViewById(R.id.tvNumeroPregunta)
        val tvEstadoRespuesta: TextView = view.findViewById(R.id.tvEstadoRespuesta)
        val tvTextoPregunta: TextView = view.findViewById(R.id.tvTextoPregunta)
        val tvTuRespuesta: TextView = view.findViewById(R.id.tvTuRespuesta)
        val tvRespuestaCorrecta: TextView = view.findViewById(R.id.tvRespuestaCorrecta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetallePreguntaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detalle_pregunta, parent, false)
        return DetallePreguntaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetallePreguntaViewHolder, position: Int) {
        val detalle = detalles[position]

        holder.tvNumeroPregunta.text = "Pregunta ${position + 1}"
        holder.tvTextoPregunta.text = detalle.textoPregunta
        holder.tvTuRespuesta.text = "Tu respuesta: ${detalle.respuestaSeleccionada}"
        holder.tvRespuestaCorrecta.text = "Respuesta correcta: ${detalle.respuestaCorrecta}"

        if (detalle.esCorrecta) {
            holder.tvEstadoRespuesta.text = "✓ Correcta"
            holder.tvEstadoRespuesta.setTextColor(holder.itemView.context.getColor(R.color.correcta))
        } else {
            holder.tvEstadoRespuesta.text = "✗ Incorrecta"
            holder.tvEstadoRespuesta.setTextColor(holder.itemView.context.getColor(R.color.incorrecta))
        }
    }

    override fun getItemCount() = detalles.size
}