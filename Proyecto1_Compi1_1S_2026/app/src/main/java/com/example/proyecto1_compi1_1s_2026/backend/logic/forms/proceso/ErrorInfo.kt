package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

data class ErrorInfo(
    val tipo: TipoError,
    val mensaje: String,
    val linea: Int,
    val columna: Int = 0
) {
    override fun toString(): String {
        return if (linea > 0) "Línea $linea, Col $columna: $mensaje"
        else mensaje
    }

    fun toDetailedString(): String {
        return if (linea > 0)
            "Error ${tipo.nombre} en línea $linea, columna $columna: $mensaje"
        else
            "${tipo.nombre}: $mensaje"
    }
}


