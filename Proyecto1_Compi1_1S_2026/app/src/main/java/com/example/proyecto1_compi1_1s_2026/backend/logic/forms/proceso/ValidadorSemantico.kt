package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

class ValidadorSemantico(var entornoActual: TablaSimbolos) : Visitor<List<String>> {

    private val errores = mutableListOf<String>()

    fun validar(instrucciones: List<NodoInstruccion>): List<String> {
        errores.clear()
        for (instruccion in instrucciones) {
            instruccion.accept(this)
        }
        return errores
    }

    override fun visit(node: NodoLiteral): List<String> = emptyList()

    override fun visit(node: NodoOperacionBinaria): List<String> {
        val erroresIzq = node.izq.accept(this)
        val erroresDer = node.der.accept(this)

        val izq = obtenerTipo(node.izq)
        val der = obtenerTipo(node.der)

        // Validar operación
        when (node.operador) {
            in listOf("+", "-", "*", "/", "%", "^") -> {
                if (izq != "number" || der != "number") {
                    errores.add("Error: Operación aritmética requiere números, pero obtuvo $izq y $der")
                }
            }
            in listOf("&&", "||") -> {
                if (izq != "boolean" || der != "boolean") {
                    errores.add("Error: Operación lógica requiere booleanos, pero obtuvo $izq y $der")
                }
            }
            in listOf("==", "!!") -> {
                if (izq != der) {
                    errores.add("Error: Comparación entre tipos incompatibles: $izq != $der")
                }
            }
        }

        return erroresIzq + erroresDer + errores
    }

    override fun visit(node: NodoAccesoVariable): List<String> {
        return try {
            entornoActual.obtenerVariable(node.id)
            emptyList()
        } catch (e: Exception) {
            listOf("Error: Variable '${node.id}' no fue declarada")
        }
    }

    override fun visit(node: NodoLlamadaApi): List<String> = emptyList()

    override fun visit(node: NodoOperacionUnaria): List<String> {
        val errores = node.expresion.accept(this).toMutableList()
        return errores
    }

    override fun visit(node: NodoDeclaracion): List<String> {
        if (node.valorInicio != null) {
            val tipoValor = obtenerTipo(node.valorInicio)
            if (tipoValor != node.tipo && !(node.tipo == "number" && tipoValor == "double")) {
                errores.add("Error: Tipo incompatible en declaración '${node.id}': se esperaba ${node.tipo}, pero obtuvo $tipoValor")
            }
        }
        entornoActual.almacenarVariable(node.id, when (node.tipo) {
            "number" -> 0.0
            "string" -> ""
            else -> ""
        }, node.tipo)
        return errores
    }

    override fun visit(node: NodoAsignacion): List<String> {
        val tipoActual = entornoActual.obtenerTipo(node.id)
        val tipoNuevo = obtenerTipo(node.nuevoValor)

        if (tipoActual != null && tipoNuevo != tipoActual) {
            errores.add("Error: No puedes asignar $tipoNuevo a variable de tipo $tipoActual en '${node.id}'")
        }
        return errores
    }

    override fun visit(node: NodoSentenciaIf): List<String> {
        val tipoCondicion = obtenerTipo(node.condicion)
        if (tipoCondicion != "boolean") {
            errores.add("Error: La condición del IF debe ser booleana, pero obtuvo $tipoCondicion")
        }
        node.instruccionesIf.forEach { it.accept(this) }
        node.instruccionesElse?.forEach { it.accept(this) }
        return errores
    }

    override fun visit(node: NodoCicloWhile): List<String> {
        val tipoCondicion = obtenerTipo(node.condicion)
        if (tipoCondicion != "boolean") {
            errores.add("Error: La condición del WHILE debe ser booleana")
        }
        node.instruccionesWhile.forEach { it.accept(this) }
        return errores
    }

    override fun visit(node: NodoCicloDoWhile): List<String> {
        val tipoCondicion = obtenerTipo(node.condicion)
        if (tipoCondicion != "boolean") {
            errores.add("Error: La condición del DO-WHILE debe ser booleana")
        }
        node.instrucciones.forEach { it.accept(this) }
        return errores
    }

    override fun visit(node: NodoCicloFor): List<String> {
        val tipoInicio = obtenerTipo(node.rangoInicio)
        val tipoFin = obtenerTipo(node.rangoFin)
        if (tipoInicio != "number" || tipoFin != "number") {
            errores.add("Error: Los rangos del FOR deben ser números")
        }
        node.instruccionesFor.forEach { it.accept(this) }
        return errores
    }

    override fun visit(node: NodoDraw): List<String> = emptyList()

    override fun visit(node: ComponenteSeccion): List<String> {
        node.elementosInternos.forEach { it.accept(this) }
        return errores
    }

    override fun visit(node: ComponenteTabla): List<String> = emptyList()
    override fun visit(node: ComponenteTexto): List<String> = emptyList()
    override fun visit(node: PreguntaDesplegable): List<String> = emptyList()
    override fun visit(node: PreguntaSeleccionUnica): List<String> = emptyList()
    override fun visit(node: PreguntaSeleccionadaMultiple): List<String> = emptyList()
    override fun visit(node: PreguntaAbierta): List<String> = emptyList()

    private fun obtenerTipo(expresion: NodoExpresion): String {
        return when (expresion) {
            is NodoLiteral -> {
                when (expresion.tipo) {
                    "number" -> "number"
                    "string" -> "string"
                    else -> "unknown"
                }
            }
            is NodoAccesoVariable -> {
                try {
                    entornoActual.obtenerTipo(expresion.id) ?: "unknown"
                } catch (e: Exception) {
                    "unknown"
                }
            }
            is NodoOperacionBinaria -> {
                if (expresion.operador in listOf("==", "!=", "<", ">", "<=", ">=", "&&", "||")) "boolean"
                else "number"
            }
            is NodoOperacionUnaria -> {
                if (expresion.operador == "!") "boolean" else "number"
            }
            else -> "unknown"
        }
    }
}