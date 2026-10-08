package com.example.curso.PantillasUnidades

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.curso.R

class PreguntasParcialAdapter(private val items: List<Pregunta>) :
    RecyclerView.Adapter<PreguntasParcialAdapter.VH>() {

    private val seleccion = MutableList(items.size) { -1 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pregunta_parcial, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position], position)
    }

    fun getRespuestas(): List<Int> = seleccion

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvPregunta: TextView = view.findViewById(R.id.tvPreguntaParcial)
        private val rgOpciones: RadioGroup = view.findViewById(R.id.rgOpciones)

        fun bind(p: Pregunta, pos: Int) {
            tvPregunta.text = p.texto
            // evita rebotes
            rgOpciones.setOnCheckedChangeListener(null)
            rgOpciones.removeAllViews()

            // Crear RadioButtons con IDs fijos por índice
            for (i in p.opciones.indices) {
                val rb = RadioButton(itemView.context).apply {
                    id = i // ID = índice
                    text = p.opciones[i]
                    setTextColor(Color.WHITE)
                    textSize = 16f
                    setPadding(8, 8, 8, 8)
                    buttonTintList = ColorStateList.valueOf(Color.WHITE)
                }
                rgOpciones.addView(rb)
            }

            // Restaurar selección previa
            val sel = seleccion.getOrNull(pos) ?: -1
            if (sel >= 0 && sel < rgOpciones.childCount) {
                // usa el ID que coincide con el índice
                rgOpciones.check(sel)
            } else {
                rgOpciones.clearCheck()
            }

            // Guardar selección nueva
            rgOpciones.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId >= 0 && pos < seleccion.size) {
                    seleccion[pos] = checkedId
                }
            }
        }
    }
}