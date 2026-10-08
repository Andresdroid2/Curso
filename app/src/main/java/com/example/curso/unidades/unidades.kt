package com.example.curso.unidades

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.curso.PantillasUnidades.AdministracionMemoria
import com.example.curso.PantillasUnidades.UnidadCompleta
import com.example.curso.PantillasUnidades.UnidadIntroActivity
import com.example.curso.PantillasUnidades.UnidadesAdapter
import com.example.curso.R
import com.example.curso.RepositorioUsuarios

class unidades : AppCompatActivity() {

    private lateinit var recyclerUnidades: RecyclerView
    private lateinit var adapter: UnidadesAdapter

    private val listaUnidades = mutableListOf<UnidadCompleta>()
    private val unidadesDesbloqueadas = mutableSetOf<String>()

    // Se guarda la referencia para poder dejar de observar en onDestroy
    private val observadorUnidades: (List<UnidadCompleta>) -> Unit = { lista ->
        listaUnidades.clear()
        listaUnidades.addAll(lista.sortedBy { it.order })
        adapter.actualizarUnidades(listaUnidades)
        cargarProgresoUsuario()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unidades)

        recyclerUnidades = findViewById(R.id.recyclerUnidades)
        recyclerUnidades.layoutManager = LinearLayoutManager(this)

        adapter = UnidadesAdapter(mutableListOf(), mutableSetOf()) { unidad ->
            val clave = unidad.order.toString()
            val desbloqueada = adapter.estaDesbloqueada(clave)
            if (desbloqueada) {
                val intent = Intent(this, UnidadIntroActivity::class.java)
                intent.putExtra("unidad_id", unidad.id)
                intent.putExtra("order", unidad.order)
                intent.putExtra("titulo", unidad.titulo)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Unidad bloqueada", Toast.LENGTH_SHORT).show()
            }
        }

        recyclerUnidades.adapter = adapter

        AdministracionMemoria.obtenerUnidades(observadorUnidades)
    }

    // Al volver de completar una unidad, se refresca el progreso
    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) cargarProgresoUsuario()
    }

    override fun onDestroy() {
        super.onDestroy()
        AdministracionMemoria.dejarDeObservar(observadorUnidades)
    }

    private fun cargarProgresoUsuario() {
        val usuario = RepositorioUsuarios.usuarioActual

        unidadesDesbloqueadas.clear()
        unidadesDesbloqueadas.add("0")
        usuario?.unidadesDesbloqueadas?.forEach { unidadesDesbloqueadas.add(it.toString()) }

        Log.d("Progreso", "Unidades desbloqueadas: $unidadesDesbloqueadas")
        adapter.actualizarDesbloqueadas(unidadesDesbloqueadas)
        adapter.notifyDataSetChanged()
    }
}