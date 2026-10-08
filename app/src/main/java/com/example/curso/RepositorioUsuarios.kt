package com.example.curso

import com.example.curso.usuarios.Usuario

object RepositorioUsuarios {

    private val usuarios = mutableMapOf<String, Usuario>()

    var usuarioActual: Usuario? = null
        private set

    init {
        val iniciales = listOf(
            // Administrador
            Usuario(
                email = "admin@correo.com",
                password = "admin123",
                nombre = "Administrador",
                curso = "-",
                esAdmin = true
            ),
            // Segundo administrador
            Usuario(
                email = "admin2@correo.com",
                password = "admin456",
                nombre = "Administrador 2",
                curso = "-",
                esAdmin = true
            ),
            // Usuario nuevo: solo tiene la unidad 0 desbloqueada
            Usuario(
                email = "maria@correo.com",
                password = "maria123",
                nombre = "María Gómez",
                curso = "Grado 5"
            ),
            // Usuario con la unidad 0 aprobada: tiene 0 y 1 desbloqueadas
            Usuario(
                email = "carlos@correo.com",
                password = "carlos123",
                nombre = "Carlos Pérez",
                curso = "Grado 8",
                ultimaUnidadCompletada = 0,
                unidadesDesbloqueadas = mutableSetOf(0, 1)
            ),
            // Usuario casi terminando: solo falta aprobar la última unidad
            Usuario(
                email = "laura@correo.com",
                password = "laura123",
                nombre = "Laura Rodríguez",
                curso = "Grado 11",
                ultimaUnidadCompletada = 1,
                unidadesDesbloqueadas = mutableSetOf(0, 1, 2)
            )
        )

        iniciales.forEach { usuarios[it.email.lowercase()] = it }
    }

    fun registrar(usuario: Usuario): Boolean {
        val clave = usuario.email.lowercase()
        if (usuarios.containsKey(clave)) return false
        usuarios[clave] = usuario
        return true
    }

    fun iniciarSesion(email: String, password: String): Usuario? {
        val usuario = usuarios[email.lowercase()]
        return if (usuario?.password == password) {
            usuarioActual = usuario
            usuario
        } else null
    }

    fun existeCorreo(email: String): Boolean = usuarios.containsKey(email.lowercase())

    fun cambiarPassword(email: String, nueva: String): Boolean {
        val usuario = usuarios[email.lowercase()] ?: return false
        usuario.password = nueva
        return true
    }

    fun cerrarSesion() {
        usuarioActual = null
    }
}