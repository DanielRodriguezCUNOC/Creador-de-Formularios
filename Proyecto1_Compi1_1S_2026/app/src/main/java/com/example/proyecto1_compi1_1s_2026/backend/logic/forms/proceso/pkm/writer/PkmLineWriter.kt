package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

// Maneja líneas e indentación del archivo .pkm.
class PkmLineWriter {

    private val lineas = mutableListOf<String>()
    private var nivelIndentacion = 0

    fun limpiar() {
        lineas.clear()
        nivelIndentacion = 0
    }

    fun agregarLinea(texto: String) {
        val prefijo = "    ".repeat(nivelIndentacion)
        lineas.add(prefijo + texto)
    }

    fun aumentarIndentacion() {
        nivelIndentacion++
    }

    fun disminuirIndentacion() {
        if (nivelIndentacion > 0) {
            nivelIndentacion--
        }
    }

    fun obtenerTexto(): String {
        return lineas.joinToString("\n")
    }
}
