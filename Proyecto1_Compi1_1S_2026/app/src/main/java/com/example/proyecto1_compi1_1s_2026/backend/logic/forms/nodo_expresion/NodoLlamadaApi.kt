package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

class NodoLlamadaApi(
    val tipo: String,
    val rangoInicio: NodoExpresion,
    val rangoFin: NodoExpresion,
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoExpresion {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)

    override fun validarSemantica(contexto: ContextoSemantico) {
        rangoInicio.validarSemantica(contexto)
        rangoFin.validarSemantica(contexto)

        if (tipo == "pokemon") {
            validarRangoPokemon(contexto)
        }
    }

    private fun validarRangoPokemon(contexto: ContextoSemantico) {
        val inicio = extraerValorConstante(rangoInicio, contexto)
        val fin = extraerValorConstante(rangoFin, contexto)

        if (inicio != null && fin != null) {
            val inicioInt = inicio.toInt()
            val finInt = fin.toInt()

            if (inicioInt < 1 || inicioInt > 1025) {
                contexto.reportarError(
                    "who_is_that_pokemon: El rango de inicio ($inicioInt) debe estar entre 1 y 1025",
                    linea,
                    columna
                )
            }

            if (finInt < 1 || finInt > 1025) {
                contexto.reportarError(
                    "who_is_that_pokemon: El rango final ($finInt) debe estar entre 1 y 1025",
                    linea,
                    columna
                )
            }

            if (inicioInt > finInt) {
                contexto.reportarError(
                    "who_is_that_pokemon: El rango de inicio ($inicioInt) no puede ser mayor que el final ($finInt)",
                    linea,
                    columna
                )
            }
        }
    }

    private fun extraerValorConstante(expr: NodoExpresion, contexto: ContextoSemantico): Double? {
        return when (expr) {
            is NodoLiteral -> expr.valor.toString().toDoubleOrNull()
            is NodoAccesoVariable -> {
                val valor = try {
                    contexto.entornoActual.obtenerVariable(expr.id)
                } catch (_: Exception) {
                    null
                }
                valor?.toString()?.toDoubleOrNull()
            }
            else -> null
        }
    }
}