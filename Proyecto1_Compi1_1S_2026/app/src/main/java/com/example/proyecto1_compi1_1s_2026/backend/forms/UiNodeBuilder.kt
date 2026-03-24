package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Color
import kotlin.math.abs
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteSeccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteTabla
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteTexto
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.NodoAtributo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaAbierta
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaDesplegable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaSeleccionUnica
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaSeleccionadaMultiple
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoListaExpresiones

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

    fun construirTabla(
        node: ComponenteTabla,
        filasEvaluadas: List<List<ElementoFormulario>>? = null
    ): TablaFormulario {
        val attrs = node.atributos
        val width = toFloat(evaluarAtributo(attrs, "width"))
        val height = toFloat(evaluarAtributo(attrs, "height"))
        val pointX = toFloat(evaluarAtributo(attrs, "pointX")) ?: 0f
        val pointY = toFloat(evaluarAtributo(attrs, "pointY")) ?: 0f
        val estilos = parsearEstilos(attrs)

        // Si el intérprete ya construyó las filas, usarlas directamente
        val filas: List<List<ElementoFormulario>>
        if (filasEvaluadas != null) {
            filas = filasEvaluadas
        } else {
            // Fallback: convertir cada celda a texto
            val resultado = mutableListOf<List<ElementoFormulario>>()
            for (fila in node.filas) {
                val celdas = mutableListOf<ElementoFormulario>()
                for (celdaExpr in fila) {
                    val texto = evaluarExpresion(celdaExpr)?.toString() ?: ""
                    celdas.add(TextoFormulario(contenido = texto))
                }
                resultado.add(celdas)
            }
            filas = resultado
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
        val resultado = mutableListOf<String>()

        if (valor is NodoExpresion) {
            // Cuando options viene como expresión ej: llamada API
            val evaluado = evaluarExpresion(valor)

            if (evaluado is List<*>) {
                for (item in evaluado) {
                    if (item != null) {
                        resultado.add(item.toString())
                    }
                }
                return resultado
            }

            if (evaluado != null) {
                resultado.add(evaluado.toString())
            }
            return resultado
        }

        if (valor is List<*>) {
            // Cuando options viene como lista literal {"a", "b"}
            for (item in valor) {
                if (item is NodoExpresion) {
                    val evaluado = evaluarExpresion(item)
                    if (evaluado is List<*>) {
                        for (sub in evaluado) {
                            if (sub != null) {
                                resultado.add(sub.toString())
                            }
                        }
                    } else if (evaluado != null) {
                        resultado.add(evaluado.toString())
                    }
                } else if (item != null) {
                    resultado.add(item.toString())
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
        val background = parsearColor(NodoAtributo.valor(stylesAttrs, "backgroundColor"))
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
        // Soporte para listas directas evaluadas (RGB/HSL dinámico)
        if (valor is List<*>) {
            return try {
                val r = toInt(valor.getOrNull(0)) ?: 0
                val g = toInt(valor.getOrNull(1)) ?: 0
                val b = toInt(valor.getOrNull(2)) ?: 0
                Color(r, g, b)
            } catch (_: Exception) { Color.defecto() }
        }

        val evaluado = if (valor is NodoExpresion) evaluarExpresion(valor) else valor
        
        // Si el resultado de la evaluación es una lista, re-procesar
        if (evaluado is List<*>) return parsearColor(evaluado)
        
        val s = evaluado?.toString()?.trim() ?: return Color.defecto()

        // --- RGB: (r, g, b) ---
        if (s.startsWith("(") && s.endsWith(")")) {
            return try {
                val partes = s.substring(1, s.length - 1).split(",")
                val r = partes[0].trim().toDouble().toInt().coerceIn(0, 255)
                val g = partes[1].trim().toDouble().toInt().coerceIn(0, 255)
                val b = partes[2].trim().toDouble().toInt().coerceIn(0, 255)
                Color(r, g, b)
            } catch (_: Exception) { Color.defecto() }
        }

        // --- HSL: <h, s, l> ---
        if (s.startsWith("<") && s.endsWith(">")) {
            return try {
                val partes = s.substring(1, s.length - 1).split(",")
                val h = partes[0].trim().toDouble().toInt()
                val sl = partes[1].trim().toDouble().toInt()
                val l = partes[2].trim().toDouble().toInt()
                hslToRgb(h, sl, l)
            } catch (_: Exception) { Color.defecto() }
        }

        // --- HEX: #RRGGBB o #RGB ---
        return try {
            val hex = s.trimStart('#')
            when (hex.length) {
                3 -> {
                    val r = hex.substring(0, 1).repeat(2).toInt(16)
                    val g = hex.substring(1, 2).repeat(2).toInt(16)
                    val b = hex.substring(2, 3).repeat(2).toInt(16)
                    Color(r, g, b)
                }
                6 -> {
                    val argb = (0xFF000000L or hex.toLong(16)).toInt()
                    val r = (argb shr 16) and 0xFF
                    val g = (argb shr 8) and 0xFF
                    val b = argb and 0xFF
                    Color(r, g, b)
                }
                8 -> {
                    val argb = hex.toLong(16).toInt()
                    val a = (argb shr 24) and 0xFF
                    val r = (argb shr 16) and 0xFF
                    val g = (argb shr 8) and 0xFF
                    val b = argb and 0xFF
                    Color(r, g, b, a)
                }
                else -> Color.desdeNombre(s) ?: Color.defecto()
            }
        } catch (_: Exception) {
            Color.desdeNombre(s) ?: Color.defecto()
        }
    }

    private fun hslToRgb(h: Int, s: Int, l: Int): Color {
        val hf = h.toFloat()
        val sf = s.toFloat() / 100f
        val lf = l.toFloat() / 100f
        val c = (1f - abs(2f * lf - 1f)) * sf
        val x = c * (1f - abs((hf / 60f) % 2f - 1f))
        val m = lf - c / 2f
        var r1: Float
        var g1: Float
        var b1: Float
        when {
            hf < 60f -> { r1 = c; g1 = x; b1 = 0f }
            hf < 120f -> { r1 = x; g1 = c; b1 = 0f }
            hf < 180f -> { r1 = 0f; g1 = c; b1 = x }
            hf < 240f -> { r1 = 0f; g1 = x; b1 = c }
            hf < 300f -> { r1 = x; g1 = 0f; b1 = c }
            else -> { r1 = c; g1 = 0f; b1 = x }
        }
        val r = ((r1 + m) * 255f).toInt().coerceIn(0, 255)
        val g = ((g1 + m) * 255f).toInt().coerceIn(0, 255)
        val b = ((b1 + m) * 255f).toInt().coerceIn(0, 255)
        return Color(r, g, b)
    }

    private fun toDouble(v: Any?): Double? {
        return try {
            when (v) {
                is Double -> v
                is Int -> v.toDouble()
                is Float -> v.toDouble()
                is Long -> v.toDouble()
                is Number -> v.toDouble()
                is String -> v.toDoubleOrNull()
                else -> null
            }
        } catch (_: Exception) { null }
    }

    private fun toFloat(v: Any?): Float? = toDouble(v)?.toFloat()

    private fun toInt(v: Any?): Int? = toDouble(v)?.toInt()
}
