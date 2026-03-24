package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

// Convierte el árbol de etiquetas .pkm a texto final.
class PkmTagTreeWriter {

    fun escribir(nodos: List<PkmTagNode>): String {
        val lineas = mutableListOf<String>()
        for (nodo in nodos) {
            escribirNodo(nodo, 0, lineas)
        }
        return lineas.joinToString("\n")
    }

    private fun escribirNodo(nodo: PkmTagNode, nivel: Int, lineas: MutableList<String>) {
        when (nodo) {
            is PkmTextNode -> {
                val prefijo = "    ".repeat(nivel)
                lineas.add(prefijo + nodo.texto)
            }
            is PkmGroupNode -> {
                for (hijo in nodo.hijos) {
                    escribirNodo(hijo, nivel, lineas)
                }
            }
            is PkmElementNode -> {
                val prefijo = "    ".repeat(nivel)
                lineas.add(prefijo + nodo.apertura)
                if (nodo.cierre != null) {
                    for (hijo in nodo.hijos) {
                        escribirNodo(hijo, nivel + 1, lineas)
                    }
                    lineas.add(prefijo + nodo.cierre)
                }
            }
        }
    }
}
