package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

class NodoLlamadaApi(
    val tipo: String,
    val rangoInicio: Expresion,
    val rangoFin: Expresion
): Expresion{
    override fun <T> accept(visitor: Visitor<T>): T{
        return visitor.visit(this)
    }
}