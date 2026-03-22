package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models

/**
 * Modelo de color agnóstico a la UI.
 * Representa un color en formato RGB/Hex sin dependencia de framework.
 */
data class Color(
    val rojo: Int = 0,
    val verde: Int = 0,
    val azul: Int = 0,
    val alfa: Int = 255
) {
    companion object {
        private val COLORES_PREDEFINIDOS = mapOf(
            "BLACK" to Color(0, 0, 0),
            "WHITE" to Color(255, 255, 255),
            "RED" to Color(255, 0, 0),
            "GREEN" to Color(0, 255, 0),
            "BLUE" to Color(0, 0, 255),
            "YELLOW" to Color(255, 255, 0),
            "CYAN" to Color(0, 255, 255),
            "MAGENTA" to Color(255, 0, 255),
            "GRAY" to Color(128, 128, 128),
            "LIGHT_GRAY" to Color(192, 192, 192),
            "DARK_GRAY" to Color(64, 64, 64),
            "ORANGE" to Color(255, 165, 0),
            "PINK" to Color(255, 192, 203),
            "PURPLE" to Color(128, 0, 128),
            "BROWN" to Color(165, 42, 42),
            "SKY" to Color(135, 206, 235)
        )

        /** Color predeterminado: negro */
        fun defecto(): Color = Color(0, 0, 0, 255)

        /** Interpreta un nombre de color (ej. BLACK) */
        fun desdeNombre(nombre: String): Color? {
            return COLORES_PREDEFINIDOS[nombre.uppercase()]
        }

        /** Interpreta una cadena hex en Color. Soporta #RRGGBB o #RRGGBBAA */
        fun desdeHex(hexString: String): Color? {
            return try {
                val hex = hexString.removePrefix("#")
                when (hex.length) {
                    3 -> { // Soporte corto #RGB
                        val r = hex.substring(0, 1).repeat(2).toInt(16)
                        val g = hex.substring(1, 2).repeat(2).toInt(16)
                        val b = hex.substring(2, 3).repeat(2).toInt(16)
                        Color(r, g, b, 255)
                    }
                    6 -> {
                        val r = hex.substring(0, 2).toInt(16)
                        val g = hex.substring(2, 4).toInt(16)
                        val b = hex.substring(4, 6).toInt(16)
                        Color(r, g, b, 255)
                    }
                    8 -> {
                        val r = hex.substring(0, 2).toInt(16)
                        val g = hex.substring(2, 4).toInt(16)
                        val b = hex.substring(4, 6).toInt(16)
                        val a = hex.substring(6, 8).toInt(16)
                        Color(r, g, b, a)
                    }
                    else -> null
                }
            } catch (e: Exception) {
                null
            }
        }

        /** Interpreta una cadena en formato rgb(r,g,b) o rgba(r,g,b,a) o (r,g,b) */
        fun desdeRgb(rgbString: String): Color? {
            return try {
                // Soporte para (r,g,b) sin el prefijo "rgb"
                val input = if (rgbString.startsWith("(") && !rgbString.contains("rgb")) {
                    "rgb$rgbString"
                } else {
                    rgbString
                }

                val match = if (input.startsWith("rgba")) {
                    Regex("""rgba\s*\(\s*([\d.]+)\s*,\s*([\d.]+)\s*,\s*([\d.]+)\s*,\s*([\d.]+)\s*\)""").find(input)
                } else {
                    Regex("""rgb\s*\(\s*([\d.]+)\s*,\s*([\d.]+)\s*,\s*([\d.]+)\s*\)""").find(input)
                }

                if (match != null) {
                    val groups = match.groupValues
                    val r = groups[1].toFloat().toInt().coerceIn(0, 255)
                    val g = groups[2].toFloat().toInt().coerceIn(0, 255)
                    val b = groups[3].toFloat().toInt().coerceIn(0, 255)
                    val a = if (input.startsWith("rgba")) {
                        (groups[4].toFloat() * 255).toInt().coerceIn(0, 255)
                    } else {
                        255
                    }
                    Color(r, g, b, a)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }

        /** Interpreta una cadena en formato hsl(h,s,l) o <h,s,l> */
        fun desdeHsl(hslString: String): Color? {
            return try {
                val normalizado = hslString.trim().let {
                    if (it.startsWith("<") && it.endsWith(">")) {
                        "hsl(" + it.substring(1, it.length - 1) + ")"
                    } else {
                        it
                    }
                }

                val match = Regex(
                    """hsl\s*\(\s*([\d.]+)\s*,\s*([\d.]+)\s*,\s*([\d.]+)\s*\)"""
                ).find(normalizado) ?: return null

                val h = match.groupValues[1].toFloat()
                val sPct = match.groupValues[2].toFloat()
                val lPct = match.groupValues[3].toFloat()

                val hue = ((h % 360f) + 360f) % 360f
                val s = (sPct / 100f).coerceIn(0f, 1f)
                val l = (lPct / 100f).coerceIn(0f, 1f)

                val c = (1f - kotlin.math.abs(2f * l - 1f)) * s
                val x = c * (1f - kotlin.math.abs((hue / 60f) % 2f - 1f))
                val m = l - c / 2f

                val (r1, g1, b1) = when {
                    hue < 60f -> Triple(c, x, 0f)
                    hue < 120f -> Triple(x, c, 0f)
                    hue < 180f -> Triple(0f, c, x)
                    hue < 240f -> Triple(0f, x, c)
                    hue < 300f -> Triple(x, 0f, c)
                    else -> Triple(c, 0f, x)
                }

                val r = ((r1 + m) * 255f).toInt().coerceIn(0, 255)
                val g = ((g1 + m) * 255f).toInt().coerceIn(0, 255)
                val b = ((b1 + m) * 255f).toInt().coerceIn(0, 255)

                Color(r, g, b, 255)
            } catch (e: Exception) {
                null
            }
        }
    }

    /** Convierte a representación hex #RRGGBB */
    fun aHex(): String {
        val r = rojo.toString(16).padStart(2, '0')
        val g = verde.toString(16).padStart(2, '0')
        val b = azul.toString(16).padStart(2, '0')
        return "#$r$g$b"
    }

    /** Convierte a representación rgb(r,g,b) */
    fun aRgb(): String {
        return "rgb($rojo, $verde, $azul)"
    }

    /** Convierte a representación rgba(r,g,b,a) */
    fun aRgba(): String {
        val alfaNormalizado = (alfa / 255.0f)
        return "rgba($rojo, $verde, $azul, $alfaNormalizado)"
    }
}
