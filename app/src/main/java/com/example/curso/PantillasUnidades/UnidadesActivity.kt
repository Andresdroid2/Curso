package com.example.curso.PantillasUnidades

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.curso.R
import com.example.curso.RepositorioUsuarios

class UnidadesActivity : AppCompatActivity() {

    private lateinit var recyclerUnidades: RecyclerView
    private lateinit var adapter: UnidadesAdapter
    private val unidades = mutableListOf<UnidadCompleta>()
    private val desbloqueadas = mutableSetOf<String>()

    private val observadorUnidades: (List<UnidadCompleta>) -> Unit = { lista ->
        unidades.clear()
        unidades.addAll(lista.sortedBy { it.order })
        cargarProgreso()
        adapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unidades)

        recyclerUnidades = findViewById(R.id.recyclerUnidades)
        recyclerUnidades.layoutManager = LinearLayoutManager(this)
        adapter = UnidadesAdapter(unidades, desbloqueadas) { unidad ->
            val intent = Intent(this, UnidadIntroActivity::class.java)
            intent.putExtra("unidad_id", unidad.id) // antes era "unidadId"
            startActivity(intent)
        }

        recyclerUnidades.adapter = adapter

        AdministracionMemoria.obtenerUnidades(observadorUnidades)
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            cargarProgreso()
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AdministracionMemoria.dejarDeObservar(observadorUnidades)
    }

    private fun cargarProgreso() {
        desbloqueadas.clear()
        desbloqueadas.add("0")
        RepositorioUsuarios.usuarioActual?.unidadesDesbloqueadas
            ?.forEach { desbloqueadas.add(it.toString()) }
    }
}