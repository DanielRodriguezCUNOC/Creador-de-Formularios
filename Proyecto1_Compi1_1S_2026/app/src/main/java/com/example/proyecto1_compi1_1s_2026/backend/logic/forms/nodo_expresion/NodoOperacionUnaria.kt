package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

class NodoOperacionUnaria(
    val operador: String,
    val expresion: NodoExpresion,
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoExpresion {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)

    override fun validarSemantica(contexto: ContextoSemantico) {
        expresion.validarSemantica(contexto)
    }

    override fun inferirTipo(contexto: ContextoSemantico): String {
        return if (operador == "!") "boolean" else "number"
    }
}