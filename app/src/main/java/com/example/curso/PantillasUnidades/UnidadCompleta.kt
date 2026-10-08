package com.example.curso.PantillasUnidades

data class UnidadCompleta(
    var id: String = "",
    val titulo: String = "",
    val conceptos: List<String> = emptyList(),
    val videoUrl: String = "",
    val evaluacion: List<Pregunta> = emptyList(),
    var order: Int = 0
)


