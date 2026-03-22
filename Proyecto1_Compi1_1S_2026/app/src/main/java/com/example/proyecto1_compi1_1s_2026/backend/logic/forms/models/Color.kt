package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models

/**
 * Modelo de color agnóstico a la UI.
 * Representa un color en formato RGB/Hex sin dependencia de framework.
 *
 * GRASP Experto: El modelo Color es experto en representarse a sí mismo
 * de manera independiente del framework empleado en la presentación.
 */
data class Color(
    val rojo: Int = 0,
    val verde: Int = 0,
    val azul: Int = 0,
    val alfa: Int = 255
) {
    companion object {
        /** Color predeterminado: negro */
        fun defecto(): Color = Color(0, 0, 0, 255)

        /** Interpreta una cadena hex en Color. Soporta #RRGGBB o #RRGGBBAA */
        fun desdeHex(hexString: String): Color? {
            return try {
                val hex = hexString.removePrefix("#")
                when (hex.length) {
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

        /** Interpreta una cadena en formato rgb(r,g,b) o rgba(r,g,b,a) */
        fun desdeRgb(rgbString: String): Color? {
            return try {
                val match = if (rgbString.startsWith("rgba")) {
                    Regex("""rgba\s*\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*,\s*([\d.]+)\s*\)""").find(rgbString)
                } else {
                    Regex("""rgb\s*\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)""").find(rgbString)
                }

                if (match != null) {
                    val groups = match.groupValues
                    val r = groups.getOrNull(1)?.toIntOrNull() ?: 0
                    val g = groups.getOrNull(2)?.toIntOrNull() ?: 0
                    val b = groups.getOrNull(3)?.toIntOrNull() ?: 0
                    val a = if (rgbString.startsWith("rgba")) {
                        (groups.getOrNull(4)?.toFloatOrNull() ?: 1.0f * 255).toInt()
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

                val h = match.groupValues[1].toFloatOrNull() ?: return null
                val sPct = match.groupValues[2].toFloatOrNull() ?: return null
                val lPct = match.groupValues[3].toFloatOrNull() ?: return null

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
