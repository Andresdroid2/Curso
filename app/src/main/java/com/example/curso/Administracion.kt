package com.example.curso

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.curso.PantillasUnidades.AdministracionMemoria
import com.example.curso.PantillasUnidades.UnidadCompleta


class Administracion : AppCompatActivity() {

    private lateinit var preguntasAdapter: PreguntasAdapter

    private lateinit var etTitulo: EditText
    private lateinit var etConceptos: EditText
    private lateinit var etVideo: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_administracion)

        etTitulo = findViewById(R.id.etTitulo)
        etConceptos = findViewById(R.id.etConceptos)
        etVideo = findViewById(R.id.etVideo)
        val recycler = findViewById<RecyclerView>(R.id.recyclerPreguntas)
        val btnAgregarPregunta = findViewById<Button>(R.id.btnAgregarPregunta)
        val btnCrear = findViewById<Button>(R.id.btnCrear)
        val btnEditar = findViewById<Button>(R.id.btnEditar)
        val btnEliminar = findViewById<Button>(R.id.btnEliminar)

        recycler.layoutManager = LinearLayoutManager(this)

        preguntasAdapter = PreguntasAdapter(
            preguntas = mutableListOf(),
            onEliminarClick = { posicion ->
                preguntasAdapter.eliminarPregunta(posicion)
            },
            recyclerView = recycler
        )
        recycler.adapter = preguntasAdapter

        btnAgregarPregunta.setOnClickListener {
            preguntasAdapter.agregarPregunta()
        }

        // --- CREAR UNIDAD ---
        btnCrear.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()

            if (titulo.isEmpty()) {
                Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idUnidad = generarIdDesdeTitulo(titulo)
            val conceptos = etConceptos.text.toString().split(",").map { it.trim() }
            val nuevoOrden = AdministracionMemoria.siguienteOrden()
            val (_, videoUrlLimpio) = procesarVideo(etVideo.text.toString())

            val unidad = UnidadCompleta(
                id = idUnidad,
                titulo = titulo,
                conceptos = conceptos,
                videoUrl = videoUrlLimpio,
                evaluacion = preguntasAdapter.obtenerPreguntas(),
                order = nuevoOrden
            )

            AdministracionMemoria.crearUnidadConId(unidad) { ok ->
                if (ok) {
                    Toast.makeText(this, "Unidad creada con orden $nuevoOrden", Toast.LENGTH_SHORT).show()
                    limpiarCampos()
                } else {
                    Toast.makeText(this, "Ya existe una unidad con ese título", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // --- EDITAR UNIDAD ---
        btnEditar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()

            if (titulo.isEmpty()) {
                Toast.makeText(this, "Debes escribir el título de la unidad a editar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idUnidad = generarIdDesdeTitulo(titulo)
            val existente = AdministracionMemoria.obtenerUnidadPorId(idUnidad)

            if (existente == null) {
                Toast.makeText(this, "La unidad no existe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val (_, videoUrlLimpio) = procesarVideo(etVideo.text.toString())

            val actualizada = UnidadCompleta(
                id = existente.id,
                titulo = titulo,
                conceptos = etConceptos.text.toString().split(",").map { it.trim() },
                videoUrl = videoUrlLimpio,
                evaluacion = preguntasAdapter.obtenerPreguntas(),
                order = existente.order
            )

            AdministracionMemoria.editarUnidad(idUnidad, actualizada) { ok ->
                if (ok) {
                    Toast.makeText(this, "Unidad actualizada correctamente", Toast.LENGTH_SHORT).show()
                    limpiarCampos()
                } else {
                    Toast.makeText(this, "Error al editar unidad", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // --- ELIMINAR UNIDAD ---
        btnEliminar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()

            if (titulo.isEmpty()) {
                Toast.makeText(this, "Debes escribir el título de la unidad a eliminar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idUnidad = generarIdDesdeTitulo(titulo)

            AdministracionMemoria.eliminarUnidad(idUnidad) { ok ->
                if (ok) {
                    AdministracionMemoria.reordenarUnidades()
                    Toast.makeText(this, "Unidad eliminada correctamente", Toast.LENGTH_SHORT).show()
                    limpiarCampos()
                } else {
                    Toast.makeText(this, "La unidad no existe", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun limpiarCampos() {
        etTitulo.text.clear()
        etConceptos.text.clear()
        etVideo.text.clear()
        preguntasAdapter.limpiarPreguntas()
    }

    private fun procesarVideo(url: String): Pair<String, String> {
        val id = when {
            url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
            url.contains("watch?v=") -> url.substringAfter("watch?v=").substringBefore("&")
            url.contains("shorts/") -> url.substringAfter("shorts/").substringBefore("?")
            else -> url
        }
        return Pair(id, "https://www.youtube.com/watch?v=$id")
    }

    private fun generarIdDesdeTitulo(titulo: String): String {
        return titulo.lowercase()
            .replace(" ", "_")
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ñ", "n")
            .replace(Regex("[^a-z0-9_]"), "")
    }
}