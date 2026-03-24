package com.example.proyecto1_compi1_1s_2026.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.example.proyecto1_compi1_1s_2026.backend.generate.forms.TokenInfo

object LexerSyntaxHighlighter {
    // Puedes personalizar los colores según tu preferencia
    private val colorKeyword = Color(0xFF569CD6)
    private val colorType    = Color(0xFF4EC9B0)
    private val colorString  = Color(0xFFCE9178)
    private val colorNumber  = Color(0xFFB5CEA8)
    private val colorComment = Color(0xFF6A9955)
    private val colorOperator = Color(0xFF6A9955)
    private val colorParenthesis = Color(0xFF569CD6)
    private val colorDefault = Color(0xFFD4D4D4)

    fun highlightWithTokens(code: String, tokens: List<TokenInfo>): AnnotatedString {
        return buildAnnotatedString {
            append(code)
            tokens.forEach { token ->
                val color = when (token.tipo) {
                    // Asocia los valores de sym.<TOKEN> con colores
                    // Ejemplo:
                    1 -> colorKeyword // sym.SECTION, etc.
                    2 -> colorKeyword // sym.TABLE, etc.
                    3 -> colorString  // sym.INICIO_CADENA, etc.
                    4 -> colorNumber  // sym.NUMBER_LITERAL, etc.
                    5 -> colorComment // comentarios
                    // ...agrega más según tus sym
                    else -> colorDefault
                }
                addStyle(SpanStyle(color = color), token.inicio, token.fin)
            }
        }
    }
}
