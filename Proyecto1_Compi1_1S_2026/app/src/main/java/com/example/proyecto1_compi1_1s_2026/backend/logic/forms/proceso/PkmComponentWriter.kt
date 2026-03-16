package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteSeccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteTabla
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteTexto
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteUI
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.NodoAtributo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaAbierta
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaDesplegable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaSeleccionUnica
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaSeleccionadaMultiple
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoLiteral
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoInstruccion

// Renderiza componentes del AST al formato de etiquetas .pkm.

class PkmComponentWriter(
    private val expressionWriter: PkmExpressionWriter,
    private val stats: PkmStatsCollector
) {

    fun crearSeccion(node: ComponenteSeccion, hijosInternos: List<PkmTagNode>): List<PkmTagNode> {
        stats.registrarSeccion()

        val width = valorTexto(node.atributos, "width", PkmSerializationContract.DEFAULT_SECTION_WIDTH)
        val height = valorTexto(node.atributos, "height", PkmSerializationContract.DEFAULT_SECTION_HEIGHT)
        val pointX = valorTexto(node.atributos, "pointX", PkmSerializationContract.DEFAULT_POINT_X)
        val pointY = valorTexto(node.atributos, "pointY", PkmSerializationContract.DEFAULT_POINT_Y)
        val orientacion = valorTexto(node.atributos, "orientation", PkmSerializationContract.DEFAULT_ORIENTATION)

        val nodos = mutableListOf<PkmTagNode>()
        nodos.add(PkmTextNode(PkmSerializationContract.BLOQUE_DELIMITADOR))

        val hijosSeccion = mutableListOf<PkmTagNode>()
        val styleNode = crearBloqueStylesSiExiste(node.atributos)
        if (styleNode != null) {
            hijosSeccion.add(styleNode)
        }
        hijosSeccion.add(PkmElementNode("<content>", "</content>", hijosInternos.toMutableList()))

        nodos.add(
            PkmElementNode(
                apertura = PkmSerializationContract.tagSectionOpen(width, height, pointX, pointY, orientacion),
                cierre = PkmSerializationContract.tagSectionClose(),
                hijos = hijosSeccion
            )
        )
        nodos.add(PkmTextNode(PkmSerializationContract.BLOQUE_DELIMITADOR))
        return nodos
    }

    fun crearTabla(node: ComponenteTabla, filasRenderizadas: List<List<List<PkmTagNode>>>): PkmTagNode {
        val hijosTabla = mutableListOf<PkmTagNode>()
        val styleNode = crearBloqueStylesSiExiste(node.atributos)
        if (styleNode != null) {
            hijosTabla.add(styleNode)
        }

        val lineasTabla = mutableListOf<PkmTagNode>()
        for (fila in filasRenderizadas) {
            val hijosLinea = mutableListOf<PkmTagNode>()
            for (celda in fila) {
                hijosLinea.add(
                    PkmElementNode(
                        PkmSerializationContract.tagElementOpen(),
                        PkmSerializationContract.tagElementClose(),
                        celda.toMutableList()
                    )
                )
            }
            lineasTabla.add(
                PkmElementNode(
                    PkmSerializationContract.tagLineOpen(),
                    PkmSerializationContract.tagLineClose(),
                    hijosLinea
                )
            )
        }

        hijosTabla.add(PkmElementNode(PkmSerializationContract.tagContentOpen(), PkmSerializationContract.tagContentClose(), lineasTabla))
        return PkmElementNode(PkmSerializationContract.tagTableOpen(), PkmSerializationContract.tagTableClose(), hijosTabla)
    }

    fun crearTexto(node: ComponenteTexto): PkmTagNode {
        val width = valorTexto(node.atributos, "width", PkmSerializationContract.DEFAULT_WIDTH)
        val height = valorTexto(node.atributos, "height", PkmSerializationContract.DEFAULT_HEIGHT)
        val content = valorTexto(node.atributos, "content", "\"\"")

        val hijos = mutableListOf<PkmTagNode>()
        val styleNode = crearBloqueStylesSiExiste(node.atributos)
        if (styleNode != null) {
            hijos.add(styleNode)
        }

        // Si hay estilos, serializa como bloque abierto, si no, como self-closing
        return if (hijos.isNotEmpty()) {
            PkmElementNode(
                PkmSerializationContract.tagOpenTextOpen(width, height, content),
                PkmSerializationContract.tagOpenTextClose(),
                hijos
            )
        } else {
            PkmElementNode(PkmSerializationContract.tagOpenTextSelf(width, height, content))
        }
    }

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

        if (NodoAtributo.contiene(node.atributos, "styles")) {
            val hijos = mutableListOf<PkmTagNode>()
            val styleNode = crearBloqueStylesSiExiste(node.atributos)
            if (styleNode != null) {
                hijos.add(styleNode)
            }
            return PkmElementNode(
                PkmSerializationContract.tagDropOpen(width, height, label, options, correct),
                PkmSerializationContract.tagDropClose(),
                hijos
            )
        } else {
            return PkmElementNode(PkmSerializationContract.tagDropSelf(width, height, label, options, correct))
        }
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

        if (NodoAtributo.contiene(node.atributos, "styles")) {
            val hijos = mutableListOf<PkmTagNode>()
            val styleNode = crearBloqueStylesSiExiste(node.atributos)
            if (styleNode != null) {
                hijos.add(styleNode)
            }
            return PkmElementNode(
                PkmSerializationContract.tagSelectOpen(width, height, label, options, correct),
                PkmSerializationContract.tagSelectClose(),
                hijos
            )
        } else {
            return PkmElementNode(PkmSerializationContract.tagSelectSelf(width, height, label, options, correct))
        }
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

        if (NodoAtributo.contiene(node.atributos, "styles")) {
            val hijos = mutableListOf<PkmTagNode>()
            val styleNode = crearBloqueStylesSiExiste(node.atributos)
            if (styleNode != null) {
                hijos.add(styleNode)
            }
            return PkmElementNode(
                PkmSerializationContract.tagMultipleOpen(width, height, label, options, correct),
                PkmSerializationContract.tagMultipleClose(),
                hijos
            )
        } else {
            return PkmElementNode(PkmSerializationContract.tagMultipleSelf(width, height, label, options, correct))
        }
    }

    fun crearPreguntaAbierta(node: PreguntaAbierta): PkmTagNode {
        stats.registrarPreguntaAbierta()
        val width = valorTexto(node.atributos, "width", PkmSerializationContract.DEFAULT_WIDTH)
        val height = valorTexto(node.atributos, "height", PkmSerializationContract.DEFAULT_HEIGHT)
        val label = valorTexto(node.atributos, "label", "\"\"")

        if (NodoAtributo.contiene(node.atributos, "styles")) {
            val hijos = mutableListOf<PkmTagNode>()
            val styleNode = crearBloqueStylesSiExiste(node.atributos)
            if (styleNode != null) {
                hijos.add(styleNode)
            }
            return PkmElementNode(
                PkmSerializationContract.tagOpenTextOpen(width, height, label),
                PkmSerializationContract.tagOpenTextClose(),
                hijos
            )
        } else {
            return PkmElementNode(PkmSerializationContract.tagOpenTextSelf(width, height, label))
        }
    }

    private fun crearBloqueStylesSiExiste(attrs: List<NodoAtributo>): PkmTagNode? {
        val stylesRaw = NodoAtributo.valor(attrs, "styles") ?: return null
        val stylesText = expressionWriter.valorComoTexto(stylesRaw)
        // Serializa el bloque de estilos real según el contrato
        return PkmElementNode(
            PkmSerializationContract.tagStyleOpen(),
            PkmSerializationContract.tagStyleClose(),
            mutableListOf(PkmTextNode(stylesText))
        )
    }

    private fun valorTexto(attrs: List<NodoAtributo>, nombre: String, defecto: String): String {
        val valor = NodoAtributo.valor(attrs, nombre)
        if (valor == null) {
            return defecto
        }
        return expressionWriter.valorComoTexto(valor)
    }
}
