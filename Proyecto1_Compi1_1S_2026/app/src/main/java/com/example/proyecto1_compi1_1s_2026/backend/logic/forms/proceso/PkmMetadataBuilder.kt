package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Construye el bloque inicial de metadatos del .pkm.
class PkmMetadataBuilder {

    fun construir(autor: String, stats: PkmStatsSnapshot): String {
        val fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yy"))
        val hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

        val bloque = mutableListOf<String>()
        bloque.add("###")
        bloque.add("Author: $autor")
        bloque.add("Fecha: $fecha")
        bloque.add("Hora: $hora")
        bloque.add("Description: Generado automáticamente")
        bloque.add("Total de Secciones: ${stats.totalSecciones}")
        bloque.add("Total de Preguntas: ${stats.totalPreguntas}")
        bloque.add("Abiertas: ${stats.abiertas}")
        bloque.add("Desplegables: ${stats.desplegables}")
        bloque.add("Selección: ${stats.seleccion}")
        bloque.add("Múltiples: ${stats.multiples}")
        bloque.add("###")
        return bloque.joinToString("\n")
    }
}
