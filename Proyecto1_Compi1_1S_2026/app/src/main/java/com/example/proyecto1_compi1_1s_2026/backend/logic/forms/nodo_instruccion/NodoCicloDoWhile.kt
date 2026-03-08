package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

class NodoCicloDoWhile(
    val instrucciones: List<NodoInstruccion>,
    val condicion: NodoExpresion
): NodoInstruccion {
    override fun <T> accept(visitor: Visitor<T>):T{
        return visitor.visit(this)
    }
}