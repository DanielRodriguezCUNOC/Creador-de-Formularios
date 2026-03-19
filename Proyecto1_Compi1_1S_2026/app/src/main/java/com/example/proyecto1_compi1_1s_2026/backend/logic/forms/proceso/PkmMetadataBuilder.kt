package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Construye el bloque inicial de metadatos del .pkm
class PkmMetadataBuilder {

    fun construir(autor: String, stats: PkmStatsSnapshot): String {
        val fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yy"))
        val hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

        val lineas = PkmSerializationContract.construirMetadatos(autor, fecha, hora, stats)
        return lineas.joinToString("\n")
    }
}
