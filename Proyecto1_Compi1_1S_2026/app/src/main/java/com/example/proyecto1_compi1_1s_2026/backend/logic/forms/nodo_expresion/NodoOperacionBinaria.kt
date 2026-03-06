package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

class NodoOperacionBinaria(
    val izq: Expresion,
    val operador: String,
    val der: Expresion
): Expresion{
    override fun <T> accept(visitor: Visitor<T>): T{
        return visitor.visit(this)
    }
}
