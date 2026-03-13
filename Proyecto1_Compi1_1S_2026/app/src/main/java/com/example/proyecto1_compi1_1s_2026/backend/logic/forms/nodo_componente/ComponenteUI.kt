package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoAccesoVariable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoLiteral
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoOperacionBinaria
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoInstruccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

abstract class ComponenteUI(
    /** Lista de atributos del componente. Se usa List en lugar de Map porque
     *  el número de atributos es pequeño (≤7) y la búsqueda lineal O(n)
     *  es más eficiente que el overhead del hashing de un HashMap. */
    val atributos: List<NodoAtributo>,
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoInstruccion {
    abstract override fun <T> accept(visitor: Visitor<T>): T

    protected fun validarAtributosObligatorios(
        contexto: ContextoSemantico,
        requeridos: List<String>,
        componente: String
    ) {
        for (nombre in requeridos) {
            if (!NodoAtributo.contiene(atributos, nombre)) {
                contexto.reportarError(
                    "$componente requiere el atributo '$nombre' pero no fue encontrado",
                    linea,
                    columna
                )
            }
        }
    }

    protected fun validarOpcionesNoVacias(contexto: ContextoSemantico, componente: String) {
        val optionsAttr = NodoAtributo.valor(atributos, "options")
        if (optionsAttr is List<*> && optionsAttr.isEmpty()) {
            contexto.reportarError(
                "$componente: El atributo 'options' no puede estar vacío. Proporciona al menos una opción.",
                linea,
                columna
            )
        }
    }

    protected fun validarIndiceCorrect(contexto: ContextoSemantico, componente: String) {
        val tieneCorrect = NodoAtributo.contiene(atributos, "correct")
        val tieneOptions = NodoAtributo.contiene(atributos, "options")
        if (tieneCorrect && !tieneOptions) {
            contexto.reportarError(
                "$componente tiene 'correct' pero le falta 'options'",
                linea,
                columna
            )
        }
    }

    protected fun validarCorrectMultiple(contexto: ContextoSemantico, componente: String) {
        val correctAttr = NodoAtributo.valor(atributos, "correct")
        if (correctAttr is List<*>) {
            @Suppress("UNCHECKED_CAST")
            val indices = correctAttr as List<NodoExpresion>

            for ((idx, expr) in indices.withIndex()) {
                if (expr !is NodoLiteral || expr.tipo != "number") {
                    val descripcion = when (expr) {
                        is NodoLiteral -> "literal de tipo '${expr.tipo}'"
                        is NodoAccesoVariable -> "variable '${expr.id}'"
                        is NodoOperacionBinaria -> "expresión compleja"
                        else -> "expresión inválida"
                    }
                    contexto.reportarError(
                        "$componente: El índice correcto en posición $idx debe ser un número literal, pero es $descripcion",
                        linea,
                        columna
                    )
                }
            }
        }
    }

    protected fun validarBorde(contexto: ContextoSemantico) {
        val borderAttrs = NodoAtributo.valor(atributos, "border")
        if (borderAttrs !is List<*>) return

        @Suppress("UNCHECKED_CAST")
        val attrs = borderAttrs as List<NodoAtributo>

        val tieneGrosor = NodoAtributo.contiene(attrs, "grosor")
        val tieneTipo = NodoAtributo.contiene(attrs, "tipo")
        val tieneColor = NodoAtributo.contiene(attrs, "color")

        if (!tieneGrosor || !tieneTipo || !tieneColor) {
            contexto.reportarError("Border debe tener formato: (número, tipo, color)", linea, columna)
            return
        }

        val tipoVal = NodoAtributo.valor(attrs, "tipo")
        val tipoStr = when (tipoVal) {
            is NodoLiteral -> tipoVal.valor.toString().uppercase()
            else -> tipoVal?.toString()?.uppercase()
        }

        if (tipoStr !in listOf("LINE", "DOTTED", "DOUBLE")) {
            contexto.reportarError(
                "Tipo de borde inválido '$tipoStr'. Permitidos: LINE, DOTTED, DOUBLE",
                linea,
                columna
            )
        }
    }
}