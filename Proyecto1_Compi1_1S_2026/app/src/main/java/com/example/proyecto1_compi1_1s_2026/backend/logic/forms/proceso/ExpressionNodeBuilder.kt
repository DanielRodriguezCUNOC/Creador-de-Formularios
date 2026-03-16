package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoAccesoVariable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoLiteral
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoLlamadaApi
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoOperacionBinaria
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoOperacionUnaria
import kotlin.math.pow

class ExpressionNodeBuilder(
    private val obtenerEntorno: () -> TablaSimbolos,
    private val agregarErrorSemantico: (mensaje: String, linea: Int, columna: Int) -> Unit
) {

    fun construirLiteral(node: NodoLiteral): Any? {
        if (node.tipo == "string" && node.valor is String) {
            return normalizarTextoConEmojis(node.valor)
        }
        return node.valor
    }

    private fun normalizarTextoConEmojis(texto: String): String {
        var out = texto

        // Formas simbólicas
        out = out.replace(Regex("@\\[:\\)+\\]"), "😄")
        out = out.replace(Regex("@\\[:\\(+\\]"), "😢")
        out = out.replace(Regex("@\\[:\\|+\\]"), "😐")
        out = out.replace(Regex("@\\[<+3+\\]"), "❤️")

        // Formas nombradas
        out = out.replace("@[:smile:]", "😄")
        out = out.replace("@[:sad:]", "😢")
        out = out.replace("@[:serious:]", "😐")
        out = out.replace("@[:heart:]", "❤️")

        return out
    }

    fun construirAccesoVariable(node: NodoAccesoVariable): Any? {
        return try {
            obtenerEntorno().obtenerVariable(node.id)
        } catch (_: Exception) {
            agregarErrorSemantico("Variable '${node.id}' no declarada", node.linea, node.columna)
            null
        }
    }

    fun construirLlamadaApi(node: NodoLlamadaApi, evaluarExpresion: (NodoExpresion) -> Any?): Any? {
        if (node.tipo == "pokemon") {
            // Evaluar límites del rango
            val inicio = toDouble(evaluarExpresion(node.rangoInicio))?.toInt() ?: 0
            val fin = toDouble(evaluarExpresion(node.rangoFin))?.toInt() ?: 0

            // Consumir PokéAPI de forma imperativa
            val nombres = PokemonApiService.obtenerNombresEnRango(inicio, fin)

            // 3) Si no se obtuvo nada, registrar error semántico
            if (nombres.isEmpty()) {
                agregarErrorSemantico(
                    "who_is_that_pokemon: No fue posible obtener pokémones para el rango $inicio..$fin",
                    node.linea,
                    node.columna
                )
            }

            return nombres
        }
        return null
    }

    fun construirOperacionUnaria(node: NodoOperacionUnaria, evaluarExpresion: (NodoExpresion) -> Any?): Any? {
        val valor = evaluarExpresion(node.expresion)

        return when (node.operador) {
            "-" -> when (valor) {
                is Double -> -valor
                is Int -> -valor
                is Number -> -valor.toDouble()
                else -> null
            }
            "~" -> when (valor) {
                is Boolean -> !valor
                else -> null
            }
            else -> valor
        }
    }

    fun construirOperacionBinaria(node: NodoOperacionBinaria, evaluarExpresion: (NodoExpresion) -> Any?): Any? {
        val izquierda = evaluarExpresion(node.izq)
        val derecha = evaluarExpresion(node.der)

        val izquierdaNumero = toDouble(izquierda)
        val derechaNumero = toDouble(derecha)

        return when (node.operador) {
            "+" -> {
                if (izquierda is String || derecha is String) {
                    "${izquierda ?: ""}${derecha ?: ""}"
                } else {
                    izquierdaNumero?.plus(derechaNumero ?: 0.0)
                }
            }
            "-" -> izquierdaNumero?.minus(derechaNumero ?: 0.0)
            "*" -> izquierdaNumero?.times(derechaNumero ?: 0.0)
            "/" -> {
                if (derechaNumero != null && derechaNumero != 0.0) {
                    izquierdaNumero?.div(derechaNumero)
                } else {
                    agregarErrorSemantico("División por cero", node.linea, node.columna)
                    null
                }
            }
            "%" -> izquierdaNumero?.rem(derechaNumero ?: 1.0)
            "^" -> if (izquierdaNumero != null && derechaNumero != null) izquierdaNumero.pow(derechaNumero) else null
            ">" -> izquierdaNumero != null && derechaNumero != null && izquierdaNumero > derechaNumero
            ">=" -> izquierdaNumero != null && derechaNumero != null && izquierdaNumero >= derechaNumero
            "<" -> izquierdaNumero != null && derechaNumero != null && izquierdaNumero < derechaNumero
            "<=" -> izquierdaNumero != null && derechaNumero != null && izquierdaNumero <= derechaNumero
            "==" -> izquierda == derecha
            "!!" -> izquierda != derecha
            "&&" -> toBool(izquierda) && toBool(derecha)
            "||" -> toBool(izquierda) || toBool(derecha)
            else -> null
        }
    }

    fun toDouble(valor: Any?): Double? {
        return when (valor) {
            is Double -> valor
            is Int -> valor.toDouble()
            is Float -> valor.toDouble()
            is Long -> valor.toDouble()
            is Number -> valor.toDouble()
            is String -> valor.toDoubleOrNull()
            else -> null
        }
    }

    fun toBool(valor: Any?): Boolean {
        return when (valor) {
            is Boolean -> valor
            is Number -> valor.toDouble() != 0.0
            is String -> valor.isNotEmpty()
            else -> false
        }
    }
}
