package com.example.curso.PantillasUnidades

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.curso.R

class UnidadIntroActivity : AppCompatActivity() {

    private lateinit var txtTitulo: TextView
    private lateinit var txtConceptos: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnIniciarUnidad: Button
    private var order: Int = 0

    private var titulo: String? = null
    private var videoUrl: String? = null
    private var unidadId: String? = null
    private var conceptosList: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unidad_intro)

        txtTitulo = findViewById(R.id.txtTitulo)
        txtConceptos = findViewById(R.id.txtConceptos)
        progressBar = findViewById(R.id.progressBar)
        btnIniciarUnidad = findViewById(R.id.btnIniciarUnidad)
        val btnVolverIntro = findViewById<Button>(R.id.btnVolverIntro)

        unidadId = intent.getStringExtra("unidad_id")

        if (unidadId.isNullOrEmpty()) {
            Toast.makeText(this, "Error: unidad no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarUnidad(unidadId!!)

        btnIniciarUnidad.setOnClickListener {
            btnIniciarUnidad.isEnabled = false
            val intent = Intent(this, UnidadVideoActivity::class.java)
            intent.putExtra("titulo", titulo)
            intent.putExtra("video_url", videoUrl)
            intent.putExtra("unidad_id", unidadId)
            intent.putExtra("order", order)   // <- nuevo
            startActivity(intent)
        }

        btnVolverIntro.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun cargarUnidad(unidadId: String) {
        val unidad = AdministracionMemoria.obtenerUnidadPorId(unidadId)

        if (unidad == null) {
            Toast.makeText(this, "Unidad no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        titulo = unidad.titulo
        videoUrl = unidad.videoUrl
        conceptosList = unidad.conceptos
        order = unidad.order

        txtTitulo.text = titulo ?: "Sin título"
        txtConceptos.text =
            conceptosList?.joinToString("\n• ", prefix = "• ") ?: "Sin conceptos"
        progressBar.progress = 0
    }

    override fun onResume() {
        super.onResume()
        btnIniciarUnidad.isEnabled = true
    }
}