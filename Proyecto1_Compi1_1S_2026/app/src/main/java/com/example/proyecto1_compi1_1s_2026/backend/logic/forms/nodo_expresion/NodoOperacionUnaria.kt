package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

class NodoOperacionUnaria(
    val operador: String,
    val expresion: NodoExpresion
): NodoExpresion{
    override fun <T> accept(visitor: Visitor<T>):T{
        return visitor.visit(this)
    }
}