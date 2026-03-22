package com.example.proyecto1_compi1_1s_2026.backend.generate.pkm

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.BorderEstilo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Color
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.ElementoFormulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.EstiloElemento
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TipoError

/**
 * Encapsula conversiones y validaciones semanticas usadas por el parser PKM.
 */
class PkmParserExpert(
    private val erroresSemanticos: MutableList<ErrorInfo>
) {

    fun estiloDefault(): EstiloElemento {
        val colorTexto = Color(0, 0, 0, 255)
        val colorFondo = Color(255, 255, 255, 0)
        return EstiloElemento(colorTexto, colorFondo, "SANS_SERIF", 14f, null)
    }

    fun toFloat(value: Any?): Float {
        if (value == null) return 0f
        return value.toString().toFloatOrNull() ?: run {
            erroresSemanticos.add(
                ErrorInfo(
                    TipoError.SEMANTICO,
                    "Valor numerico invalido (float): ${value}",
                    0,
                    0
                )
            )
            0f
        }
    }

    fun toInt(value: Any?): Int {
        if (value == null) return 0
        val raw = value.toString()
        raw.toIntOrNull()?.let { return it }

        // Soporta numeros como "15.2" para no romper el parseo.
        raw.toFloatOrNull()?.let { return it.toInt() }

        erroresSemanticos.add(
            ErrorInfo(
                TipoError.SEMANTICO,
                "Valor numerico invalido (int): ${value}",
                0,
                0
            )
        )
        return 0
    }

    fun normalizarIndice(indice: Int, opciones: List<*>?, campo: String): Int? {
        if (indice < 0) return null
        if (opciones == null || indice >= opciones.size) {
            erroresSemanticos.add(
                ErrorInfo(
                    TipoError.SEMANTICO,
                    "Indice fuera de rango en $campo: $indice",
                    0,
                    0
                )
            )
            return null
        }
        return indice
    }

    fun filtrarIndicesValidos(indices: List<*>?, opciones: List<*>?, campo: String): MutableList<Int> {
        val validos = mutableListOf<Int>()
        if (indices == null) return validos

        for (item in indices) {
            val idx = normalizarIndice(toInt(item), opciones, campo)
            if (idx != null) {
                validos.add(idx)
            }
        }
        return validos
    }

    fun listaVacia(): MutableList<Any?> = mutableListOf()

    fun listaConElementoSiNoNulo(elemento: ElementoFormulario?): MutableList<ElementoFormulario> {
        val lista = mutableListOf<ElementoFormulario>()
        if (elemento != null) {
            lista.add(elemento)
        }
        return lista
    }

    fun mapaVacio(): MutableMap<String, Any?> = mutableMapOf()

    fun mapaAtributo(key: String, value: Any?): MutableMap<String, Any?> {
        val mapa = mutableMapOf<String, Any?>()
        mapa[key] = value
        return mapa
    }

    fun mapaBorde(width: Any?, tipo: Any?, color: Any?): MutableMap<String, Any?> {
        val mapa = mutableMapOf<String, Any?>()
        mapa["border_width"] = width
        mapa["border_type"] = tipo
        mapa["border_color"] = color
        return mapa
    }

    fun buildStyleFromMap(data: Map<*, *>?): EstiloElemento {
        val base = estiloDefault()

        var colorTexto = base.color
        var colorFondo = base.backgroundColor
        var font = base.fontFamily
        var textSize = base.textSize
        var border = base.border

        if (data != null) {
            if (data.containsKey("color")) {
                colorTexto = parseColor(data["color"], colorTexto, "color")
            }
            if (data.containsKey("background_color")) {
                colorFondo = parseColor(data["background_color"], colorFondo, "background color")
            }
            if (data.containsKey("font_family")) {
                font = data["font_family"].toString()
            }
            if (data.containsKey("text_size")) {
                try {
                    textSize = data["text_size"].toString().toFloat()
                } catch (_: Exception) {
                    erroresSemanticos.add(
                        ErrorInfo(
                            TipoError.SEMANTICO,
                            "text size invalido: ${data["text_size"]}",
                            0,
                            0
                        )
                    )
                }
            }

            val hasBorder = data.containsKey("border_width")
                && data.containsKey("border_type")
                && data.containsKey("border_color")

            if (hasBorder) {
                val width = toFloat(data["border_width"])
                val type = data["border_type"].toString()
                val color = parseColor(data["border_color"], colorTexto, "border")
                border = BorderEstilo(width, type, color)
            }
        }

        return EstiloElemento(colorTexto, colorFondo, font, textSize, border)
    }

    private fun parseColor(value: Any?, fallback: Color, campo: String): Color {
        if (value == null) return fallback
        val raw = value.toString().trim()

        Color.desdeHex(raw)?.let { return it }

        var rgbNormalizado = raw
        if (raw.startsWith("(") && raw.endsWith(")")) {
            rgbNormalizado = "rgb$raw"
        }
        Color.desdeRgb(rgbNormalizado)?.let { return it }

        Color.desdeHsl(raw)?.let { return it }

        erroresSemanticos.add(
            ErrorInfo(
                TipoError.SEMANTICO,
                "Color invalido en $campo: $raw",
                0,
                0
            )
        )
        return fallback
    }
}
