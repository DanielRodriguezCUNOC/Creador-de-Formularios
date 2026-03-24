package com.example.proyecto1_compi1_1s_2026.backend.pkm

class PkmTextSanitizer {


    fun escaparCadena(texto: String): String {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"")
    }

    fun desescaparCadena(texto: String): String {
        return texto.replace("\\\"", "\"").replace("\\\\", "\\")
    }

    // Solo normaliza algunos emojis básicos para guardado
    fun normalizarEmojisParaGuardado(texto: String): String {
        var out = texto
        out = out.replace("😄", "@[:smile:]")
        out = out.replace("😢", "@[:sad:]")
        out = out.replace("😐", "@[:serious:]")
        out = out.replace("❤️", "@[:heart:]")
        out = out.replace("😺", "@[:cat:]")
        return out
    }

    fun restaurarEmojisDesdePkm(texto: String): String {
        var out = texto
        out = out.replace("@[:smile:]", "😄")
        out = out.replace("@[:sad:]", "😢")
        out = out.replace("@[:serious:]", "😐")
        out = out.replace("@[:heart:]", "❤️")
        out = out.replace("@[:cat:]", "😺")
        return out
    }

    fun cadenaEntreComillas(texto: String): String {
        val normalizado = normalizarEmojisParaGuardado(texto)
        val escapado = escaparCadena(normalizado)
        return "\"$escapado\""
    }
}
