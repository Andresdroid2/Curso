package com.example.curso.PantillasUnidades

data class Pregunta(
    var texto: String = "",
    var opciones: List<String> = emptyList(),
    var respuestaCorrecta: Int = -1
)