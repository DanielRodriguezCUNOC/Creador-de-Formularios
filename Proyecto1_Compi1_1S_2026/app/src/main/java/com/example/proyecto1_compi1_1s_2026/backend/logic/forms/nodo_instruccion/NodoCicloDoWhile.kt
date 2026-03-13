package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

class NodoCicloDoWhile(
    val instrucciones: List<NodoInstruccion>,
    val condicion: NodoExpresion,
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoInstruccion {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)

    override fun validarSemantica(contexto: ContextoSemantico) {
        val tipoCondicion = condicion.inferirTipo(contexto)
        if (tipoCondicion != "boolean") {
            contexto.reportarError(
                "La condición del DO-WHILE debe ser booleana",
                linea,
                columna
            )
        }

        instrucciones.forEach { it.validarSemantica(contexto) }
        condicion.validarSemantica(contexto)
    }
}