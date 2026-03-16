package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteUI
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoLiteral
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

class NodoDraw(
    val idVariableEspecial: String,
    /** Permite que recibamos los comodines */
    val parametros: List<NodoExpresion>,
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoInstruccion {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)

    override fun validarSemantica(contexto: ContextoSemantico) {
        for (parametro in parametros) {
            parametro.validarSemantica(contexto)
        }

        val valorEspecial = try {
            contexto.entornoActual.obtenerVariable(idVariableEspecial)
        } catch (_: Exception) {
            contexto.reportarError(
                "Variable especial '$idVariableEspecial' no fue declarada",
                linea,
                columna
            )
            return
        }

        // En draw(...) los argumentos reales son los parámetros provistos por el usuario.
        val parametrosEnDraw = parametros.size

        val comodinesEnVariable = contarComodinesEnComponente(valorEspecial)

        if (parametrosEnDraw != comodinesEnVariable) {
            contexto.reportarError(
                "Variable especial '$idVariableEspecial': Esperaba $comodinesEnVariable parámetro(s) en .draw(), pero se proporcionaron $parametrosEnDraw",
                linea,
                columna
            )
        }
    }

    private fun contarComodinesEnComponente(valor: Any?): Int {
        return when (valor) {
            is ComponenteUI -> valor.atributos.sumOf { attr -> contarComodinesEnValor(attr.valor) }
            else -> contarComodinesEnValor(valor)
        }
    }

    private fun contarComodinesEnValor(valor: Any?): Int {
        return when (valor) {
            null -> 0
            is String -> valor.count { it == '?' }
            is NodoLiteral -> when (valor.tipo) {
                "comodin" -> 1
                "string" -> valor.valor.toString().count { it == '?' }
                else -> 0
            }
            is ComponenteUI -> valor.atributos.sumOf { attr -> contarComodinesEnValor(attr.valor) }
            is List<*> -> valor.sumOf { item -> contarComodinesEnValor(item) }
            is NodoExpresion -> 0
            else -> 0
        }
    }
}