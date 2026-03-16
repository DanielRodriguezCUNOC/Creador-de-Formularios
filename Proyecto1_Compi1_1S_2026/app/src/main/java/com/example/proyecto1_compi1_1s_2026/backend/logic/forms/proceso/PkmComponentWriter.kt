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

        val width = valorTexto(node.atributos, "width", "100")
        val height = valorTexto(node.atributos, "height", "100")
        val pointX = valorTexto(node.atributos, "pointX", "0")
        val pointY = valorTexto(node.atributos, "pointY", "0")
        val orientacion = valorTexto(node.atributos, "orientation", "VERTICAL")

        val nodos = mutableListOf<PkmTagNode>()
        nodos.add(PkmTextNode("###"))

        val hijosSeccion = mutableListOf<PkmTagNode>()
        val styleNode = crearBloqueStylesSiExiste(node.atributos)
        if (styleNode != null) {
            hijosSeccion.add(styleNode)
        }
        hijosSeccion.add(PkmElementNode("<content>", "</content>", hijosInternos.toMutableList()))

        nodos.add(
            PkmElementNode(
                apertura = "<section=$width,$height,$pointX,$pointY,$orientacion>",
                cierre = "</section>",
                hijos = hijosSeccion
            )
        )
        nodos.add(PkmTextNode("###"))
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
                hijosLinea.add(PkmElementNode("<element>", "</element>", celda.toMutableList()))
            }
            lineasTabla.add(PkmElementNode("<line>", "</line>", hijosLinea))
        }

        hijosTabla.add(PkmElementNode("<content>", "</content>", lineasTabla))
        return PkmElementNode("<table>", "</table>", hijosTabla)
    }

    fun crearTexto(node: ComponenteTexto): PkmTagNode {
        val width = valorTexto(node.atributos, "width", "50")
        val height = valorTexto(node.atributos, "height", "10")
        val content = valorTexto(node.atributos, "content", "\"\"")

        if (NodoAtributo.contiene(node.atributos, "styles")) {
            val hijos = mutableListOf<PkmTagNode>()
            val styleNode = crearBloqueStylesSiExiste(node.atributos)
            if (styleNode != null) {
                hijos.add(styleNode)
            }
            return PkmElementNode("<open=$width,$height,$content>", "</open>", hijos)
        } else {
            return PkmElementNode("<open=$width,$height,$content/>")
        }
    }

    fun crearPreguntaDesplegable(node: PreguntaDesplegable): PkmTagNode {
        stats.registrarPreguntaDesplegable()
        val width = valorTexto(node.atributos, "width", "50")
        val height = valorTexto(node.atributos, "height", "10")
        val label = valorTexto(node.atributos, "label", "\"\"")

        val optionsRaw = NodoAtributo.valor(node.atributos, "options")
        val options = if (optionsRaw is NodoExpresion) expressionWriter.expresionComoTexto(optionsRaw)
        else expressionWriter.valorComoTexto(optionsRaw)

        val correctRaw = NodoAtributo.valor(node.atributos, "correct")
        val correct = if (correctRaw == null) "-1" else expressionWriter.valorComoTexto(correctRaw)

        if (NodoAtributo.contiene(node.atributos, "styles")) {
            val hijos = mutableListOf<PkmTagNode>()
            val styleNode = crearBloqueStylesSiExiste(node.atributos)
            if (styleNode != null) {
                hijos.add(styleNode)
            }
            return PkmElementNode("<drop=$width,$height,$label,$options,$correct>", "</drop>", hijos)
        } else {
            return PkmElementNode("<drop=$width,$height,$label,$options,$correct/>")
        }
    }

    fun crearPreguntaSeleccion(node: PreguntaSeleccionUnica): PkmTagNode {
        stats.registrarPreguntaSeleccion()
        val width = valorTexto(node.atributos, "width", "50")
        val height = valorTexto(node.atributos, "height", "10")
        val label = valorTexto(node.atributos, "label", "\"\"")

        val optionsRaw = NodoAtributo.valor(node.atributos, "options")
        val options = if (optionsRaw is NodoExpresion) expressionWriter.expresionComoTexto(optionsRaw)
        else expressionWriter.valorComoTexto(optionsRaw)

        val correctRaw = NodoAtributo.valor(node.atributos, "correct")
        val correct = if (correctRaw == null) "-1" else expressionWriter.valorComoTexto(correctRaw)

        if (NodoAtributo.contiene(node.atributos, "styles")) {
            val hijos = mutableListOf<PkmTagNode>()
            val styleNode = crearBloqueStylesSiExiste(node.atributos)
            if (styleNode != null) {
                hijos.add(styleNode)
            }
            return PkmElementNode("<select=$width,$height,$label,$options,$correct>", "</select>", hijos)
        } else {
            return PkmElementNode("<select=$width,$height,$label,$options,$correct/>")
        }
    }

    fun crearPreguntaMultiple(node: PreguntaSeleccionadaMultiple): PkmTagNode {
        stats.registrarPreguntaMultiple()
        val width = valorTexto(node.atributos, "width", "50")
        val height = valorTexto(node.atributos, "height", "10")
        val label = valorTexto(node.atributos, "label", "\"\"")

        val optionsRaw = NodoAtributo.valor(node.atributos, "options")
        val options = if (optionsRaw is NodoExpresion) expressionWriter.expresionComoTexto(optionsRaw)
        else expressionWriter.valorComoTexto(optionsRaw)

        val correctRaw = NodoAtributo.valor(node.atributos, "correct")
        val correct = if (correctRaw == null) "{}" else expressionWriter.valorComoTexto(correctRaw)

        if (NodoAtributo.contiene(node.atributos, "styles")) {
            val hijos = mutableListOf<PkmTagNode>()
            val styleNode = crearBloqueStylesSiExiste(node.atributos)
            if (styleNode != null) {
                hijos.add(styleNode)
            }
            return PkmElementNode("<multiple=$width,$height,$label,$options,$correct>", "</multiple>", hijos)
        } else {
            return PkmElementNode("<multiple=$width,$height,$label,$options,$correct/>")
        }
    }

    fun crearPreguntaAbierta(node: PreguntaAbierta): PkmTagNode {
        stats.registrarPreguntaAbierta()
        val width = valorTexto(node.atributos, "width", "50")
        val height = valorTexto(node.atributos, "height", "10")
        val label = valorTexto(node.atributos, "label", "\"\"")

        if (NodoAtributo.contiene(node.atributos, "styles")) {
            val hijos = mutableListOf<PkmTagNode>()
            val styleNode = crearBloqueStylesSiExiste(node.atributos)
            if (styleNode != null) {
                hijos.add(styleNode)
            }
            return PkmElementNode("<open=$width,$height,$label>", "</open>", hijos)
        } else {
            return PkmElementNode("<open=$width,$height,$label/>")
        }
    }

    private fun crearBloqueStylesSiExiste(attrs: List<NodoAtributo>): PkmTagNode? {
        if (!NodoAtributo.contiene(attrs, "styles")) {
            return null
        }
        return PkmElementNode("<style> ... </style>")
    }

    private fun valorTexto(attrs: List<NodoAtributo>, nombre: String, defecto: String): String {
        val valor = NodoAtributo.valor(attrs, nombre)
        if (valor == null) {
            return defecto
        }
        return expressionWriter.valorComoTexto(valor)
    }
}
