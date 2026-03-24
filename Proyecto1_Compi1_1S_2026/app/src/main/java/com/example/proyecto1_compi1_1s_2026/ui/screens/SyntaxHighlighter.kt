package com.example.proyecto1_compi1_1s_2026.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Utilidad de resaltado de sintaxis.
 *
 * Convierte un [String] de código en un [AnnotatedString] con colores
 *
 * Prioridad de tokens (de menor a mayor):
 *   números → tipos → palabras clave → cadenas → comentarios
 *
 * Las palabras clave de formularios se pueden ampliar en [keywords].
 */
object SyntaxHighlighter {

    // ── Colores del editor ────────────────────────────────────────────────

    /** Fondo oscuro del área de código. */
    val bgColor   = Color(0xFF1E1E1E)

    /** Color de texto predeterminado (para tokens sin clasificar). */
    val textColor = Color(0xFFD4D4D4)

    // Paleta (VS Code Dark+)
    private val colorKeyword = Color(0xFF569CD6)   // azul
    private val colorType    = Color(0xFF4EC9B0)   // verde azulado
    private val colorString  = Color(0xFFCE9178)   // naranja
    private val colorNumber  = Color(0xFFB5CEA8)   // verde claro
    private val colorComment = Color(0xFF6A9955)   // verde
    private val colorOperator = Color(0xFF6A9955)  // verde
    private val colorParenthesis = Color(0xFF569CD6) // azul

    // ── Palabras clave ────────────────────────────────────────────────────

    private val keywords = setOf(
        // Control de flujo
        "if", "else", "while", "for", "do", "return", "break", "continue",
        "when", "in", "is", "as",
        // Declaraciones
        "fun", "val", "var", "const", "class", "interface", "object",
        "companion", "override", "abstract", "open", "sealed",
        // Visibilidad
        "public", "private", "protected", "internal",
        // Otros
        "import", "package", "try", "catch", "finally", "throw", "new",
        "null", "true", "false",
        // ── Palabras clave del lenguaje de formularios (personalizar aquí) ──
        "form", "field", "label", "input", "text", "number", "date",
        "checkbox", "radio", "select", "option", "button", "submit", "reset",
        "required", "optional", "default", "type", "name", "value", "placeholder"
    )

    // ── Tipos primitivos / estándar ───────────────────────────────────────

    private val types = setOf(
        "String", "Int", "Float", "Double", "Boolean", "Long",
        "Char", "Byte", "Short", "Unit", "Any", "Nothing",
        "List", "Map", "Set", "Array"
    )

    // ── Expresiones regulares ─────────────────────────────────────────────

    /** Número entero o decimal con sufijo opcional (1, 3.14, 2f, 10L…) */
    private val regexNumber = Regex("""\b\d+(\.\d+)?[fFdDlL]?\b""")

    /** Cadena entre comillas dobles con soporte de escapes (\n, \", …) */
    private val regexString = Regex(""""([^"\\]|\\.)*"""")

    /** Comentario de una sola línea */
    private val regexLineComment = Regex("""//[^\n]*""")

    /** Comentario multilínea */
    private val regexBlockComment = Regex("""/\*.*?\*/""", RegexOption.DOT_MATCHES_ALL)

    /** Operadores aritméticos */
    private val regexArithmeticOperators = Regex("""[+\-*/%^]""")

    /** Paréntesis */
    private val regexParentheses = Regex("""[()]""")

    // ── API pública ───────────────────────────────────────────────────────

    /**
     * Aplica resaltado de sintaxis a [code] y devuelve un [AnnotatedString]
     * listo para usar en un composable [androidx.compose.material3.Text] o
     * [androidx.compose.material3.OutlinedTextField].
     */
    fun highlight(code: String): AnnotatedString {
        if (code.isEmpty()) return AnnotatedString("")

        return buildAnnotatedString {
            append(code)

            // 1. Números (menor prioridad)
            regexNumber.findAll(code).forEach { m ->
                addStyle(SpanStyle(color = colorNumber), m.range.first, m.range.last + 1)
            }

            // 2. Tipos
            types.forEach { t ->
                Regex("""\b${Regex.escape(t)}\b""").findAll(code).forEach { m ->
                    addStyle(SpanStyle(color = colorType), m.range.first, m.range.last + 1)
                }
            }

            // 3. Palabras clave
            keywords.forEach { kw ->
                Regex("""\b${Regex.escape(kw)}\b""").findAll(code).forEach { m ->
                    addStyle(
                        SpanStyle(color = colorKeyword, fontWeight = FontWeight.Bold),
                        m.range.first, m.range.last + 1
                    )
                }
            }

            // 4. Operadores aritméticos
            regexArithmeticOperators.findAll(code).forEach { m ->
                addStyle(SpanStyle(color = colorOperator), m.range.first, m.range.last + 1)
            }

            // 5. Paréntesis
            regexParentheses.findAll(code).forEach { m ->
                addStyle(SpanStyle(color = colorParenthesis), m.range.first, m.range.last + 1)
            }

            // 6. Cadenas (prioridad sobre palabras clave)
            regexString.findAll(code).forEach { m ->
                addStyle(SpanStyle(color = colorString), m.range.first, m.range.last + 1)
            }

            // 7. Comentarios de una línea
            regexLineComment.findAll(code).forEach { m ->
                addStyle(
                    SpanStyle(color = colorComment, fontStyle = FontStyle.Italic),
                    m.range.first, m.range.last + 1
                )
            }

            // 8. Comentarios multilínea (mayor prioridad)
            regexBlockComment.findAll(code).forEach { m ->
                addStyle(
                    SpanStyle(color = colorComment, fontStyle = FontStyle.Italic),
                    m.range.first, m.range.last + 1
                )
            }
        }
    }
}
