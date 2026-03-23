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
                // '+' permite concatenación cuando alguno de los operandos es string.
                if (operador == "+" && (tipoIzq == "string" || tipoDer == "string")) {
                    return
                }

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

                val operadores = mutableSetOf<String>()
                recolectarOperadoresLogicos(this, operadores)
                if (operadores.contains("&&") && operadores.contains("||")) {
                    contexto.reportarError(
                        "No se permite mezclar operadores lógicos '&&' y '||' en la misma expresión",
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

                // Regla del lenguaje: '!!' no se usa para lógica booleana.
                if (operador == "!!" && tipoIzq == "boolean" && tipoDer == "boolean") {
                    contexto.reportarError(
                        "No se permite usar '!!' entre booleanos; utiliza operadores lógicos (&& o ||)",
                        linea,
                        columna
                    )
                }
            }
        }
    }

    private fun recolectarOperadoresLogicos(exp: NodoExpresion, acumulador: MutableSet<String>) {
        if (exp is NodoOperacionBinaria) {
            if (exp.operador == "&&" || exp.operador == "||") {
                acumulador.add(exp.operador)
                recolectarOperadoresLogicos(exp.izq, acumulador)
                recolectarOperadoresLogicos(exp.der, acumulador)
            }
        }
    }

    override fun inferirTipo(contexto: ContextoSemantico): String {
        return when (operador) {
            "==", "!!", "<", ">", "<=", ">=", "&&", "||" -> "boolean"
            "+" -> {
                val tipoIzq = izq.inferirTipo(contexto)
                val tipoDer = der.inferirTipo(contexto)
                if (tipoIzq == "string" || tipoDer == "string") "string" else "number"
            }
            else -> "number"
        }
    }
}
