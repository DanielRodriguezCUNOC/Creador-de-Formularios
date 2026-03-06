package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.Expresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

class NodoDeclaracion(
    val id: String,
    val tipo: String, // puede ser number, string o especial
    val valorInicio: Expresion? // puede ser null si no se inicializa
): Instruccion{
    override fun <T> accept(visitor: Visitor<T>): T{
        return visitor.visit(this)
    }
}