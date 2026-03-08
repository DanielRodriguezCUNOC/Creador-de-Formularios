package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

class NodoDraw(
    val idVariableEspecial: String,
    //* Permite que recibamos los comodines
    val parametros: List<NodoExpresion>
): NodoInstruccion {
    override fun <T> accept(visitor: Visitor <T>): T {
        return visitor.visit(this)
    }
}