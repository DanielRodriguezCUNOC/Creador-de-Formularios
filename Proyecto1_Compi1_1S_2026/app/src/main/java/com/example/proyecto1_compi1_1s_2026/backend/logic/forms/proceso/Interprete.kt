package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import kotlin.math.pow

class Interprete(var entornoActual: TablaSimbolos) : Visitor<Any?> {

    override fun visit(node: NodoLiteral): Any? {
        return node.valor
    }

    override fun visit(node: NodoOperacionBinaria): Any? {
        val izq = node.izq.accept(this)
        val der = node.der.accept(this)

        return when (node.operador) {
            "+" -> {
                if (izq is Double && der is Double) izq + der
                else izq.toString() + der.toString()
            }
            "-" -> (izq as Double) - (der as Double)
            "*" -> (izq as Double) * (der as Double)
            "/" -> {
                val divisor = der as Double
                if (divisor == 0.0) throw Exception("División por cero")
                (izq as Double) / divisor
            }
            "%" -> (izq as Double) % (der as Double)
            "^" -> (izq as Double).pow(der as Double)
            "==" -> izq == der
            "!!" -> izq != der
            ">" -> (izq as Double) > (der as Double)
            "<" -> (izq as Double) < (der as Double)
            ">=" -> (izq as Double) >= (der as Double)
            "<=" -> (izq as Double) <= (der as Double)
            "&&" -> (izq as Boolean) && (der as Boolean)
            "||" -> (izq as Boolean) || (der as Boolean)
            else -> throw Exception("Operador desconocido: ${node.operador}")
        }
    }

    override fun visit(node: NodoAccesoVariable): Any? {
        return entornoActual.obtenerVariable(node.id)
    }

    override fun visit(node: NodoLlamadaApi): Any? {
        if (node.tipo == "pokemon") {
            val inicio = node.rangoInicio.accept(this) as? Double ?: 0.0
            val fin = node.rangoFin.accept(this) as? Double ?: 0.0
            return "Pokemons del ${inicio.toInt()} al ${fin.toInt()}"
        }
        return null
    }

    override fun visit(node: NodoOperacionUnaria): Any? {
        val valor = node.expresion.accept(this)
        return when (node.operador) {
            "-" -> -(valor as Double)
            "!" -> !(valor as Boolean)
            else -> throw Exception("Operador unario desconocido: ${node.operador}")
        }
    }

    override fun visit(node: NodoDeclaracion): Any? {
        val valor = node.valorInicio?.accept(this) ?: when (node.tipo) {
            "number" -> 0.0
            "string" -> ""
            "special" -> ""
            else -> ""
        }
        entornoActual.almacenarVariable(node.id, valor!!)
        return null
    }

    override fun visit(node: NodoAsignacion): Any? {
        val valor = node.nuevoValor.accept(this)
        entornoActual.reasignarVariable(node.id, valor!!)
        return null
    }

    override fun visit(node: NodoSentenciaIf): Any? {
        val condicion = node.condicion.accept(this) as? Boolean ?: false
        if (condicion) {
            val nuevoEntorno = TablaSimbolos(entornoActual)
            val temp = entornoActual
            entornoActual = nuevoEntorno
            node.instruccionesIf.forEach { it.accept(this) }
            entornoActual = temp
        } else {
            node.instruccionesElse?.let {
                val nuevoEntorno = TablaSimbolos(entornoActual)
                val temp = entornoActual
                entornoActual = nuevoEntorno
                it.forEach { inst -> inst.accept(this) }
                entornoActual = temp
            }
        }
        return null
    }

    override fun visit(node: NodoCicloWhile): Any? {
        while (node.condicion.accept(this) as? Boolean == true) {
            val nuevoEntorno = TablaSimbolos(entornoActual)
            val temp = entornoActual
            entornoActual = nuevoEntorno
            node.instruccionesWhile.forEach { it.accept(this) }
            entornoActual = temp
        }
        return null
    }

    override fun visit(node: NodoCicloDoWhile): Any? {
        do {
            val nuevoEntorno = TablaSimbolos(entornoActual)
            val temp = entornoActual
            entornoActual = nuevoEntorno
            node.instrucciones.forEach { it.accept(this) }
            entornoActual = temp
        } while (node.condicion.accept(this) as? Boolean == true)
        return null
    }

    override fun visit(node: NodoCicloFor): Any? {
        val inicioVal = (node.rangoInicio.accept(this) as? Double ?: 0.0).toInt()
        val finVal = (node.rangoFin.accept(this) as? Double ?: 0.0).toInt()

        for (i in inicioVal..finVal) {
            val nuevoEntorno = TablaSimbolos(entornoActual)
            nuevoEntorno.almacenarVariable(node.idVariable, i.toDouble())
            val temp = entornoActual
            entornoActual = nuevoEntorno
            node.instruccionesFor.forEach { it.accept(this) }
            entornoActual = temp
        }
        return null
    }

    override fun visit(node: NodoDraw): Any? {
        val evaluados = node.parametros.map { it.accept(this) }
        println("DRAW: ${node.idVariableEspecial} con params: $evaluados")
        return null
    }

    private fun evaluarAtributo(valor: Any?): Any? {
        return when (valor) {
            is NodoExpresion -> valor.accept(this)
            is List<*> -> valor.map { evaluarAtributo(it) }
            is Map<*, *> -> valor.mapValues { (_, v) -> evaluarAtributo(v) }
            else -> valor
        }
    }


    private fun evaluarAtributos(atributos: Map<String, Object>): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        for ((clave, valor) in atributos) {
            result[clave] = evaluarAtributo(valor) ?: ""
        }
        return result
    }

    override fun visit(node: ComponenteSeccion): Any? {
        val evaluados = evaluarAtributos(node.atributos)
        val hijos = node.elementosInternos.mapNotNull { it.accept(this) }
        return mapOf("tipo" to "seccion", "atributos" to evaluados, "hijos" to hijos)
    }

    override fun visit(node: ComponenteTabla): Any? {
        val evaluados = evaluarAtributos(node.atributos)
        val filasEvaluadas = node.filas.map { fila ->
            fila.map { expresion -> expresion.accept(this) }
        }
        return mapOf("tipo" to "tabla", "atributos" to evaluados, "filas" to filasEvaluadas)
    }

    override fun visit(node: ComponenteTexto): Any? {
        val evaluados = evaluarAtributos(node.atributos)
        return mapOf("tipo" to "texto", "atributos" to evaluados)
    }

    override fun visit(node: PreguntaDesplegable): Any? {
        val evaluados = evaluarAtributos(node.atributos)
        return mapOf("tipo" to "desplegable", "atributos" to evaluados)
    }

    override fun visit(node: PreguntaSeleccionUnica): Any? {
        val evaluados = evaluarAtributos(node.atributos)
        return mapOf("tipo" to "seleccion_unica", "atributos" to evaluados)
    }

    override fun visit(node: PreguntaSeleccionadaMultiple): Any? {
        val evaluados = evaluarAtributos(node.atributos)
        return mapOf("tipo" to "seleccion_multiple", "atributos" to evaluados)
    }

    override fun visit(node: PreguntaAbierta): Any? {
        val evaluados = evaluarAtributos(node.atributos)
        return mapOf("tipo" to "pregunta_abierta", "atributos" to evaluados)
    }
}