package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteUI
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.NodoAtributo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoAccesoVariable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoLlamadaApi
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoLiteral
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoListaExpresiones
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoOperacionBinaria
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoOperacionUnaria
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
            is NodoLiteral -> contarComodinesEnLiteral(valor)
            is NodoOperacionBinaria ->
                contarComodinesEnExpresion(valor.izq) + contarComodinesEnExpresion(valor.der)
            is NodoOperacionUnaria -> contarComodinesEnExpresion(valor.expresion)
            is NodoLlamadaApi ->
                contarComodinesEnExpresion(valor.rangoInicio) + contarComodinesEnExpresion(valor.rangoFin)
            is NodoAccesoVariable -> 0
            is NodoAtributo -> contarComodinesEnValor(valor.valor)
            is ComponenteUI -> valor.atributos.sumOf { attr -> contarComodinesEnValor(attr.valor) }
            is List<*> -> valor.sumOf { item -> contarComodinesEnValor(item) }
            is NodoExpresion -> contarComodinesEnExpresion(valor)
            else -> 0
        }
    }

    private fun contarComodinesEnExpresion(expresion: NodoExpresion): Int {
        return when (expresion) {
            is NodoLiteral -> contarComodinesEnLiteral(expresion)
            is NodoOperacionBinaria ->
                contarComodinesEnExpresion(expresion.izq) + contarComodinesEnExpresion(expresion.der)
            is NodoOperacionUnaria -> contarComodinesEnExpresion(expresion.expresion)
            is NodoLlamadaApi ->
                contarComodinesEnExpresion(expresion.rangoInicio) + contarComodinesEnExpresion(expresion.rangoFin)
            is NodoListaExpresiones ->
                expresion.elementos.sumOf { elem -> 
                    if (elem is NodoExpresion) contarComodinesEnExpresion(elem) 
                    else contarComodinesEnValor(elem)
                }
            is NodoAccesoVariable -> 0
            else -> 0
        }
    }

    private fun contarComodinesEnLiteral(literal: NodoLiteral): Int {
        return when (literal.tipo) {
            "comodin" -> 1
            "color" -> literal.valor.toString().count { it == '?' }
            else -> 0
        }
    }
}