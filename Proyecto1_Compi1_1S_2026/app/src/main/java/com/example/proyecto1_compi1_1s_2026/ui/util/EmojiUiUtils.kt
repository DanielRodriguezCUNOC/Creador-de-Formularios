package com.example.proyecto1_compi1_1s_2026.ui.util

/**
 * Utilidad exclusiva de la capa de UI para interpretar las etiquetas
 * de emojis y estrellas dinámicas y dibujarlas en Jetpack Compose.
 */
object EmojiUiUtils {

    fun decodificarParaPantalla(texto: String): String {
        val sb = StringBuilder()
        var i = 0
        
        while (i < texto.length) {
            if (texto.startsWith("@[:star-", i)) {
                val inicioNum = i + 8
                val finCierre = texto.indexOf(":]", inicioNum)
                if (finCierre != -1) {
                    val numStr = texto.substring(inicioNum, finCierre).trim()
                    val n = numStr.toIntOrNull() ?: 1
                    repeat(n.coerceAtLeast(0)) { sb.append('⭐') }
                    i = finCierre + 2
                    continue
                }
            } else if (texto.startsWith("@[:star:", i)) {
                val inicioNum = i + 8
                val finCierre = texto.indexOf(":]", inicioNum)
                if (finCierre != -1) {
                    val numStr = texto.substring(inicioNum, finCierre).trim()
                    val n = numStr.toIntOrNull() ?: 1
                    repeat(n.coerceAtLeast(0)) { sb.append('⭐') }
                    i = finCierre + 2
                    continue
                }
            } else if (texto.startsWith("@[:star:]", i)) {
                sb.append('⭐')
                i += 9
                continue
            }
            sb.append(texto[i])
            i++
        }

        var resultado = sb.toString()
        resultado = resultado.replace("@[:smile:]", "😄")
        resultado = resultado.replace("@[:sad:]", "😢")
        resultado = resultado.replace("@[:serious:]", "😐")
        resultado = resultado.replace("@[:heart:]", "❤️")
        resultado = resultado.replace("@[:cat:]", "😺")
        
        return resultado
    }
}