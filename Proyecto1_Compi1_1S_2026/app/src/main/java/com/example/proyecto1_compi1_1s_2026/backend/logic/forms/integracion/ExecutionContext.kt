package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.integracion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.ExpressionNodeBuilder
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TablaSimbolos
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TipoError
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

/**
 * Gestiona el contexto de ejecución: entorno de variables, scopes, y control de flujo.
 * Responsable de todas las operaciones sobre TablaSimbolos y evaluación de expresiones.
 *
 * GRASP Experto: Esta clase es experta en gestionar entornos y variables porque:
 * - Mantiene el estado del entorno actual
 * - Decide cómo crear, mantener y restaurar scopes
 * - Coordina todas las operaciones sobre TablaSimbolos
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

    // ─── Acceso al entorno actual ────────────────────────────────────────────

    fun getEntornoActual(): TablaSimbolos = entornoActual

    fun setEntornoActual(nuevoEntorno: TablaSimbolos) {
        entornoActual = nuevoEntorno
    }

    // ─── Declaraciones y asignaciones ────────────────────────────────────────

    fun declararVariable(id: String, valorInicio: Any?, tipo: String) {
        val valor = if (valorInicio != null) {
            valorInicio
        } else {
            when (tipo) {
                "number" -> 0.0
                "string" -> ""
                else -> null
            }
        }
        entornoActual.almacenarVariable(id, valor ?: "", tipo)
    }

    fun reasignarVariable(id: String, nuevoValor: Any?) {
        try {
            entornoActual.reasignarVariable(id, nuevoValor ?: "")
        } catch (e: Exception) {
            throw e
        }
    }

    fun obtenerVariable(id: String): Any? {
        return entornoActual.obtenerVariable(id)
    }

    fun almacenarVariableEspecial(id: String, valor: Any?) {
        entornoActual.almacenarVariable(id, valor ?: "", "special")
    }

    // ─── Manejo de scopes para control de flujo ──────────────────────────────

    fun crearNuevoScope(): TablaSimbolos {
        return TablaSimbolos(entornoActual)
    }

    fun pushScope(nuevoEntorno: TablaSimbolos) {
        entornoActual = nuevoEntorno
    }

    fun popScope(entornoAnterior: TablaSimbolos) {
        entornoActual = entornoAnterior
    }

    // ─── Evaluación de expresiones ──────────────────────────────────────────

    fun evaluarExpresion(expresion: Any?): Any? {
        return expresion
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

    // Adaptador interno para permitir evaluación de expresiones
    private inner class ExpressionVisitor(private val builder: ExpressionNodeBuilder) : Visitor<Any?> {
        override fun visit(node: NodoLiteral): Any? = builder.construirLiteral(node)
        override fun visit(node: NodoAccesoVariable): Any? = builder.construirAccesoVariable(node)
        override fun visit(node: NodoLlamadaApi): Any? = builder.construirLlamadaApi(node) { it }
        override fun visit(node: NodoOperacionUnaria): Any? = builder.construirOperacionUnaria(node) { it }
        override fun visit(node: NodoOperacionBinaria): Any? = builder.construirOperacionBinaria(node) { it }
        override fun visit(node: NodoDeclaracion): Any? = null
        override fun visit(node: NodoDeclaracionSpecial): Any? = null
        override fun visit(node: NodoAsignacion): Any? = null
        override fun visit(node: NodoSentenciaIf): Any? = null
        override fun visit(node: NodoCicloWhile): Any? = null
        override fun visit(node: NodoCicloDoWhile): Any? = null
        override fun visit(node: NodoCicloFor): Any? = null
        override fun visit(node: NodoDraw): Any? = null
    }
}
