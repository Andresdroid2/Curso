package com.example.curso.PantillasUnidades

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.curso.R
import com.example.curso.RepositorioUsuarios
import com.example.curso.unidades.agradecimientos
import com.example.curso.unidades.unidades

class ParcialActivity : AppCompatActivity() {

    private var unidadId: String? = null
    private lateinit var tvTitulo: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var btnEnviar: Button
    private val preguntas = mutableListOf<Pregunta>()
    private lateinit var adapter: PreguntasParcialAdapter
    private var unidadOrder: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parcial)

        tvTitulo = findViewById(R.id.tvParcialTitulo)
        recycler = findViewById(R.id.recyclerParcial)
        btnEnviar = findViewById(R.id.btnEnviarParcial)

        recycler.layoutManager = LinearLayoutManager(this)
        adapter = PreguntasParcialAdapter(preguntas)
        recycler.adapter = adapter

        unidadId = intent.getStringExtra("unidad_id")
        if (unidadId.isNullOrEmpty()) {
            Toast.makeText(this, "No se recibió ID de unidad", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarUnidadYPreguntas()

        btnEnviar.setOnClickListener { evaluarRespuestas() }
    }

    private fun cargarUnidadYPreguntas() {
        val unidad = AdministracionMemoria.obtenerUnidadPorId(unidadId!!)

        if (unidad == null) {
            Toast.makeText(this, "Unidad no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        unidadOrder = unidad.order
        tvTitulo.text = unidad.titulo

        preguntas.clear()
        preguntas.addAll(unidad.evaluacion)

        adapter = PreguntasParcialAdapter(preguntas)
        recycler.adapter = adapter
    }

    private fun evaluarRespuestas() {
        val respuestasUsuario = adapter.getRespuestas()

        if (respuestasUsuario.isEmpty() || respuestasUsuario.any { it == -1 }) {
            Toast.makeText(
                this,
                "Debes responder todas las preguntas antes de enviar",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        var correctas = 0
        val total = preguntas.size
        if (total == 0) return

        for (i in preguntas.indices) {
            if (respuestasUsuario[i] == preguntas[i].respuestaCorrecta) correctas++
        }

        val porcentaje = (correctas * 100) / total

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Resultado del Parcial")

        if (porcentaje >= 70) {
            builder.setMessage("¡Felicidades! Aprobaste con $correctas de $total preguntas correctas ($porcentaje%).")
            builder.setPositiveButton("Continuar") { _, _ ->
                aprobarUnidadYSeguir()
            }
        } else {
            builder.setMessage("Obtuviste $correctas de $total ($porcentaje%). No alcanzaste el 70% para aprobar.")
            builder.setPositiveButton("Reintentar") { _, _ ->
                reintentarParcial()
            }
            builder.setNegativeButton("Salir") { _, _ ->
                irAUnidades()
            }
        }

        builder.setCancelable(false)
        builder.show()
    }

    // Marca la unidad como completada y desbloquea la siguiente
    private fun aprobarUnidadYSeguir() {
        val usuario = RepositorioUsuarios.usuarioActual

        if (usuario == null) {
            guardarProgresoLocal(unidadOrder)
            irAUnidades()
            return
        }

        val maxOrder = AdministracionMemoria.unidades.value.maxOfOrNull { it.order } ?: 0

        if (unidadOrder > usuario.ultimaUnidadCompletada) {
            usuario.ultimaUnidadCompletada = unidadOrder
        }
        usuario.unidadesDesbloqueadas.add(unidadOrder)

        if (unidadOrder >= maxOrder) {
            irAAgradecimientos()
        } else {
            usuario.unidadesDesbloqueadas.add(unidadOrder + 1)
            Toast.makeText(this, "Unidad completada correctamente", Toast.LENGTH_SHORT).show()
            irAUnidades()
        }
    }

    private fun irAUnidades() {
        val intent = Intent(this, unidades::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun irAAgradecimientos() {
        val intent = Intent(this, agradecimientos::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun reintentarParcial() {
        cargarUnidadYPreguntas()
        Toast.makeText(this, "Parcial reiniciado", Toast.LENGTH_SHORT).show()
    }

    // Progreso local si no hay usuario en sesión
    private fun guardarProgresoLocal(order: Int) {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val current = prefs.getInt("lastCompletedOrder", -1)
        if (order > current) prefs.edit().putInt("lastCompletedOrder", order).apply()
    }
}