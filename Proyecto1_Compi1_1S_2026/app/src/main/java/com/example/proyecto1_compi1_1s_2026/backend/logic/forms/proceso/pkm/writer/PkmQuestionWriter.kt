package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.NodoAtributo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaAbierta
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaDesplegable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaSeleccionUnica
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaSeleccionadaMultiple
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion

// Renderiza preguntas al formato PKM y centraliza reglas comunes.
class PkmQuestionWriter(
    private val expressionWriter: PkmExpressionWriter,
    private val styleWriter: PkmStyleWriter,
    private val stats: PkmStatsCollector
) {

    fun crearPreguntaDesplegable(node: PreguntaDesplegable): PkmTagNode {
        stats.registrarPreguntaDesplegable()
        val width = valorTexto(node.atributos, "width", PkmSerializationContract.DEFAULT_WIDTH)
        val height = valorTexto(node.atributos, "height", PkmSerializationContract.DEFAULT_HEIGHT)
        val label = valorTexto(node.atributos, "label", "\"\"")

        val optionsRaw = NodoAtributo.valor(node.atributos, "options")
        val options = if (optionsRaw is NodoExpresion) expressionWriter.expresionComoTexto(optionsRaw)
        else expressionWriter.valorComoTexto(optionsRaw)

        val correctRaw = NodoAtributo.valor(node.atributos, "correct")
        val correct = if (correctRaw == null) PkmSerializationContract.DEFAULT_CORRECT_SINGLE else expressionWriter.valorComoTexto(correctRaw)

        return crearConStyleOpcional(
            attrs = node.atributos,
            contexto = "DROP_QUESTION",
            linea = node.linea,
            columna = node.columna,
            apertura = PkmSerializationContract.tagDropOpen(width, height, label, options, correct),
            cierre = PkmSerializationContract.tagDropClose(),
            autocierre = PkmSerializationContract.tagDropSelf(width, height, label, options, correct)
        )
    }

    fun crearPreguntaSeleccion(node: PreguntaSeleccionUnica): PkmTagNode {
        stats.registrarPreguntaSeleccion()
        val width = valorTexto(node.atributos, "width", PkmSerializationContract.DEFAULT_WIDTH)
        val height = valorTexto(node.atributos, "height", PkmSerializationContract.DEFAULT_HEIGHT)
        val label = valorTexto(node.atributos, "label", "\"\"")

        val optionsRaw = NodoAtributo.valor(node.atributos, "options")
        val options = if (optionsRaw is NodoExpresion) expressionWriter.expresionComoTexto(optionsRaw)
        else expressionWriter.valorComoTexto(optionsRaw)

        val correctRaw = NodoAtributo.valor(node.atributos, "correct")
        val correct = if (correctRaw == null) PkmSerializationContract.DEFAULT_CORRECT_SINGLE else expressionWriter.valorComoTexto(correctRaw)

        return crearConStyleOpcional(
            attrs = node.atributos,
            contexto = "SELECT_QUESTION",
            linea = node.linea,
            columna = node.columna,
            apertura = PkmSerializationContract.tagSelectOpen(width, height, label, options, correct),
            cierre = PkmSerializationContract.tagSelectClose(),
            autocierre = PkmSerializationContract.tagSelectSelf(width, height, label, options, correct)
        )
    }

    fun crearPreguntaMultiple(node: PreguntaSeleccionadaMultiple): PkmTagNode {
        stats.registrarPreguntaMultiple()
        val width = valorTexto(node.atributos, "width", PkmSerializationContract.DEFAULT_WIDTH)
        val height = valorTexto(node.atributos, "height", PkmSerializationContract.DEFAULT_HEIGHT)
        val label = valorTexto(node.atributos, "label", "\"\"")

        val optionsRaw = NodoAtributo.valor(node.atributos, "options")
        val options = if (optionsRaw is NodoExpresion) expressionWriter.expresionComoTexto(optionsRaw)
        else expressionWriter.valorComoTexto(optionsRaw)

        val correctRaw = NodoAtributo.valor(node.atributos, "correct")
        val correct = if (correctRaw == null) PkmSerializationContract.DEFAULT_CORRECT_MULTIPLE else expressionWriter.valorComoTexto(correctRaw)

        return crearConStyleOpcional(
            attrs = node.atributos,
            contexto = "MULTIPLE_QUESTION",
            linea = node.linea,
            columna = node.columna,
            apertura = PkmSerializationContract.tagMultipleOpen(width, height, label, options, correct),
            cierre = PkmSerializationContract.tagMultipleClose(),
            autocierre = PkmSerializationContract.tagMultipleSelf(width, height, label, options, correct)
        )
    }

    fun crearPreguntaAbierta(node: PreguntaAbierta): PkmTagNode {
        stats.registrarPreguntaAbierta()
        val width = valorTexto(node.atributos, "width", PkmSerializationContract.DEFAULT_WIDTH)
        val height = valorTexto(node.atributos, "height", PkmSerializationContract.DEFAULT_HEIGHT)
        val label = valorTexto(node.atributos, "label", "\"\"")

        return crearConStyleOpcional(
            attrs = node.atributos,
            contexto = "OPEN_QUESTION",
            linea = node.linea,
            columna = node.columna,
            apertura = PkmSerializationContract.tagOpenTextOpen(width, height, label),
            cierre = PkmSerializationContract.tagOpenTextClose(),
            autocierre = PkmSerializationContract.tagOpenTextSelf(width, height, label)
        )
    }

    private fun crearConStyleOpcional(
        attrs: List<NodoAtributo>,
        contexto: String,
        linea: Int,
        columna: Int,
        apertura: String,
        cierre: String,
        autocierre: String
    ): PkmTagNode {
        val hijos = mutableListOf<PkmTagNode>()
        val styleNode = styleWriter.crearBloqueStylesSiExiste(attrs, contexto, linea, columna)
        if (styleNode != null) {
            hijos.add(styleNode)
        }

        return if (hijos.isNotEmpty()) {
            PkmElementNode(apertura, cierre, hijos)
        } else {
            PkmElementNode(autocierre)
        }
    }

    private fun valorTexto(attrs: List<NodoAtributo>, nombre: String, defecto: String): String {
        val valor = NodoAtributo.valor(attrs, nombre)
        if (valor == null) {
            return defecto
        }
        return expressionWriter.valorComoTexto(valor)
    }
}
