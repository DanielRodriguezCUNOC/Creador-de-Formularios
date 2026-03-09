package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

class NodoLlamadaApi(
    val tipo: String,
    val rangoInicio: NodoExpresion,
    val rangoFin: NodoExpresion,
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoExpresion {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)
}