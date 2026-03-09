package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

class ValidadorSemantico(var entornoActual: TablaSimbolos) : Visitor<List<ErrorInfo>> {

    private val errores = mutableListOf<ErrorInfo>()

    fun validar(instrucciones: List<NodoInstruccion>): List<ErrorInfo> {
        errores.clear()
        for (instruccion in instrucciones) {
            instruccion.accept(this)
        }
        return errores
    }

    override fun visit(node: NodoLiteral): List<ErrorInfo> = emptyList()

    override fun visit(node: NodoOperacionBinaria): List<ErrorInfo> {
        // Visitar expresiones hijas primero
        node.izq.accept(this)
        node.der.accept(this)

        val izq = obtenerTipo(node.izq)
        val der = obtenerTipo(node.der)

        // Validar operación
        when (node.operador) {
            in listOf("+", "-", "*", "/", "%", "^") -> {
                if (izq != "number" || der != "number") {
                    errores.add(ErrorInfo(TipoError.SEMANTICO, "Operación aritmética requiere números, pero obtuvo $izq y $der", node.linea, node.columna))
                }
            }
            in listOf("&&", "||") -> {
                if (izq != "boolean" || der != "boolean") {
                    errores.add(ErrorInfo(TipoError.SEMANTICO, "Operación lógica requiere booleanos, pero obtuvo $izq y $der", node.linea, node.columna))
                }
            }
            in listOf("==", "!!") -> {
                if (izq != der) {
                    errores.add(ErrorInfo(TipoError.SEMANTICO, "Comparación entre tipos incompatibles: $izq != $der", node.linea, node.columna))
                }
            }
        }

        return errores
    }

    override fun visit(node: NodoAccesoVariable): List<ErrorInfo> {
        return try {
            entornoActual.obtenerVariable(node.id)
            emptyList()
        } catch (e: Exception) {
            listOf(ErrorInfo(TipoError.SEMANTICO, "Variable '${node.id}' no fue declarada", node.linea, node.columna))
        }
    }

    override fun visit(node: NodoLlamadaApi): List<ErrorInfo> = emptyList()

    override fun visit(node: NodoOperacionUnaria): List<ErrorInfo> {
        val errores = node.expresion.accept(this).toMutableList()
        return errores
    }

    override fun visit(node: NodoDeclaracion): List<ErrorInfo> {
        if (node.valorInicio != null) {
            // Primero visitar la expresión para detectar errores internos
            node.valorInicio.accept(this)
            
            val tipoValor = obtenerTipo(node.valorInicio)
            if (tipoValor != node.tipo && !(node.tipo == "number" && tipoValor == "double")) {
                errores.add(ErrorInfo(TipoError.SEMANTICO, "Tipo incompatible en declaración '${node.id}': se esperaba ${node.tipo}, pero obtuvo $tipoValor", node.linea, node.columna))
            }
        }
        entornoActual.almacenarVariable(node.id, when (node.tipo) {
            "number" -> 0.0
            "string" -> ""
            else -> ""
        }, node.tipo)
        return errores
    }

    override fun visit(node: NodoAsignacion): List<ErrorInfo> {
        // Primero visitar la expresión para detectar errores internos
        node.nuevoValor.accept(this)
        
        val tipoActual = entornoActual.obtenerTipo(node.id)
        val tipoNuevo = obtenerTipo(node.nuevoValor)

        if (tipoActual != null && tipoNuevo != tipoActual) {
            errores.add(ErrorInfo(TipoError.SEMANTICO, "No puedes asignar $tipoNuevo a variable de tipo $tipoActual en '${node.id}'", node.linea, node.columna))
        }
        return errores
    }

    override fun visit(node: NodoSentenciaIf): List<ErrorInfo> {
        val tipoCondicion = obtenerTipo(node.condicion)
        if (tipoCondicion != "boolean") {
            errores.add(ErrorInfo(TipoError.SEMANTICO, "La condición del IF debe ser booleana, pero obtuvo $tipoCondicion", node.linea, node.columna))
        }
        node.instruccionesIf.forEach { it.accept(this) }
        node.instruccionesElse?.forEach { it.accept(this) }
        return errores
    }

    override fun visit(node: NodoCicloWhile): List<ErrorInfo> {
        val tipoCondicion = obtenerTipo(node.condicion)
        if (tipoCondicion != "boolean") {
            errores.add(ErrorInfo(TipoError.SEMANTICO, "La condición del WHILE debe ser booleana", node.linea, node.columna))
        }
        node.instruccionesWhile.forEach { it.accept(this) }
        return errores
    }

    override fun visit(node: NodoCicloDoWhile): List<ErrorInfo> {
        val tipoCondicion = obtenerTipo(node.condicion)
        if (tipoCondicion != "boolean") {
            errores.add(ErrorInfo(TipoError.SEMANTICO, "La condición del DO-WHILE debe ser booleana", node.linea, node.columna))
        }
        node.instrucciones.forEach { it.accept(this) }
        return errores
    }

    override fun visit(node: NodoCicloFor): List<ErrorInfo> {
        val tipoInicio = obtenerTipo(node.rangoInicio)
        val tipoFin = obtenerTipo(node.rangoFin)
        if (tipoInicio != "number" || tipoFin != "number") {
            errores.add(ErrorInfo(TipoError.SEMANTICO, "Los rangos del FOR deben ser números", node.linea, node.columna))
        }
        node.instruccionesFor.forEach { it.accept(this) }
        return errores
    }

    override fun visit(node: NodoDraw): List<ErrorInfo> = emptyList()

    override fun visit(node: ComponenteSeccion): List<ErrorInfo> {
        node.elementosInternos.forEach { it.accept(this) }
        return errores
    }

    override fun visit(node: ComponenteTabla): List<ErrorInfo> = emptyList()
    override fun visit(node: ComponenteTexto): List<ErrorInfo> = emptyList()
    override fun visit(node: PreguntaDesplegable): List<ErrorInfo> = emptyList()
    override fun visit(node: PreguntaSeleccionUnica): List<ErrorInfo> = emptyList()
    override fun visit(node: PreguntaSeleccionadaMultiple): List<ErrorInfo> = emptyList()
    override fun visit(node: PreguntaAbierta): List<ErrorInfo> = emptyList()

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