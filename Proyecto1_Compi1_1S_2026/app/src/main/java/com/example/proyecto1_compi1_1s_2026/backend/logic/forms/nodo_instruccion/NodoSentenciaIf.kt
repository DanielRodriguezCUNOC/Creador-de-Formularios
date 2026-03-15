package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

class NodoSentenciaIf(
    val condicion: NodoExpresion,
    val instruccionesIf: List<NodoInstruccion>,
    val instruccionesElse: List<NodoInstruccion>?,
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoInstruccion {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)

    override fun validarSemantica(contexto: ContextoSemantico) {
        condicion.validarSemantica(contexto)
        val tipoCondicion = condicion.inferirTipo(contexto)

        if (tipoCondicion != "boolean") {
            contexto.reportarError(
                "La condición del IF debe ser booleana, pero obtuvo $tipoCondicion",
                linea,
                columna
            )
        }

        instruccionesIf.forEach { it.validarSemantica(contexto) }
        instruccionesElse?.forEach { it.validarSemantica(contexto) }
    }
}