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
    private val lineWriter: PkmLineWriter,
    private val expressionWriter: PkmExpressionWriter,
    private val stats: PkmStatsCollector
) {

    fun escribirSeccion(node: ComponenteSeccion, renderInstruccion: (NodoInstruccion) -> Unit) {
        stats.registrarSeccion()

        val width = valorTexto(node.atributos, "width", "100")
        val height = valorTexto(node.atributos, "height", "100")
        val pointX = valorTexto(node.atributos, "pointX", "0")
        val pointY = valorTexto(node.atributos, "pointY", "0")
        val orientacion = valorTexto(node.atributos, "orientation", "VERTICAL")

        lineWriter.agregarLinea("###")
        lineWriter.agregarLinea("<section=$width,$height,$pointX,$pointY,$orientacion>")
        lineWriter.aumentarIndentacion()
        escribirBloqueStylesSiExiste(node.atributos)
        lineWriter.agregarLinea("<content>")
        lineWriter.aumentarIndentacion()

        for (interno in node.elementosInternos) {
            renderInstruccion(interno)
        }

        lineWriter.disminuirIndentacion()
        lineWriter.agregarLinea("</content>")
        lineWriter.disminuirIndentacion()
        lineWriter.agregarLinea("</section>")
        lineWriter.agregarLinea("###")
    }

    fun escribirTabla(node: ComponenteTabla, renderComponente: (ComponenteUI) -> Unit) {
        lineWriter.agregarLinea("<table>")
        lineWriter.aumentarIndentacion()
        escribirBloqueStylesSiExiste(node.atributos)
        lineWriter.agregarLinea("<content>")
        lineWriter.aumentarIndentacion()

        for (fila in node.filas) {
            lineWriter.agregarLinea("<line>")
            lineWriter.aumentarIndentacion()

            for (celda in fila) {
                lineWriter.agregarLinea("<element>")
                lineWriter.aumentarIndentacion()

                if (celda is NodoLiteral && celda.tipo == "table_cell_component" && celda.valor is ComponenteUI) {
                    val componente = celda.valor as ComponenteUI
                    renderComponente(componente)
                } else {
                    lineWriter.agregarLinea(expressionWriter.expresionComoTexto(celda))
                }

                lineWriter.disminuirIndentacion()
                lineWriter.agregarLinea("</element>")
            }

            lineWriter.disminuirIndentacion()
            lineWriter.agregarLinea("</line>")
        }

        lineWriter.disminuirIndentacion()
        lineWriter.agregarLinea("</content>")
        lineWriter.disminuirIndentacion()
        lineWriter.agregarLinea("</table>")
    }

    fun escribirTexto(node: ComponenteTexto) {
        val width = valorTexto(node.atributos, "width", "50")
        val height = valorTexto(node.atributos, "height", "10")
        val content = valorTexto(node.atributos, "content", "\"\"")

        if (NodoAtributo.contiene(node.atributos, "styles")) {
            lineWriter.agregarLinea("<open=$width,$height,$content>")
            lineWriter.aumentarIndentacion()
            escribirBloqueStylesSiExiste(node.atributos)
            lineWriter.disminuirIndentacion()
            lineWriter.agregarLinea("</open>")
        } else {
            lineWriter.agregarLinea("<open=$width,$height,$content/>")
        }
    }

    fun escribirPreguntaDesplegable(node: PreguntaDesplegable) {
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
            lineWriter.agregarLinea("<drop=$width,$height,$label,$options,$correct>")
            lineWriter.aumentarIndentacion()
            escribirBloqueStylesSiExiste(node.atributos)
            lineWriter.disminuirIndentacion()
            lineWriter.agregarLinea("</drop>")
        } else {
            lineWriter.agregarLinea("<drop=$width,$height,$label,$options,$correct/>")
        }
    }

    fun escribirPreguntaSeleccion(node: PreguntaSeleccionUnica) {
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
            lineWriter.agregarLinea("<select=$width,$height,$label,$options,$correct>")
            lineWriter.aumentarIndentacion()
            escribirBloqueStylesSiExiste(node.atributos)
            lineWriter.disminuirIndentacion()
            lineWriter.agregarLinea("</select>")
        } else {
            lineWriter.agregarLinea("<select=$width,$height,$label,$options,$correct/>")
        }
    }

    fun escribirPreguntaMultiple(node: PreguntaSeleccionadaMultiple) {
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
            lineWriter.agregarLinea("<multiple=$width,$height,$label,$options,$correct>")
            lineWriter.aumentarIndentacion()
            escribirBloqueStylesSiExiste(node.atributos)
            lineWriter.disminuirIndentacion()
            lineWriter.agregarLinea("</multiple>")
        } else {
            lineWriter.agregarLinea("<multiple=$width,$height,$label,$options,$correct/>")
        }
    }

    fun escribirPreguntaAbierta(node: PreguntaAbierta) {
        stats.registrarPreguntaAbierta()
        val width = valorTexto(node.atributos, "width", "50")
        val height = valorTexto(node.atributos, "height", "10")
        val label = valorTexto(node.atributos, "label", "\"\"")

        if (NodoAtributo.contiene(node.atributos, "styles")) {
            lineWriter.agregarLinea("<open=$width,$height,$label>")
            lineWriter.aumentarIndentacion()
            escribirBloqueStylesSiExiste(node.atributos)
            lineWriter.disminuirIndentacion()
            lineWriter.agregarLinea("</open>")
        } else {
            lineWriter.agregarLinea("<open=$width,$height,$label/>")
        }
    }

    private fun escribirBloqueStylesSiExiste(attrs: List<NodoAtributo>) {
        if (!NodoAtributo.contiene(attrs, "styles")) {
            return
        }
        lineWriter.agregarLinea("<style> ... </style>")
    }

    private fun valorTexto(attrs: List<NodoAtributo>, nombre: String, defecto: String): String {
        val valor = NodoAtributo.valor(attrs, nombre)
        if (valor == null) {
            return defecto
        }
        return expressionWriter.valorComoTexto(valor)
    }
}
