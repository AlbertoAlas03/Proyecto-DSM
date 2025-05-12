package com.example.ag221353_ms172008_gg171680

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ag221353_ms172008_gg171680.models.Evaluacion

class EvaluacionAdapter(
    private val evaluaciones: List<Evaluacion>,
    private val onItemClick: (Evaluacion) -> Unit
) : RecyclerView.Adapter<EvaluacionAdapter.EvaluacionViewHolder>() {

    class EvaluacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewNombre: TextView = view.findViewById(R.id.textViewNombreEvaluacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvaluacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_evaluacion, parent, false)
        return EvaluacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: EvaluacionViewHolder, position: Int) {
        val evaluacion = evaluaciones[position]
        holder.textViewNombre.text = evaluacion.nombre
        holder.itemView.setOnClickListener { onItemClick(evaluacion) }
    }

    override fun getItemCount() = evaluaciones.size
}