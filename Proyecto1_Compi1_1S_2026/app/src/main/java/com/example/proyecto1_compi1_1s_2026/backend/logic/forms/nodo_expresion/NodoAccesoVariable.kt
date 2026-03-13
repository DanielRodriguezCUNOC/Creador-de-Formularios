package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

class NodoAccesoVariable(
    /** Obtiene el nombre de la variable */
    val id: String,
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoExpresion {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)

    override fun validarSemantica(contexto: ContextoSemantico) {
        try {
            contexto.entornoActual.obtenerVariable(id)
        } catch (_: Exception) {
            contexto.reportarError("Variable '$id' no fue declarada", linea, columna)
        }
    }

    override fun inferirTipo(contexto: ContextoSemantico): String {
        return contexto.entornoActual.obtenerTipo(id) ?: "unknown"
    }
}