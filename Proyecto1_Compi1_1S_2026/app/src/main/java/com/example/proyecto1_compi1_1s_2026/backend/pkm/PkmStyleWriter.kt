package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.NodoAtributo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion

// Renderiza el bloque <style>...</style> en formato PKM.
class PkmStyleWriter(
    private val expressionWriter: PkmExpressionWriter,
    private val stats: PkmStatsCollector,
    private val reportarErrorSemantico: (String, Int, Int) -> Unit
) {

    fun crearBloqueStylesSiExiste(
        attrs: List<NodoAtributo>,
        contexto: String = "componente",
        linea: Int = 0,
        columna: Int = 0
    ): PkmTagNode? {
        val stylesRaw = NodoAtributo.valor(attrs, "styles") ?: return null
        stats.registrarEstilo()

        // serializar styles en formato de etiquetas PKM.
        val lineasStyle = crearLineasStyle(stylesRaw, contexto, linea, columna)
        val hijos = lineasStyle.mapTo(mutableListOf<PkmTagNode>()) { linea -> PkmTextNode(linea) }

        return PkmElementNode(
            PkmSerializationContract.tagStyleOpen(),
            PkmSerializationContract.tagStyleClose(),
            hijos
        )
    }

    private fun crearLineasStyle(stylesRaw: Any, contexto: String, linea: Int, columna: Int): List<String> {
        if (stylesRaw !is List<*>) {
            
            return listOf(expressionWriter.valorComoTexto(stylesRaw))
        }

        val styles = stylesRaw.filterIsInstance<NodoAtributo>()
        val lineas = mutableListOf<String>()

        for (style in styles) {
            when (style.nombre) {
                "color" -> {
                    val value = valorEstilo(style.valor)
                    if (value.isNotBlank()) {
                        lineas.add("<color=$value/>")
                    } else {
                        reportarErrorSemantico(
                            "En $contexto, el atributo de style 'color' requiere contenido y no puede ir vacio",
                            linea,
                            columna
                        )
                    }
                }
                "backgroundColor" -> {
                    val value = valorEstilo(style.valor)
                    if (value.isNotBlank()) {
                        lineas.add("<background color=$value/>")
                    } else {
                        reportarErrorSemantico(
                            "En $contexto, el atributo de style 'background color' requiere contenido y no puede ir vacio",
                            linea,
                            columna
                        )
                    }
                }
                "fontFamily" -> {
                    val value = valorEstilo(style.valor)
                    if (value.isNotBlank()) {
                        lineas.add("<font family=$value/>")
                    } else {
                        reportarErrorSemantico(
                            "En $contexto, el atributo de style 'font family' requiere contenido y no puede ir vacio",
                            linea,
                            columna
                        )
                    }
                }
                "textSize" -> {
                    val value = valorEstilo(style.valor)
                    if (value.isNotBlank()) {
                        lineas.add("<text size=$value/>")
                    } else {
                        reportarErrorSemantico(
                            "En $contexto, el atributo de style 'text size' requiere contenido y no puede ir vacio",
                            linea,
                            columna
                        )
                    }
                }
                "border" -> {
                    val borde = crearLineaBorde(style.valor, contexto, linea, columna)
                    if (borde != null) lineas.add(borde)
                }
            }
        }

        return lineas
    }

    private fun crearLineaBorde(borderRaw: Any, contexto: String, linea: Int, columna: Int): String? {
        if (borderRaw !is List<*>) return null

        val borderAttrs = borderRaw.filterIsInstance<NodoAtributo>()
        val grosor = NodoAtributo.valor(borderAttrs, "grosor") ?: return null
        val tipo = NodoAtributo.valor(borderAttrs, "tipo") ?: return null
        val color = NodoAtributo.valor(borderAttrs, "color") ?: return null

        val grosorTxt = valorEstilo(grosor)
        val tipoTxt = valorEstilo(tipo)
        val colorTxt = valorEstilo(color)
        if (grosorTxt.isBlank() || tipoTxt.isBlank() || colorTxt.isBlank()) {
            reportarErrorSemantico(
                "En $contexto, el atributo de style 'border' requiere grosor, tipo y color con contenido",
                linea,
                columna
            )
            return null
        }

        return "<border,$grosorTxt,$tipoTxt,color=$colorTxt/>"
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
