package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

/**
 * Representa ambas formas de ciclo FOR:
 * - FOR clásica: FOR ID IN inicio..fin { }
 * - FOR imperativa: FOR (ID = init; cond; ID = inc) { }
 *
 * @param idVariable     Nombre de la variable de control
 * @param rangoInicio    Valor inicial (en FOR clásica) o inicialización (en FOR imperativa)
 * @param rangoFin       Valor final (en FOR clásica) o condición (en FOR imperativa)
 * @param incremento     null en FOR clásica; en FOR imperativa es la expresión de incremento
 * @param instruccionesFor Cuerpo del ciclo
 */
class NodoCicloFor(
    val idVariable: String,
    val rangoInicio: NodoExpresion,
    val rangoFin: NodoExpresion,
    val instruccionesFor: List<NodoInstruccion>,
    val incremento: NodoExpresion? = null,  // null → FOR clásica; no null → FOR imperativa
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoInstruccion {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)

    override fun validarSemantica(contexto: ContextoSemantico) {
        rangoInicio.validarSemantica(contexto)
        rangoFin.validarSemantica(contexto)
        incremento?.validarSemantica(contexto)

        val tipoInicio = rangoInicio.inferirTipo(contexto)
        val tipoFin = rangoFin.inferirTipo(contexto)

        if (incremento == null) {
            // FOR clásica: inicio..fin debe ser number
            if (tipoInicio != "number" || tipoFin != "number") {
                contexto.reportarError(
                    "Los rangos del FOR deben ser números",
                    linea,
                    columna
                )
            }
        } else {
            // FOR imperativa: init e incremento numéricos, condición booleana
            val tipoInc = incremento.inferirTipo(contexto)
            if (tipoInicio != "number") {
                contexto.reportarError(
                    "La inicialización del FOR imperativo debe ser numérica",
                    linea,
                    columna
                )
            }
            if (tipoFin != "boolean") {
                contexto.reportarError(
                    "La condición del FOR imperativo debe ser booleana",
                    linea,
                    columna
                )
            }
            if (tipoInc != "number") {
                contexto.reportarError(
                    "El incremento del FOR imperativo debe ser numérico",
                    linea,
                    columna
                )
            }
        }

        instruccionesFor.forEach { it.validarSemantica(contexto) }
    }
}