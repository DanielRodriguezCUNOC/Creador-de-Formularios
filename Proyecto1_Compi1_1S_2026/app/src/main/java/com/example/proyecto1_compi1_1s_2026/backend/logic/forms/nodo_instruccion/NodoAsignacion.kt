package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.Expresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

class NodoAsignacion(
    val id: String,
    val nuevoValor: Expresion
): NodoInstruccion {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}
