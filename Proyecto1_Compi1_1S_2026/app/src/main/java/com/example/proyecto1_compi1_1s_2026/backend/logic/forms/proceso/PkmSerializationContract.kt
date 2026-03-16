package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

// Contrato central de serialización para salida .pkm.
object PkmSerializationContract {

    // Metadatos
    const val BLOQUE_DELIMITADOR = "###"
    const val DESCRIPCION_POR_DEFECTO = "Generado automáticamente"

    // Valores por defecto de componentes
    const val DEFAULT_WIDTH = "50"
    const val DEFAULT_HEIGHT = "10"
    const val DEFAULT_SECTION_WIDTH = "100"
    const val DEFAULT_SECTION_HEIGHT = "100"
    const val DEFAULT_POINT_X = "0"
    const val DEFAULT_POINT_Y = "0"
    const val DEFAULT_ORIENTATION = "VERTICAL"

    // Reglas para respuestas correctas
    const val DEFAULT_CORRECT_SINGLE = "-1"
    const val DEFAULT_CORRECT_MULTIPLE = "{}"

    // Reglas de API en texto
    const val API_POKEMON_PREFIX = "who_is_that_pokemon(NUMBER"

    fun construirMetadatos(autor: String, fecha: String, hora: String, stats: PkmStatsSnapshot): List<String> {
        val lineas = mutableListOf<String>()
        lineas.add(BLOQUE_DELIMITADOR)
        lineas.add("Author: $autor")
        lineas.add("Fecha: $fecha")
        lineas.add("Hora: $hora")
        lineas.add("Description: $DESCRIPCION_POR_DEFECTO")
        lineas.add("Total de Secciones: ${stats.totalSecciones}")
        lineas.add("Total de Preguntas: ${stats.totalPreguntas}")
        lineas.add("Abiertas: ${stats.abiertas}")
        lineas.add("Desplegables: ${stats.desplegables}")
        lineas.add("Selección: ${stats.seleccion}")
        lineas.add("Múltiples: ${stats.multiples}")
        lineas.add(BLOQUE_DELIMITADOR)
        return lineas
    }

    fun tagSectionOpen(width: String, height: String, pointX: String, pointY: String, orientation: String): String {
        return "<section=$width,$height,$pointX,$pointY,$orientation>"
    }

    fun tagSectionClose(): String = "</section>"

    fun tagTableOpen(): String = "<table>"

    fun tagTableClose(): String = "</table>"

    fun tagContentOpen(): String = "<content>"

    fun tagContentClose(): String = "</content>"

    fun tagLineOpen(): String = "<line>"

    fun tagLineClose(): String = "</line>"

    fun tagElementOpen(): String = "<element>"

    fun tagElementClose(): String = "</element>"

    fun tagStylePlaceholder(): String = "<style> ... </style>"

    fun tagOpenTextSelf(width: String, height: String, content: String): String {
        return "<open=$width,$height,$content/>"
    }

    fun tagOpenTextOpen(width: String, height: String, content: String): String {
        return "<open=$width,$height,$content>"
    }

    fun tagOpenTextClose(): String = "</open>"

    fun tagDropSelf(width: String, height: String, label: String, options: String, correct: String): String {
        return "<drop=$width,$height,$label,$options,$correct/>"
    }

    fun tagDropOpen(width: String, height: String, label: String, options: String, correct: String): String {
        return "<drop=$width,$height,$label,$options,$correct>"
    }

    fun tagDropClose(): String = "</drop>"

    fun tagSelectSelf(width: String, height: String, label: String, options: String, correct: String): String {
        return "<select=$width,$height,$label,$options,$correct/>"
    }

    fun tagSelectOpen(width: String, height: String, label: String, options: String, correct: String): String {
        return "<select=$width,$height,$label,$options,$correct>"
    }

    fun tagSelectClose(): String = "</select>"

    fun tagMultipleSelf(width: String, height: String, label: String, options: String, correct: String): String {
        return "<multiple=$width,$height,$label,$options,$correct/>"
    }

    fun tagMultipleOpen(width: String, height: String, label: String, options: String, correct: String): String {
        return "<multiple=$width,$height,$label,$options,$correct>"
    }

    fun tagMultipleClose(): String = "</multiple>"
}
