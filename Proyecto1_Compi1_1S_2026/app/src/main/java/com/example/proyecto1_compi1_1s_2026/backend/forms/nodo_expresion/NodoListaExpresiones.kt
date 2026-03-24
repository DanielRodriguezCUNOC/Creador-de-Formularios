package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

/**
 * Representa una lista de expresiones, típicamente para colores con componentes expresivos.
 * Ejemplos: (43+?, 255, 100) para RGB, <120, ?, 50> para HSL
 */
class NodoListaExpresiones(
    val elementos: List<Any>,  // Puede contener NodoExpresion u otros tipos
    val tipo: String = "color",
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoExpresion {
    
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)

    override fun validarSemantica(contexto: ContextoSemantico) {
        elementos.forEach { elem ->
            if (elem is NodoExpresion) {
                elem.validarSemantica(contexto)
            }
        }
    }

    override fun inferirTipo(contexto: ContextoSemantico): String = tipo
}
