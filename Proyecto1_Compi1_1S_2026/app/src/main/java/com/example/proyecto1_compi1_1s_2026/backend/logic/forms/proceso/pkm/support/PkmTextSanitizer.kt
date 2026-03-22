package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import java.nio.charset.Charset

// Encapsula normalización y escapado de texto para guardado .pkm.
class PkmTextSanitizer {

    private val charsetGbk: Charset = Charset.forName("GB18030")

    fun escaparCadena(texto: String): String {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"")
    }

    fun normalizarEmojisParaGuardado(texto: String): String {
        var out = texto
        out = out.replace("😄", "@[:smile:]")
        out = out.replace("😢", "@[:sad:]")
        out = out.replace("😐", "@[:serious:]")
        out = out.replace("❤️", "@[:heart:]")
        out = out.replace("😺", "@[:cat:]")
        return out
    }

    fun cadenaEntreComillas(texto: String): String {
        val corregido = repararMojibakeSiAplica(texto)
        val normalizado = normalizarEmojisParaGuardado(corregido)
        val escapado = escaparCadena(normalizado)
        return "\"$escapado\""
    }

    // Repara textos del tipo "驴Qu茅" -> "¿Qué" cuando llegan con mojibake GBK.
    private fun repararMojibakeSiAplica(texto: String): String {
        if (!pareceMojibakeGbk(texto)) return texto

        return try {
            val reparado = String(texto.toByteArray(charsetGbk), Charsets.UTF_8)
            if (reparado.contains('�')) texto else reparado
        } catch (_: Exception) {
            texto
        }
    }

    private fun pareceMojibakeGbk(texto: String): Boolean {
        if (texto.isBlank()) return false

        // Marcadores frecuentes del problema observado en salida PKM.
        val marcadores = listOf('驴', '谩', '茅', '贸', '铆', '煤')
        return marcadores.any { texto.contains(it) }
    }
}
