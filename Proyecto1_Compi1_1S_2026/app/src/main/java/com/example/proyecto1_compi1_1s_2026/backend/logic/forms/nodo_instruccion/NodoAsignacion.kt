package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

class NodoAsignacion(
    val id: String,
    val nuevoValor: NodoExpresion,
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoInstruccion {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)

    override fun validarSemantica(contexto: ContextoSemantico) {
        nuevoValor.validarSemantica(contexto)

        val tipoActual = contexto.entornoActual.obtenerTipo(id)
        val tipoNuevo = nuevoValor.inferirTipo(contexto)

        if (tipoActual != null && tipoNuevo != tipoActual) {
            contexto.reportarError(
                "No puedes asignar $tipoNuevo a variable de tipo $tipoActual en '$id'",
                linea,
                columna
            )
        }
    }
}
