package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import java.nio.charset.Charset

// Encapsula normalización y escapado de texto para guardado .pkm.
class PkmTextSanitizer {

    private val charsetWin1252: Charset = Charset.forName("windows-1252")

    fun escaparCadena(texto: String): String {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"")
    }

    fun desescaparCadena(texto: String): String {
        return texto.replace("\\\"", "\"").replace("\\\\", "\\")
    }

    fun normalizarEmojisParaGuardado(texto: String): String {
        var out = texto
        out = out.replace("😄", "@[:smile:]")
        out = out.replace("😢", "@[:sad:]")
        out = out.replace("😐", "@[:serious:]")
        out = out.replace("❤️", "@[:heart:]")
        out = out.replace("😺", "@[:cat:]")
        out = out.replace("⭐", "@[:star:]")
        return out
    }

    fun restaurarEmojisDesdePkm(texto: String): String {
        var out = texto
        out = out.replace("@[:smile:]", "😄")
        out = out.replace("@[:sad:]", "😢")
        out = out.replace("@[:serious:]", "😐")
        out = out.replace("@[:heart:]", "❤️")
        out = out.replace("@[:<3]", "❤️")
        out = out.replace("@[:cat:]", "😺")

        // Soporte para estrellas con multiplicador ej @[:star:3:] -> ⭐⭐⭐
        // Se escapan los corchetes y dos puntos correctamente para evitar errores Unicode.
        val regexStar = Regex("""@\[:star:(\d+):]""")
        out = regexStar.replace(out) { match ->
            val cant = match.groupValues[1].toIntOrNull() ?: 1
            "⭐".repeat(cant)
        }
        out = out.replace("@[:star:]", "⭐")

        return out
    }

    fun cadenaEntreComillas(texto: String): String {
        val corregido = normalizarMojibake(texto)
        val normalizado = normalizarEmojisParaGuardado(corregido)
        val escapado = escaparCadena(normalizado)
        return "\"$escapado\""
    }

    fun normalizarMojibake(texto: String): String {
        val reparado = repararMojibakeSiAplica(texto)
        return limpiarBasuraVisual(reparado)
    }

    // Repara mojibake de forma determinista, evitando heurísticas agresivas.
    private fun repararMojibakeSiAplica(texto: String): String {
        if (texto.isBlank()) return texto

        val base = reemplazarMojibakeConocido(texto)
        if (!contieneIndicadoresMojibake(base)) return base

        val candidatos = listOf(
            base,
            recodificar(base, Charsets.ISO_8859_1),
            recodificar(base, charsetWin1252)
        ).distinct()

        return candidatos.minByOrNull { contarIndicadoresMojibake(it) } ?: base
    }

    private fun contieneIndicadoresMojibake(texto: String): Boolean {
        return texto.contains("Ã")
            || texto.contains("Â")
            || texto.contains("â€")
            || texto.any { it in CARACTERES_MOJIBAKE_CJK }
    }

    private fun reemplazarMojibakeConocido(texto: String): String {
        return texto
            .replace('\u9A74', '¿')
            .replace('\u8C29', 'á')
            .replace('\u8305', 'é')
            .replace('\u8D38', 'í')
            .replace('\u94C6', 'ó')
            .replace('\u7164', 'ú')
    }

    private fun recodificar(texto: String, origen: Charset): String {
        return try {
            String(texto.toByteArray(origen), Charsets.UTF_8)
        } catch (_: Exception) {
            texto
        }
    }

    private fun contarIndicadoresMojibake(texto: String): Int {
        var total = 0
        total += texto.count { it == '\uFFFD' } * 100
        total += texto.windowed(1).count { it == "Ã" || it == "Â" } * 10
        if (texto.contains("â€")) total += 10
        total += texto.count { it in CARACTERES_MOJIBAKE_CJK } * 8

        if (Regex("[A-Za-zÁÉÍÓÚáéíóúÑñ¿¡]").containsMatchIn(texto)) {
            total += texto.count { it in '\u4E00'..'\u9FFF' } * 6
        }
        return total
    }

    // Si quedan caracteres CJK o de reemplazo, se eliminan para evitar basura visual.
    private fun limpiarBasuraVisual(texto: String): String {
        val limpio = buildString(texto.length) {
            for (ch in texto) {
                if (ch == '\uFFFD') continue
                if (ch in '\u4E00'..'\u9FFF') continue
                if (ch.isISOControl() && ch != '\n' && ch != '\r' && ch != '\t') continue
                append(ch)
            }
        }
        return limpio
    }

    companion object {
        private val CARACTERES_MOJIBAKE_CJK = setOf(
            '\u9A74', // 驴
            '\u8C29', // 谩
            '\u8305', // 茅
            '\u8D38', // 贸
            '\u94C6', // 铆
            '\u7164'  // 煤
        )
    }
}
