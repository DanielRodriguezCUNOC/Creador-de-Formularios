package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

// Acumula estadísticas para el bloque de metadatos del .pkm.

class PkmStatsCollector {

    private var totalSecciones = 0
    private var totalAbiertas = 0
    private var totalDesplegables = 0
    private var totalSeleccion = 0
    private var totalMultiples = 0

    fun limpiar() {
        totalSecciones = 0
        totalAbiertas = 0
        totalDesplegables = 0
        totalSeleccion = 0
        totalMultiples = 0
    }

    fun registrarSeccion() {
        totalSecciones++
    }

    fun registrarPreguntaAbierta() {
        totalAbiertas++
    }

    fun registrarPreguntaDesplegable() {
        totalDesplegables++
    }

    fun registrarPreguntaSeleccion() {
        totalSeleccion++
    }

    fun registrarPreguntaMultiple() {
        totalMultiples++
    }

    fun snapshot(): PkmStatsSnapshot {
        val totalPreguntas = totalAbiertas + totalDesplegables + totalSeleccion + totalMultiples
        return PkmStatsSnapshot(
            totalSecciones = totalSecciones,
            totalPreguntas = totalPreguntas,
            abiertas = totalAbiertas,
            desplegables = totalDesplegables,
            seleccion = totalSeleccion,
            multiples = totalMultiples
        )
    }
}

data class PkmStatsSnapshot(
    val totalSecciones: Int,
    val totalPreguntas: Int,
    val abiertas: Int,
    val desplegables: Int,
    val seleccion: Int,
    val multiples: Int
)
