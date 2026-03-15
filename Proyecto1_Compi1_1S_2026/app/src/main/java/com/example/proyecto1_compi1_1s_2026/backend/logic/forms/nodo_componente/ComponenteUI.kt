package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoAccesoVariable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoLiteral
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoOperacionBinaria
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoInstruccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

abstract class ComponenteUI(

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
        validarAtributosDuplicados(contexto, componente)

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

    protected fun validarAtributosDuplicados(contexto: ContextoSemantico, componente: String) {
        val repetidos = atributos.groupingBy { it.nombre }.eachCount().filter { it.value > 1 }
        for ((nombre, cantidad) in repetidos) {
            contexto.reportarError(
                "$componente: el atributo '$nombre' está repetido $cantidad veces. Los atributos no deben repetirse.",
                linea,
                columna
            )
        }

        val styles = NodoAtributo.valor(atributos, "styles")
        if (styles is List<*>) {
            val stylesAttrs = styles.filterIsInstance<NodoAtributo>()
            val repetidosStyles = stylesAttrs.groupingBy { it.nombre }.eachCount().filter { it.value > 1 }
            for ((nombre, cantidad) in repetidosStyles) {
                contexto.reportarError(
                    "$componente.styles: el atributo '$nombre' está repetido $cantidad veces. Los estilos no deben repetirse.",
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
            return
        }

        if (!tieneCorrect) return

        val correctAttr = NodoAtributo.valor(atributos, "correct")
        val indice = extraerIndiceEntero(correctAttr)
        if (indice == null) {
            contexto.reportarError(
                "$componente: 'correct' debe ser un número entero",
                linea,
                columna
            )
            return
        }

        val cantidadOpciones = obtenerCantidadOpcionesLiteral()
        if (cantidadOpciones != null && (indice < 0 || indice >= cantidadOpciones)) {
            contexto.reportarError(
                "$componente: 'correct'=$indice está fuera de rango. Índices válidos: 0..${cantidadOpciones - 1}",
                linea,
                columna
            )
        }
    }

    protected fun validarCorrectMultiple(contexto: ContextoSemantico, componente: String) {
        val correctAttr = NodoAtributo.valor(atributos, "correct")
        val cantidadOpciones = obtenerCantidadOpcionesLiteral()

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
                } else {
                    val valorIndice = expr.valor.toString().toDoubleOrNull()?.toInt()
                    if (valorIndice == null) {
                        contexto.reportarError(
                            "$componente: el índice correcto en posición $idx debe ser entero",
                            linea,
                            columna
                        )
                    } else if (cantidadOpciones != null && (valorIndice < 0 || valorIndice >= cantidadOpciones)) {
                        contexto.reportarError(
                            "$componente: el índice correcto en posición $idx (= $valorIndice) está fuera de rango. Índices válidos: 0..${cantidadOpciones - 1}",
                            linea,
                            columna
                        )
                    }
                }
            }
        }
    }

    protected fun validarAdvertenciaMaxOpciones(contexto: ContextoSemantico, componente: String, maximo: Int) {
        val cantidadOpciones = obtenerCantidadOpcionesLiteral() ?: return
        if (cantidadOpciones > maximo) {
            contexto.reportarError(
                "Advertencia: $componente tiene $cantidadOpciones opciones. Se recomienda no exceder $maximo.",
                linea,
                columna
            )
        }
    }

    private fun obtenerCantidadOpcionesLiteral(): Int? {
        val optionsAttr = NodoAtributo.valor(atributos, "options")
        return if (optionsAttr is List<*>) optionsAttr.size else null
    }

    private fun extraerIndiceEntero(valor: Any?): Int? {
        return when (valor) {
            is NodoLiteral -> valor.valor.toString().toDoubleOrNull()?.toInt()
            is Number -> valor.toInt()
            is String -> valor.toDoubleOrNull()?.toInt()
            else -> null
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