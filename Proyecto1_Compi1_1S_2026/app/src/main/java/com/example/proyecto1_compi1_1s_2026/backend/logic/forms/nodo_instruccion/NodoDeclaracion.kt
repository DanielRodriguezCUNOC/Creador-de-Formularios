package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

class NodoDeclaracion(
    val id: String,
    val tipo: String,
    val valorInicio: NodoExpresion?, 
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoInstruccion {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)

    override fun validarSemantica(contexto: ContextoSemantico) {
        if (contexto.entornoActual.obtenerTipo(id) != null) {
            contexto.reportarError(
                "La variable '$id' ya fue declarada y no puede redefinirse",
                linea,
                columna
            )
            return
        }

        valorInicio?.validarSemantica(contexto)

        if (valorInicio != null) {
            val tipoValor = valorInicio.inferirTipo(contexto)
            if (tipoValor != tipo && !(tipo == "number" && tipoValor == "double")) {
                contexto.reportarError(
                    "Tipo incompatible en declaración '$id': se esperaba $tipo, pero obtuvo $tipoValor",
                    linea,
                    columna
                )
            }
        }

        contexto.entornoActual.almacenarVariable(
            id,
            when (tipo) {
                "number" -> 0.0
                "string" -> ""
                else -> ""
            },
            tipo
        )
    }
}