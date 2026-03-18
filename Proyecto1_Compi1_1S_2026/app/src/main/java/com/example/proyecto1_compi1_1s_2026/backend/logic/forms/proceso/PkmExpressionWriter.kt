package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.NodoAtributo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoAccesoVariable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoLiteral
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoLlamadaApi
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoOperacionBinaria
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoOperacionUnaria

// Convierte expresiones y valores del AST a texto .pkm

class PkmExpressionWriter(private val sanitizer: PkmTextSanitizer) {

    fun valorComoTexto(valor: Any?): String {
        return when (valor) {
            null -> ""
            is NodoExpresion -> expresionComoTexto(valor)
            is String -> sanitizer.cadenaEntreComillas(valor)
            is Number -> numeroComoTexto(valor)
            is Boolean -> valor.toString()
            is List<*> -> listaComoTexto(valor)
            else -> valor.toString()
        }
    }

    fun expresionComoTexto(exp: NodoExpresion): String {
        return when (exp) {
            is NodoLiteral -> {
                if (exp.tipo == "string") {
                    sanitizer.cadenaEntreComillas(exp.valor.toString())
                } else {
                    exp.valor.toString()
                }
            }
            is NodoAccesoVariable -> exp.id
            is NodoOperacionBinaria -> {
                val izq = expresionComoTexto(exp.izq)
                val der = expresionComoTexto(exp.der)
                "$izq ${exp.operador} $der"
            }
            is NodoOperacionUnaria -> {
                val interno = expresionComoTexto(exp.expresion)
                "${exp.operador}$interno"
            }
            is NodoLlamadaApi -> {
                val inicio = expresionComoTexto(exp.rangoInicio)
                val fin = expresionComoTexto(exp.rangoFin)
                "${PkmSerializationContract.API_POKEMON_PREFIX}, $inicio, $fin)"
            }
            else -> "<expr>"
        }
    }

    private fun listaComoTexto(lista: List<*>): String {
        val partes = mutableListOf<String>()
        for (item in lista) {
            if (item is NodoExpresion) {
                partes.add(expresionComoTexto(item))
            } else if (item is NodoAtributo) {
                val valor = valorComoTexto(item.valor)
                partes.add("\"${item.nombre}\":$valor")
            } else if (item is Number) {
                partes.add(numeroComoTexto(item))
            } else if (item is String) {
                partes.add(sanitizer.cadenaEntreComillas(item))
            }
        }
        return "{" + partes.joinToString(",") + "}"
    }

    private fun numeroComoTexto(n: Number): String {
        val d = n.toDouble()
        val i = d.toInt().toDouble()
        return if (d == i) i.toInt().toString() else d.toString()
    }
}
