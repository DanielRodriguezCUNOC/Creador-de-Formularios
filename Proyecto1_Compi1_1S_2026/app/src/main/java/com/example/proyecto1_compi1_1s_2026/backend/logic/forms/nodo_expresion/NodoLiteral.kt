package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

class NodoLiteral(
    val valor: Any,
    val tipo: String,
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoExpresion {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)

    override fun validarSemantica(contexto: ContextoSemantico) = Unit

    override fun inferirTipo(contexto: ContextoSemantico): String {
        return when (tipo) {
            "number" -> "number"
            "string" -> "string"
            "boolean" -> "boolean"
            "comodin" -> "comodin"
            else -> "unknown"
        }
    }
}