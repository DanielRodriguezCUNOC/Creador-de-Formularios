package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

class NodoCicloWhile(
    val condicion: NodoExpresion,
    val instruccionesWhile: List<NodoInstruccion>,
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoInstruccion {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)
}