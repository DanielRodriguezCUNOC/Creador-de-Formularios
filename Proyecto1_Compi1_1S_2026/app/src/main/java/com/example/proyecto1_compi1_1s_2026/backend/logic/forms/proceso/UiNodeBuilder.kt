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
                } else if (item is String) {
                    // Caso 3: soporte sencillo de comando textual:
                    // "who_is_that_pokemon(NUMBER, 1, 10)" o "who_is_that_pokemon(1, 10)"
                    val expandido = expandirComandoPokemon(item)
                    if (expandido.isNotEmpty()) {
                        for (nombrePokemon in expandido) {
                            resultado.add(nombrePokemon)
                        }
                    } else {
                        resultado.add(item)
                    }
                } else if (item != null) {
                    resultado.add(item.toString())
                }
            }
            return resultado
        }

        return emptyList()
    }

    /**
     * Expande un comando textual de pokémon a una lista de nombres.
     * Si no coincide con el formato esperado, retorna lista vacía.
     */
    private fun expandirComandoPokemon(texto: String): List<String> {
        val limpio = texto.trim()

        // Formato con prefijo NUMBER:
        // who_is_that_pokemon(NUMBER, 1, 10)
        val r1 = Regex("""^who_is_that_pokemon\(\s*NUMBER\s*,\s*(\d+)\s*,\s*(\d+)\s*\)$""", RegexOption.IGNORE_CASE)
        val m1 = r1.find(limpio)
        if (m1 != null) {
            val inicio = m1.groupValues[1].toIntOrNull() ?: return emptyList()
            val fin = m1.groupValues[2].toIntOrNull() ?: return emptyList()
            return PokemonApiService.obtenerNombresEnRango(inicio, fin)
        }

        // Formato corto:
        // who_is_that_pokemon(1, 10)
        val r2 = Regex("""^who_is_that_pokemon\(\s*(\d+)\s*,\s*(\d+)\s*\)$""", RegexOption.IGNORE_CASE)
        val m2 = r2.find(limpio)
        if (m2 != null) {
            val inicio = m2.groupValues[1].toIntOrNull() ?: return emptyList()
            val fin = m2.groupValues[2].toIntOrNull() ?: return emptyList()
            return PokemonApiService.obtenerNombresEnRango(inicio, fin)
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
        val str = when (valor) {
            is NodoExpresion -> evaluarExpresion(valor)?.toString()
            else -> valor?.toString()
        } ?: return Color.defecto()

        val s = str.trim()

        // --- RGB: (r, g, b) ---
        if (s.startsWith("(") && s.endsWith(")")) {
            return try {
                val partes = s.substring(1, s.length - 1).split(",")
                val r = partes[0].trim().toInt().coerceIn(0, 255)
                val g = partes[1].trim().toInt().coerceIn(0, 255)
                val b = partes[2].trim().toInt().coerceIn(0, 255)
                Color(r, g, b)
            } catch (_: Exception) { Color.defecto() }
        }

        // --- HSL: <h, s, l> ---
        if (s.startsWith("<") && s.endsWith(">")) {
            return try {
                val partes = s.substring(1, s.length - 1).split(",")
                val h = partes[0].trim().toInt()
                val sl = partes[1].trim().toInt()
                val l = partes[2].trim().toInt()
                hslToRgb(h, sl, l)
            } catch (_: Exception) { Color.defecto() }
        }

        // --- HEX: #RRGGBB o #RGB ---
        return try {
            val hex = s.trimStart('#')
            when (hex.length) {
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
                else -> Color.defecto()
            }
        } catch (_: Exception) {
            when (s.lowercase()) {
                "red" -> Color(255, 0, 0)
                "blue" -> Color(0, 0, 255)
                "green" -> Color(0, 128, 0)
                "white" -> Color(255, 255, 255)
                "black" -> Color(0, 0, 0)
                "yellow" -> Color(255, 255, 0)
                "cyan" -> Color(0, 255, 255)
                "magenta" -> Color(255, 0, 255)
                "gray", "grey" -> Color(128, 128, 128)
                "transparent" -> Color(0, 0, 0, 0)
                else -> Color.defecto()
            }
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
