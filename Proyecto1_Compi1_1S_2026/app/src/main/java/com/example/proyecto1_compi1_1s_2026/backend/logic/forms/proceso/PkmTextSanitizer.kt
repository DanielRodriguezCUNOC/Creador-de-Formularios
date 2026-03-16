package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

// Encapsula normalización y escapado de texto para guardado .pkm.
class PkmTextSanitizer {

    fun escaparCadena(texto: String): String {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"")
    }

    fun normalizarEmojisParaGuardado(texto: String): String {
        var out = texto
        out = out.replace("😄", "@[:smile:]")
        out = out.replace("😢", "@[:sad:]")
        out = out.replace("😐", "@[:serious:]")
        out = out.replace("❤️", "@[:heart:]")
        return out
    }

    fun cadenaEntreComillas(texto: String): String {
        val normalizado = normalizarEmojisParaGuardado(texto)
        val escapado = escaparCadena(normalizado)
        return "\"$escapado\""
    }
}
