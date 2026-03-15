package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TablaSimbolos

class NodoCicloFor(
    val idVariable: String?,
    val rangoInicio: NodoExpresion?,
    val rangoFin: NodoExpresion,
    val instruccionesFor: List<NodoInstruccion>,
    val incremento: NodoExpresion? = null,
    val inicializacionImperativa: NodoInstruccion? = null,
    val actualizacionImperativa: NodoInstruccion? = null,
    val esImperativo: Boolean = false,
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoInstruccion {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)

    override fun validarSemantica(contexto: ContextoSemantico) {
        rangoFin.validarSemantica(contexto)
        rangoInicio?.validarSemantica(contexto)
        incremento?.validarSemantica(contexto)

        val tipoInicio = rangoInicio?.inferirTipo(contexto)
        val tipoFin = rangoFin.inferirTipo(contexto)

        if (!esImperativo) {
            val nombreVariable = idVariable
            if (nombreVariable == null || rangoInicio == null) {
                contexto.reportarError(
                    "FOR clásico inválido: faltan variable o rango inicial",
                    linea,
                    columna
                )
                return
            }

            val tipoExistente = contexto.entornoActual.obtenerTipo(nombreVariable)
            if (tipoExistente != null && tipoExistente != "number") {
                contexto.reportarError(
                    "La variable '$nombreVariable' del FOR ya existe y no es de tipo number",
                    linea,
                    columna
                )
            }

            // FOR clásica: inicio..fin debe ser number
            if (tipoInicio != "number" || tipoFin != "number") {
                contexto.reportarError(
                    "Los rangos del FOR deben ser números",
                    linea,
                    columna
                )
            }
        } else {
            if (inicializacionImperativa == null || actualizacionImperativa == null) {
                contexto.reportarError(
                    "El FOR imperativo requiere instrucción de inicialización y actualización",
                    linea,
                    columna
                )
            }
        }

        val entornoPrevio = contexto.entornoActual
        try {
            val entornoFor = TablaSimbolos(entornoPrevio)
            contexto.entornoActual = entornoFor

            if (!esImperativo && idVariable != null) {
                entornoFor.almacenarVariable(idVariable, 0.0, "number")
            } else {
                inicializacionImperativa?.validarSemantica(contexto)

                val tipoCondicionImperativa = rangoFin.inferirTipo(contexto)
                if (tipoCondicionImperativa != "boolean") {
                    contexto.reportarError(
                        "La condición del FOR imperativo debe ser booleana",
                        linea,
                        columna
                    )
                }
            }

            for (instruccion in instruccionesFor) {
                instruccion.validarSemantica(contexto)
            }

            if (esImperativo) {
                actualizacionImperativa?.validarSemantica(contexto)
            }
        } finally {
            contexto.entornoActual = entornoPrevio
        }
    }
}