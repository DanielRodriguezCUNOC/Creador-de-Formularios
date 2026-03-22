package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.NodoAtributo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion

// Renderiza el bloque <style>...</style> en formato PKM.
class PkmStyleWriter(
    private val expressionWriter: PkmExpressionWriter,
    private val stats: PkmStatsCollector
) {

    fun crearBloqueStylesSiExiste(attrs: List<NodoAtributo>): PkmTagNode? {
        val stylesRaw = NodoAtributo.valor(attrs, "styles") ?: return null
        stats.registrarEstilo()

        // serializar styles en formato de etiquetas PKM.
        val lineasStyle = crearLineasStyle(stylesRaw)
        val hijos = lineasStyle.mapTo(mutableListOf<PkmTagNode>()) { linea -> PkmTextNode(linea) }

        return PkmElementNode(
            PkmSerializationContract.tagStyleOpen(),
            PkmSerializationContract.tagStyleClose(),
            hijos
        )
    }

    private fun crearLineasStyle(stylesRaw: Any): List<String> {
        if (stylesRaw !is List<*>) {
            // Compatibilidad minima: si no llega la lista esperada, dejamos el valor crudo.
            return listOf(expressionWriter.valorComoTexto(stylesRaw))
        }

        val styles = stylesRaw.filterIsInstance<NodoAtributo>()
        val lineas = mutableListOf<String>()

        for (style in styles) {
            when (style.nombre) {
                "color" -> lineas.add("<color=${valorEstilo(style.valor)}/>")
                "backgroundColor" -> lineas.add("<background color=${valorEstilo(style.valor)}/>")
                "fontFamily" -> lineas.add("<font family=${valorEstilo(style.valor)}/>")
                "textSize" -> lineas.add("<text size=${valorEstilo(style.valor)}/>")
                "border" -> {
                    val borde = crearLineaBorde(style.valor)
                    if (borde != null) lineas.add(borde)
                }
            }
        }

        return lineas
    }

    private fun crearLineaBorde(borderRaw: Any): String? {
        if (borderRaw !is List<*>) return null

        val borderAttrs = borderRaw.filterIsInstance<NodoAtributo>()
        val grosor = NodoAtributo.valor(borderAttrs, "grosor") ?: return null
        val tipo = NodoAtributo.valor(borderAttrs, "tipo") ?: return null
        val color = NodoAtributo.valor(borderAttrs, "color") ?: return null

        return "<border,${valorEstilo(grosor)},${valorEstilo(tipo)},color=${valorEstilo(color)}/>"
    }

    private fun valor(raw: Any): String {
        return if (raw is NodoExpresion) {
            expressionWriter.expresionComoTexto(raw)
        } else {
            expressionWriter.valorComoTexto(raw)
        }
    }

    private fun valorEstilo(raw: Any): String {
        val texto = valor(raw).trim()
        return if (texto.length >= 2 && texto.startsWith("\"") && texto.endsWith("\"")) {
            texto.substring(1, texto.length - 1)
        } else {
            texto
        }
    }
}
