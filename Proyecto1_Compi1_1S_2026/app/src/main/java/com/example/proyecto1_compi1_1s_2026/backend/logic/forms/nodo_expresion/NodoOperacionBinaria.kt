package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

class NodoOperacionBinaria(
    val izq: NodoExpresion,
    val operador: String,
    val der: NodoExpresion,
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoExpresion {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)

    override fun validarSemantica(contexto: ContextoSemantico) {
        izq.validarSemantica(contexto)
        der.validarSemantica(contexto)

        val tipoIzq = izq.inferirTipo(contexto)
        val tipoDer = der.inferirTipo(contexto)

        when (operador) {
            "+", "-", "*", "/", "%", "^" -> {
                if (tipoIzq != "number" || tipoDer != "number") {
                    contexto.reportarError(
                        "Operación aritmética requiere números, pero obtuvo $tipoIzq y $tipoDer",
                        linea,
                        columna
                    )
                }
            }
            "&&", "||" -> {
                if (tipoIzq != "boolean" || tipoDer != "boolean") {
                    contexto.reportarError(
                        "Operación lógica requiere booleanos, pero obtuvo $tipoIzq y $tipoDer",
                        linea,
                        columna
                    )
                }
            }
            "==", "!!" -> {
                if (tipoIzq != tipoDer) {
                    contexto.reportarError(
                        "Comparación entre tipos incompatibles: $tipoIzq != $tipoDer",
                        linea,
                        columna
                    )
                }
            }
        }
    }

    override fun inferirTipo(contexto: ContextoSemantico): String {
        return when (operador) {
            "==", "!!", "<", ">", "<=", ">=", "&&", "||" -> "boolean"
            else -> "number"
        }
    }
}
