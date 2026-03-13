package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import androidx.compose.ui.graphics.Color
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteSeccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteTabla
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteTexto
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.NodoAtributo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaAbierta
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaDesplegable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaSeleccionUnica
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaSeleccionadaMultiple
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion

// Aliases para evitar conflicto de nombres entre nodos y modelos
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaAbierta as ModeloPreguntaAbierta
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaDesplegable as ModeloPreguntaDesplegable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaSeleccionUnica as ModeloPreguntaSeleccionUnica
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaSeleccionMultiple as ModeloPreguntaSeleccionMultiple

class UiNodeBuilder(
    private val evaluarExpresion: (NodoExpresion) -> Any?
) {

    fun construirSeccion(node: ComponenteSeccion, internos: List<ElementoFormulario>): SeccionFormulario {
        val attrs = node.atributos
        val width = toFloat(evaluarAtributo(attrs, "width"))
        val height = toFloat(evaluarAtributo(attrs, "height"))
        val pointX = toFloat(evaluarAtributo(attrs, "pointX")) ?: 0f
        val pointY = toFloat(evaluarAtributo(attrs, "pointY")) ?: 0f
        val orientacion = when ((evaluarAtributo(attrs, "orientation") as? String)?.uppercase()) {
            "HORIZONTAL" -> Orientacion.HORIZONTAL
            else -> Orientacion.VERTICAL
        }
        val estilos = parsearEstilos(attrs)

        return SeccionFormulario(
            width = width,
            height = height,
            pointX = pointX,
            pointY = pointY,
            orientacion = orientacion,
            elementos = internos,
            estilos = estilos
        )
    }

    fun construirTexto(node: ComponenteTexto): TextoFormulario {
        val attrs = node.atributos
        val contenido = evaluarAtributo(attrs, "content")?.toString() ?: ""
        val estilos = parsearEstilos(attrs)
        return TextoFormulario(contenido = contenido, estilos = estilos)
    }

    fun construirPreguntaAbierta(node: PreguntaAbierta): ModeloPreguntaAbierta {
        val attrs = node.atributos
        val label = evaluarAtributo(attrs, "label")?.toString() ?: ""
        val estilos = parsearEstilos(attrs)
        return ModeloPreguntaAbierta(label = label, estilos = estilos)
    }

    fun construirPreguntaDesplegable(node: PreguntaDesplegable): ModeloPreguntaDesplegable {
        val attrs = node.atributos
        val label = evaluarAtributo(attrs, "label")?.toString() ?: ""
        val opciones = evaluarOpciones(attrs, "options")
        val correcta = toInt(evaluarAtributo(attrs, "correct"))
        val estilos = parsearEstilos(attrs)
        return ModeloPreguntaDesplegable(
            label = label,
            opciones = opciones,
            correcta = correcta,
            estilos = estilos
        )
    }

    fun construirPreguntaSeleccionUnica(node: PreguntaSeleccionUnica): ModeloPreguntaSeleccionUnica {
        val attrs = node.atributos
        val label = evaluarAtributo(attrs, "label")?.toString() ?: ""
        val opciones = evaluarOpciones(attrs, "options")
        val correcta = toInt(evaluarAtributo(attrs, "correct"))
        val estilos = parsearEstilos(attrs)
        return ModeloPreguntaSeleccionUnica(
            label = label,
            opciones = opciones,
            correcta = correcta,
            estilos = estilos
        )
    }

    fun construirPreguntaSeleccionMultiple(node: PreguntaSeleccionadaMultiple): ModeloPreguntaSeleccionMultiple {
        val attrs = node.atributos
        val label = evaluarAtributo(attrs, "label")?.toString() ?: ""
        val opciones = evaluarOpciones(attrs, "options")
        val correctas = evaluarCorrectas(attrs)
        val estilos = parsearEstilos(attrs)
        return ModeloPreguntaSeleccionMultiple(
            label = label,
            opciones = opciones,
            correctas = correctas,
            estilos = estilos
        )
    }

    fun construirTabla(node: ComponenteTabla): TablaFormulario {
        val attrs = node.atributos
        val width = toFloat(evaluarAtributo(attrs, "width"))
        val height = toFloat(evaluarAtributo(attrs, "height"))
        val pointX = toFloat(evaluarAtributo(attrs, "pointX")) ?: 0f
        val pointY = toFloat(evaluarAtributo(attrs, "pointY")) ?: 0f
        val estilos = parsearEstilos(attrs)

        val filas = mutableListOf<List<ElementoFormulario>>()
        for (fila in node.filas) {
            val celdas = mutableListOf<ElementoFormulario>()
            for (celdaExpr in fila) {
                val texto = evaluarExpresion(celdaExpr)?.toString() ?: ""
                celdas.add(TextoFormulario(contenido = texto))
            }
            filas.add(celdas)
        }

        return TablaFormulario(
            width = width,
            height = height,
            pointX = pointX,
            pointY = pointY,
            filas = filas,
            estilos = estilos
        )
    }

    private fun evaluarAtributo(attrs: List<NodoAtributo>, nombre: String): Any? {
        val valor = NodoAtributo.valor(attrs, nombre) ?: return null
        return if (valor is NodoExpresion) evaluarExpresion(valor) else valor
    }

    private fun evaluarOpciones(attrs: List<NodoAtributo>, nombre: String): List<String> {
        val valor = NodoAtributo.valor(attrs, nombre) ?: return emptyList()

        if (valor is NodoExpresion) {
            return listOf(evaluarExpresion(valor)?.toString() ?: "")
        }

        if (valor is List<*>) {
            val resultado = mutableListOf<String>()
            for (item in valor) {
                if (item is NodoExpresion) {
                    resultado.add(evaluarExpresion(item)?.toString() ?: "")
                }
            }
            return resultado
        }

        return emptyList()
    }

    private fun evaluarCorrectas(attrs: List<NodoAtributo>): List<Int> {
        val valor = NodoAtributo.valor(attrs, "correct") ?: return emptyList()
        if (valor !is List<*>) return emptyList()

        val correctas = mutableListOf<Int>()
        for (item in valor) {
            if (item is NodoExpresion) {
                correctas.add(toInt(evaluarExpresion(item)) ?: 0)
            }
        }
        return correctas
    }

    private fun parsearEstilos(attrs: List<NodoAtributo>): EstiloElemento {
        val stylesVal = NodoAtributo.valor(attrs, "styles") ?: return EstiloElemento()
        if (stylesVal !is List<*>) return EstiloElemento()

        val stylesAttrs = mutableListOf<NodoAtributo>()
        for (item in stylesVal) {
            if (item is NodoAtributo) {
                stylesAttrs.add(item)
            }
        }

        val color = parsearColor(NodoAtributo.valor(stylesAttrs, "color"))
        val background = parsearColor(NodoAtributo.valor(stylesAttrs, "background"))
        val fontFamily = evaluarAtributo(stylesAttrs, "fontFamily")?.toString()?.uppercase() ?: "SANS_SERIF"
        val textSize = toFloat(evaluarAtributo(stylesAttrs, "textSize")) ?: 14f

        val borderVal = NodoAtributo.valor(stylesAttrs, "border")
        val border = if (borderVal is List<*>) {
            val borderAttrs = mutableListOf<NodoAtributo>()
            for (item in borderVal) {
                if (item is NodoAtributo) {
                    borderAttrs.add(item)
                }
            }

            BorderEstilo(
                grosor = toFloat(evaluarAtributo(borderAttrs, "grosor")) ?: 1f,
                tipo = evaluarAtributo(borderAttrs, "tipo")?.toString()?.uppercase() ?: "LINE",
                color = parsearColor(NodoAtributo.valor(borderAttrs, "color"))
            )
        } else {
            null
        }

        return EstiloElemento(
            color = color,
            backgroundColor = background,
            fontFamily = fontFamily,
            textSize = textSize,
            border = border
        )
    }

    private fun parsearColor(valor: Any?): Color {
        val str = when (valor) {
            is NodoExpresion -> evaluarExpresion(valor)?.toString()
            else -> valor?.toString()
        } ?: return Color.Black

        return try {
            val hex = str.trim().trimStart('#')
            when (hex.length) {
                6 -> Color((0xFF000000L or hex.toLong(16)).toInt())
                8 -> Color(hex.toLong(16).toInt())
                else -> Color.Black
            }
        } catch (_: Exception) {
            when (str.trim().lowercase()) {
                "red" -> Color.Red
                "blue" -> Color.Blue
                "green" -> Color.Green
                "white" -> Color.White
                "black" -> Color.Black
                "yellow" -> Color.Yellow
                "cyan" -> Color.Cyan
                "magenta" -> Color.Magenta
                "gray", "grey" -> Color.Gray
                "transparent" -> Color.Transparent
                else -> Color.Black
            }
        }
    }

    private fun toDouble(v: Any?): Double? {
        return when (v) {
            is Double -> v
            is Int -> v.toDouble()
            is Float -> v.toDouble()
            is Long -> v.toDouble()
            is Number -> v.toDouble()
            is String -> v.toDoubleOrNull()
            else -> null
        }
    }

    private fun toFloat(v: Any?): Float? = toDouble(v)?.toFloat()

    private fun toInt(v: Any?): Int? = toDouble(v)?.toInt()
}
