package com.example.curso

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.example.curso.PantillasUnidades.Pregunta

class PreguntasAdapter(
    private val preguntas: MutableList<Pregunta>,
    private val onEliminarClick: (Int) -> Unit,
    private val recyclerView: RecyclerView? = null // Se pasa el RecyclerView
) : RecyclerView.Adapter<PreguntasAdapter.PreguntaViewHolder>() {

    class PreguntaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val etPregunta: EditText = itemView.findViewById(R.id.etPregunta)
        val etOpcion1: EditText = itemView.findViewById(R.id.etOpcion1)
        val etOpcion2: EditText = itemView.findViewById(R.id.etOpcion2)
        val etOpcion3: EditText = itemView.findViewById(R.id.etOpcion3)
        val etOpcion4: EditText = itemView.findViewById(R.id.etOpcion4)
        val spCorrecta: Spinner = itemView.findViewById(R.id.spCorrecta)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminarPregunta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreguntaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pregunta, parent, false)
        return PreguntaViewHolder(view)
    }

    override fun getItemCount(): Int = preguntas.size

    override fun onBindViewHolder(holder: PreguntaViewHolder, position: Int) {
        val pregunta = preguntas[position]

        //Mostrar datos existentes
        holder.etPregunta.setText(pregunta.texto)
        if (pregunta.opciones.size == 4) {
            holder.etOpcion1.setText(pregunta.opciones[0])
            holder.etOpcion2.setText(pregunta.opciones[1])
            holder.etOpcion3.setText(pregunta.opciones[2])
            holder.etOpcion4.setText(pregunta.opciones[3])
        }

        //Configurar spinner
        val opcionesCorrectas = listOf("Opción 1", "Opción 2", "Opción 3", "Opción 4")
        val adapter = ArrayAdapter(
            holder.itemView.context,
            android.R.layout.simple_spinner_dropdown_item,
            opcionesCorrectas
        )
        holder.spCorrecta.adapter = adapter
        holder.spCorrecta.setSelection(pregunta.respuestaCorrecta)

        //Eliminar pregunta
        holder.btnEliminar.setOnClickListener {
            preguntas.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, preguntas.size)
            onEliminarClick(position)
        }
    }

    //Eliminar
    fun eliminarPregunta(posicion: Int) {
        if (posicion in preguntas.indices) {
            preguntas.removeAt(posicion)
            notifyItemRemoved(posicion)
        }
    }

    //Agregar nueva pregunta vacía
    fun agregarPregunta() {
        preguntas.add(Pregunta("", listOf("", "", "", ""), 0))
        notifyItemInserted(preguntas.size - 1)
    }

    //Limpiar todas las preguntas
    fun limpiarPreguntas() {
        preguntas.clear()
        notifyDataSetChanged()
    }

    //Obtener preguntas actualizadas
    fun obtenerPreguntas(): List<Pregunta> {
        val listaPreguntas = mutableListOf<Pregunta>()

        for (i in 0 until itemCount) {
            val holder = recyclerView?.findViewHolderForAdapterPosition(i) as? PreguntaViewHolder
            if (holder != null) {
                val texto = holder.etPregunta.text.toString().trim()
                val opciones = listOf(
                    holder.etOpcion1.text.toString().trim(),
                    holder.etOpcion2.text.toString().trim(),
                    holder.etOpcion3.text.toString().trim(),
                    holder.etOpcion4.text.toString().trim()
                )
                val respuestaCorrecta = holder.spCorrecta.selectedItemPosition

                listaPreguntas.add(Pregunta(texto, opciones, respuestaCorrecta))
            } else {
                listaPreguntas.add(preguntas[i])
            }
        }

        return listaPreguntas
    }
}