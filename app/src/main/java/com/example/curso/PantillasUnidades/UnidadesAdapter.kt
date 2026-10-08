package com.example.curso.PantillasUnidades

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.curso.R

class UnidadesAdapter(
    private val unidades: MutableList<UnidadCompleta>,
    private val desbloqueadas: MutableSet<String>,
    private val onUnidadClick: (UnidadCompleta) -> Unit
) : RecyclerView.Adapter<UnidadesAdapter.UnidadViewHolder>() {

    private val imagenes = listOf(
        R.drawable.unidad1,
        R.drawable.unidad2,
        R.drawable.unidad3,
        R.drawable.unidad6,
        R.drawable.unidad5,
        R.drawable.unidad4
    )

    fun actualizarUnidades(nuevasUnidades: List<UnidadCompleta>) {
        unidades.clear()
        unidades.addAll(nuevasUnidades)
        notifyDataSetChanged()
    }

    fun actualizarDesbloqueadas(nuevasDesbloqueadas: Set<String>) {
        desbloqueadas.clear()
        desbloqueadas.addAll(nuevasDesbloqueadas)
        notifyDataSetChanged()
    }

    fun estaDesbloqueada(clave: String): Boolean {
        return desbloqueadas.contains(clave)
    }

    class UnidadViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTituloUnidad)
        val imgUnidad: ImageView = view.findViewById(R.id.imgUnidad)
        val btnVerUnidad: Button = view.findViewById(R.id.btnVerUnidad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnidadViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_unidad, parent, false)
        return UnidadViewHolder(view)
    }

    override fun onBindViewHolder(holder: UnidadViewHolder, position: Int) {
        val unidad = unidades[position]

        holder.tvTitulo.text = unidad.titulo

        val randomIndex = unidad.order % imagenes.size
        holder.imgUnidad.setImageResource(imagenes[randomIndex])

        val desbloqueada = desbloqueadas.contains(unidad.order.toString())

        if (desbloqueada) {
            holder.itemView.alpha = 1f
            holder.btnVerUnidad.isEnabled = true
            holder.btnVerUnidad.alpha = 1f
        } else {
            holder.itemView.alpha = 0.5f
            holder.btnVerUnidad.isEnabled = false
            holder.btnVerUnidad.alpha = 0.5f
        }

        holder.btnVerUnidad.setOnClickListener {
            if (desbloqueada) onUnidadClick(unidad)
        }
    }

    override fun getItemCount() = unidades.size
}
