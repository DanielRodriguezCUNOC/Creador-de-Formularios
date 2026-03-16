package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

// Acumula estadísticas para el bloque de metadatos del .pkm

class PkmStatsCollector {

    private var totalSecciones = 0
    private var totalAbiertas = 0
    private var totalDesplegables = 0
    private var totalSeleccion = 0
    private var totalMultiples = 0
    private var totalTextos = 0
    private var totalTablas = 0
    private var totalEstilos = 0
    private var totalDraws = 0

    fun limpiar() {
        totalSecciones = 0
        totalAbiertas = 0
        totalDesplegables = 0
        totalSeleccion = 0
        totalMultiples = 0
        totalTextos = 0
        totalTablas = 0
        totalEstilos = 0
        totalDraws = 0
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

    fun registrarTexto() {
        totalTextos++
    }

    fun registrarTabla() {
        totalTablas++
    }

    fun registrarEstilo() {
        totalEstilos++
    }

    fun registrarDraw() {
        totalDraws++
    }

    fun snapshot(): PkmStatsSnapshot {
        val totalPreguntas = totalAbiertas + totalDesplegables + totalSeleccion + totalMultiples
        val totalComponentes = totalSecciones + totalPreguntas + totalTextos + totalTablas
        return PkmStatsSnapshot(
            totalSecciones = totalSecciones,
            totalPreguntas = totalPreguntas,
            abiertas = totalAbiertas,
            desplegables = totalDesplegables,
            seleccion = totalSeleccion,
            multiples = totalMultiples,
            textos = totalTextos,
            tablas = totalTablas,
            estilos = totalEstilos,
            draws = totalDraws,
            totalComponentes = totalComponentes
        )
    }
}

data class PkmStatsSnapshot(
    val totalSecciones: Int,
    val totalPreguntas: Int,
    val abiertas: Int,
    val desplegables: Int,
    val seleccion: Int,
    val multiples: Int,
    val textos: Int,
    val tablas: Int,
    val estilos: Int,
    val draws: Int,
    val totalComponentes: Int
)
