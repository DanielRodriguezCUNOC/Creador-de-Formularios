package com.example.proyecto1_compi1_1s_2026.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.example.proyecto1_compi1_1s_2026.backend.generate.forms.TokenInfo

object LexerSyntaxHighlighter {
    
    private val colorKeyword = Color(0xFF569CD6)
    private val colorType    = Color(0xFF4EC9B0)
    private val colorString  = Color(0xFFCE9178)
    private val colorNumber  = Color(0xFFB5CEA8)
    private val colorComment = Color(0xFF6A9955)
    private val colorOperator = Color(0xFF6A9955)
    private val colorParenthesis = Color(0xFF569CD6)
    val colorDefault = Color(0xFFD4D4D4)
    val colorBackground = Color(0xFF1E1E1E)

    private fun getColorForTokenType(tipo: Int): Color {
        // Operadores aritméticos: Verde
        val operadoresArit = setOf(
            61, // MAS
            62, // MENOS
            63, // POR
            64, // DIV
            65, // POTENCIA
            66  // MODULO
        )
        // Variables: Blanco (IDENTIFICADOR)
        val variables = setOf(76) // IDENTIFICADOR
        // Strings literales: Naranja
        val strings = setOf(78, 79, 80) // INICIO_CADENA, FIN_CADENA, CONTENIDO_CADENA
        // Números literales: Celeste
        val numeros = setOf(77, 60) // NUMBER_LITERAL, NUMBER
        // Palabras reservadas: Morado
        val palabrasReservadas = setOf(
            2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47
        )
        // Llaves, corchetes, paréntesis: Azul
        val llavesCorchetesParentesis = setOf(48,49,50,51,52,53)
        // Especificación de emojis: Amarillo
        val emojis = setOf(81,82,83,84,85,86)

        return when {
            tipo in operadoresArit -> colorOperator
            tipo in variables -> colorDefault
            tipo in strings -> colorString
            tipo in numeros -> colorNumber
            tipo in palabrasReservadas -> colorType
            tipo in llavesCorchetesParentesis -> colorKeyword
            tipo in emojis -> Color(0xFFFFD700) // Amarillo
            else -> colorDefault
        }
    }

    fun highlightWithTokens(code: String, tokens: List<TokenInfo>): AnnotatedString {
        return buildAnnotatedString {
            append(code)
            tokens.forEach { token ->
                val color = getColorForTokenType(token.tipo)
                addStyle(SpanStyle(color = color), token.inicio, token.fin)
            }
        }
    }

    // Nuevo método: si no hay tokens, solo muestra el texto plano
    fun highlight(code: String, tokens: List<TokenInfo>? = null): AnnotatedString {
        return if (tokens != null) highlightWithTokens(code, tokens) else buildAnnotatedString { append(code) }
    }
}

fun analizarTokensPKM(codigo: String): List<TokenInfo> {
    val tokens = mutableListOf<TokenInfo>()
    try {
        val lexerClass = Class.forName("com.example.proyecto1_compi1_1s_2026.backend.generate.pkm.LexerPKM")
        val symClass = Class.forName("com.example.proyecto1_compi1_1s_2026.backend.generate.pkm.sym")
        val tokenInfoClass = Class.forName("com.example.proyecto1_compi1_1s_2026.backend.generate.forms.TokenInfo")
        val stringReaderCtor = lexerClass.getConstructor(java.io.Reader::class.java)
        val lexer = stringReaderCtor.newInstance(java.io.StringReader(codigo))
        val nextTokenMethod = lexerClass.getMethod("next_token")
        val symEOF = symClass.getField("EOF").getInt(null)

        while (true) {
            val symbol = nextTokenMethod.invoke(lexer)
            val symType = symbol.javaClass.getField("sym").getInt(symbol)
            if (symType == symEOF) break
            val value = symbol.javaClass.getField("value").get(symbol)
            val left = symbol.javaClass.getField("left").getInt(symbol)
            val right = symbol.javaClass.getField("right").getInt(symbol)
            val texto = value?.toString() ?: ""
            // Crea instancia de TokenInfo
            val tokenInfo = tokenInfoClass.getConstructor(Int::class.java, String::class.java, Int::class.java, Int::class.java)
                .newInstance(symType, texto, left, right) as TokenInfo
            tokens.add(tokenInfo)
        }
    } catch (e: Exception) {
        // Si hay error, regresa lista vacía
    }
    return tokens
}
