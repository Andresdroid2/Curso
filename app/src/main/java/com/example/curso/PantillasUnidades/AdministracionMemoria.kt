package com.example.curso.PantillasUnidades

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

object AdministracionMemoria {

    private val _unidades = MutableStateFlow(unidadesIniciales())
    val unidades: StateFlow<List<UnidadCompleta>> = _unidades.asStateFlow()

    private val listeners = CopyOnWriteArrayList<(List<UnidadCompleta>) -> Unit>()

    private fun unidadesIniciales(): List<UnidadCompleta> = listOf(
        UnidadCompleta(
            id = "bienvenida_e_introduccion",
            titulo = "Bienvenida e introducción",
            conceptos = listOf("Objetivos del curso", "Cómo usar la plataforma", "Sistema de evaluación"),
            videoUrl = "https://www.youtube.com/watch?v=REEMPLAZA_ID_1",
            evaluacion = listOf(
                Pregunta(
                    "¿Qué porcentaje mínimo necesitas para aprobar un parcial?",
                    listOf("50%", "60%", "70%", "90%"),
                    2
                ),
                Pregunta(
                    "¿Qué se desbloquea al aprobar una unidad?",
                    listOf("Un descuento", "La siguiente unidad", "El certificado final", "Nada"),
                    1
                ),
                Pregunta(
                    "¿Cuántas veces puedes reintentar un parcial?",
                    listOf("Solo una", "Dos", "Las que necesites", "Ninguna"),
                    2
                )
            ),
            order = 0
        ),
        UnidadCompleta(
            id = "fundamentos_del_curso",
            titulo = "Fundamentos del curso",
            conceptos = listOf("Conceptos básicos", "Vocabulario clave", "Ejemplos prácticos"),
            videoUrl = "https://www.youtube.com/watch?v=REEMPLAZA_ID_2",
            evaluacion = listOf(
                Pregunta(
                    "¿Para qué sirve el vocabulario clave de la unidad?",
                    listOf("Para decorar", "Para entender el resto del curso", "Para nada", "Para el certificado"),
                    1
                ),
                Pregunta(
                    "¿Qué tipo de ejemplos se usan en esta unidad?",
                    listOf("Prácticos", "Históricos", "Musicales", "Ninguno"),
                    0
                ),
                Pregunta(
                    "¿Qué debes hacer antes de enviar el parcial?",
                    listOf("Cerrar la app", "Responder todas las preguntas", "Borrar el video", "Reiniciar el celular"),
                    1
                )
            ),
            order = 1
        ),
        UnidadCompleta(
            id = "cierre_y_certificacion",
            titulo = "Cierre y certificación",
            conceptos = listOf("Repaso general", "Cómo obtener el certificado", "Próximos pasos"),
            videoUrl = "https://www.youtube.com/watch?v=REEMPLAZA_ID_3",
            evaluacion = listOf(
                Pregunta(
                    "¿Qué incluye el certificado?",
                    listOf("Tu nombre y un código de seguridad", "Solo la fecha", "Una foto", "Tu contraseña"),
                    0
                ),
                Pregunta(
                    "¿Qué pasa al aprobar la última unidad?",
                    listOf("Se reinicia el curso", "Ves la pantalla de agradecimientos", "Se borra tu cuenta", "Nada"),
                    1
                ),
                Pregunta(
                    "¿Cuántas horas de carga tiene el curso?",
                    listOf("2", "5", "10", "40"),
                    2
                )
            ),
            order = 2
        )
    )

    private fun notificar() {
        val actual = _unidades.value
        listeners.forEach { it(actual) }
    }

    fun crearUnidad(unidad: UnidadCompleta, onComplete: (Boolean) -> Unit) {
        try {
            unidad.id = UUID.randomUUID().toString()
            _unidades.update { it + unidad }
            notificar()
            onComplete(true)
        } catch (e: Exception) {
            onComplete(false)
        }
    }

    fun existeUnidad(id: String): Boolean = _unidades.value.any { it.id == id }

    fun siguienteOrden(): Int = (_unidades.value.maxOfOrNull { it.order } ?: -1) + 1

    fun crearUnidadConId(unidad: UnidadCompleta, onComplete: (Boolean) -> Unit) {
        if (existeUnidad(unidad.id)) {
            onComplete(false)
            return
        }
        _unidades.update { it + unidad }
        notificar()
        onComplete(true)
    }

    fun obtenerUnidadPorId(unidadId: String): UnidadCompleta? =
        _unidades.value.firstOrNull { it.id == unidadId }

    fun editarUnidad(unidadId: String, nuevosDatos: UnidadCompleta, onComplete: (Boolean) -> Unit) {
        var encontrada = false
        _unidades.update { lista ->
            lista.map { u ->
                if (u.id == unidadId) {
                    encontrada = true
                    nuevosDatos.id = unidadId
                    nuevosDatos
                } else u
            }
        }
        if (encontrada) notificar()
        onComplete(encontrada)
    }

    fun eliminarUnidad(unidadId: String, onComplete: (Boolean) -> Unit) {
        val tamanoAntes = _unidades.value.size
        _unidades.update { lista -> lista.filter { it.id != unidadId } }
        val eliminada = _unidades.value.size < tamanoAntes
        if (eliminada) notificar()
        onComplete(eliminada)
    }

    fun reordenarUnidades() {
        val ordenadas = _unidades.value.sortedBy { it.order }
        ordenadas.forEachIndexed { index, unidad -> unidad.order = index }
        _unidades.value = ordenadas
        notificar()
    }

    fun obtenerUnidades(onResult: (List<UnidadCompleta>) -> Unit) {
        listeners.add(onResult)
        onResult(_unidades.value)
    }

    fun dejarDeObservar(onResult: (List<UnidadCompleta>) -> Unit) {
        listeners.remove(onResult)
    }
}