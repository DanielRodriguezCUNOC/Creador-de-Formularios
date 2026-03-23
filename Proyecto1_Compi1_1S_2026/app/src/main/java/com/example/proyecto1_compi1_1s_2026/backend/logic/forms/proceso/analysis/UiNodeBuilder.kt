package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Color
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

        // Nombre de color (BLACK, PURPLE, SKY, etc.)
        Color.desdeNombre(s)?.let { return it }

        // Hexadecimal (#RGB, #RRGGBB, #RRGGBBAA)
        Color.desdeHex(s)?.let { return it }

        // RGB/RGBA y variante corta (r,g,b)
        val rgbNormalizado = if (s.startsWith("(") && s.endsWith(")")) "rgb$s" else s
        Color.desdeRgb(rgbNormalizado)?.let { return it }

        // HSL (hsl(h,s,l) o <h,s,l>)
        Color.desdeHsl(s)?.let { return it }

        return Color.defecto()
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
