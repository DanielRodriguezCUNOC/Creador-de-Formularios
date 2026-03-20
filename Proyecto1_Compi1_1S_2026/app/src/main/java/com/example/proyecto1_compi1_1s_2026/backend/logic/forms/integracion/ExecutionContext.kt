package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.integracion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ExpressionNodeBuilder
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TablaSimbolos
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TipoError
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo

/**
 * Gestiona el contexto de ejecución: entorno de variables, scopes, y control de flujo.
 */
class ExecutionContext(
    private var entornoActual: TablaSimbolos,
    private val erroresRef: MutableList<ErrorInfo>
) {
    private val exprBuilder by lazy {
        ExpressionNodeBuilder(
            obtenerEntorno = { entornoActual },
            agregarErrorSemantico = { mensaje, linea, columna ->
                erroresRef.add(ErrorInfo(TipoError.SEMANTICO, mensaje, linea, columna))
            }
        )
    }

    fun getEntornoActual(): TablaSimbolos = entornoActual

    fun setEntornoActual(nuevoEntorno: TablaSimbolos) {
        entornoActual = nuevoEntorno
    }

    fun declararVariable(id: String, valorInicio: Any?, tipo: String) {
        val valor = valorInicio ?: when (tipo) {
            "number" -> 0.0
            "string" -> ""
            else -> null
        }
        entornoActual.almacenarVariable(id, valor ?: "", tipo)
    }

    fun reasignarVariable(id: String, nuevoValor: Any?) {
        entornoActual.reasignarVariable(id, nuevoValor ?: "")
    }

    fun obtenerVariable(id: String): Any? {
        return entornoActual.obtenerVariable(id)
    }

    fun almacenarVariableEspecial(id: String, valor: Any?) {
        entornoActual.almacenarVariable(id, valor ?: "", "special")
    }

    fun crearNuevoScope(): TablaSimbolos {
        return TablaSimbolos(entornoActual)
    }

    fun pushScope(nuevoEntorno: TablaSimbolos) {
        entornoActual = nuevoEntorno
    }

    fun popScope(entornoAnterior: TablaSimbolos) {
        entornoActual = entornoAnterior
    }

    fun evaluarExpresion(expresion: Any?): Any? {
        return when (expresion) {
            is NodoExpresion -> expresion.accept(ExpressionVisitor(exprBuilder))
            else -> expresion
        }
    }

    fun evaluarCondicion(valor: Any?): Boolean {
        return exprBuilder.toBool(valor)
    }

    fun evaluarRangoInicio(nodo: NodoCicloFor): Int {
        return exprBuilder.toDouble(nodo.rangoInicio?.accept(ExpressionVisitor(exprBuilder)))?.toInt() ?: 0
    }

    fun evaluarRangoFin(nodo: NodoCicloFor): Int {
        return exprBuilder.toDouble(nodo.rangoFin.accept(ExpressionVisitor(exprBuilder)))?.toInt() ?: 0
    }

    private inner class ExpressionVisitor(private val builder: ExpressionNodeBuilder) : Visitor<Any?> {
        // Expresiones
        override fun visit(node: NodoLiteral): Any? = builder.construirLiteral(node)
        override fun visit(node: NodoListaExpresiones): Any? {
            return node.elementos.map { elem ->
                if (elem is NodoExpresion) elem.accept(this) else elem
            }
        }
        override fun visit(node: NodoAccesoVariable): Any? = builder.construirAccesoVariable(node)
        override fun visit(node: NodoLlamadaApi): Any? = builder.construirLlamadaApi(node) { it.accept(this) }
        override fun visit(node: NodoOperacionUnaria): Any? = builder.construirOperacionUnaria(node) { it.accept(this) }
        override fun visit(node: NodoOperacionBinaria): Any? = builder.construirOperacionBinaria(node) { it.accept(this) }

        // Instrucciones (no retornan valor en este contexto)
        override fun visit(node: NodoDeclaracion): Any? = null
        override fun visit(node: NodoDeclaracionSpecial): Any? = null
        override fun visit(node: NodoAsignacion): Any? = null
        override fun visit(node: NodoSentenciaIf): Any? = null
        override fun visit(node: NodoCicloWhile): Any? = null
        override fun visit(node: NodoCicloDoWhile): Any? = null
        override fun visit(node: NodoCicloFor): Any? = null
        override fun visit(node: NodoDraw): Any? = null

        // Componentes UI (no retornan valor en este contexto)
        override fun visit(node: ComponenteSeccion): Any? = null
        override fun visit(node: ComponenteTabla): Any? = null
        override fun visit(node: ComponenteTexto): Any? = null
        override fun visit(node: PreguntaDesplegable): Any? = null
        override fun visit(node: PreguntaSeleccionUnica): Any? = null
        override fun visit(node: PreguntaSeleccionadaMultiple): Any? = null
        override fun visit(node: PreguntaAbierta): Any? = null
    }
}
