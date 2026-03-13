package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteUI
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.NodoAtributo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoLiteral
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

class NodoDraw(
    val idVariableEspecial: String,
    /** Permite que recibamos los comodines */
    val parametros: List<NodoExpresion>,
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoInstruccion {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)

    override fun validarSemantica(contexto: ContextoSemantico) {
        parametros.forEach { it.validarSemantica(contexto) }

        val valorEspecial = try {
            contexto.entornoActual.obtenerVariable(idVariableEspecial)
        } catch (_: Exception) {
            contexto.reportarError(
                "Variable especial '$idVariableEspecial' no fue declarada",
                linea,
                columna
            )
            return
        }

        val comodinesEnDraw = parametros.count { param ->
            param is NodoLiteral && param.tipo == "comodin"
        }

        val comodinesEnVariable = contarComodinesEnComponente(valorEspecial)

        if (comodinesEnDraw != comodinesEnVariable) {
            contexto.reportarError(
                "Variable especial '$idVariableEspecial': Esperaba $comodinesEnVariable comodín(es) pero .draw() proporciona $comodinesEnDraw",
                linea,
                columna
            )
        }
    }

    private fun contarComodinesEnComponente(valor: Any?): Int {
        val atributos: List<NodoAtributo> = when (valor) {
            is ComponenteUI -> valor.atributos
            is List<*> -> {
                @Suppress("UNCHECKED_CAST")
                (valor as? List<NodoAtributo>) ?: return 0
            }
            else -> return 0
        }

        var count = 0
        for (attr in atributos) {
            when (val v = attr.valor) {
                is String -> count += v.count { it == '?' }
                is NodoLiteral -> if (v.tipo == "string") count += v.valor.toString().count { it == '?' }
                is List<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val exprs = v as? List<NodoExpresion>
                    exprs?.forEach { expr ->
                        if (expr is NodoLiteral) {
                            if (expr.tipo == "comodin") count++
                            if (expr.tipo == "string") count += expr.valor.toString().count { it == '?' }
                        }
                    }
                }
            }
        }
        return count
    }
}