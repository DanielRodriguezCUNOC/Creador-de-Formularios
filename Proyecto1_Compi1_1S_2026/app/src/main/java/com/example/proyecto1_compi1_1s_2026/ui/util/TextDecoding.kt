package com.example.proyecto1_compi1_1s_2026.ui.util

import java.nio.charset.StandardCharsets

/**
 * Decodifica texto de forma robusta para evitar mojibake cuando el origen
 * no declara bien el charset (UTF-8, ISO-8859-1 o Windows-1252).
 */
fun decodeTextRobust(bytes: ByteArray): String {
    if (bytes.isEmpty()) return ""

    val sinBomUtf8 = removeUtf8Bom(bytes)
    val utf8 = String(sinBomUtf8, StandardCharsets.UTF_8)
    if (!looksBroken(utf8)) return utf8

    val latin1 = String(bytes, StandardCharsets.ISO_8859_1)
    if (!looksBroken(latin1)) return latin1

    return utf8
}

private fun removeUtf8Bom(bytes: ByteArray): ByteArray {
    return if (
        bytes.size >= 3
        && bytes[0] == 0xEF.toByte()
        && bytes[1] == 0xBB.toByte()
        && bytes[2] == 0xBF.toByte()
    ) {
        bytes.copyOfRange(3, bytes.size)
    } else {
        bytes
    }
}

private fun looksBroken(text: String): Boolean {
    if (text.isEmpty()) return false

    // Marcadores tipicos de texto mal decodificado.
    return text.contains('\uFFFD')
        || text.contains("Ã")
        || text.contains("Â")
        || text.contains("â€")
}
