package com.example.ag221353_ms172008_gg171680

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ag221353_ms172008_gg171680.models.Pregunta

class PreguntaAdapter(
    private val preguntas: List<Pregunta>,
    private val onItemClick: (Pregunta) -> Unit
) : RecyclerView.Adapter<PreguntaAdapter.PreguntaViewHolder>() {

    class PreguntaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewPregunta: TextView = view.findViewById(R.id.textViewPregunta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreguntaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pregunta, parent, false)
        return PreguntaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PreguntaViewHolder, position: Int) {
        val pregunta = preguntas[position]
        holder.textViewPregunta.text = pregunta.texto
        holder.itemView.setOnClickListener { onItemClick(pregunta) }
    }

    override fun getItemCount() = preguntas.size
}