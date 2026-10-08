package com.example.curso.usuarios

data class Usuario(
    val email: String,
    var password: String,
    val nombre: String,
    val curso: String,
    val esAdmin: Boolean = false,
    var codigoCertificado: String? = null,
    var ultimaUnidadCompletada: Int = 0,
    val unidadesDesbloqueadas: MutableSet<Int> = mutableSetOf(0)
)