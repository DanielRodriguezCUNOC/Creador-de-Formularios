package com.example.proyecto1_compi1_1s_2026.ui.integration

import com.example.proyecto1_compi1_1s_2026.backend.generate.forms.LexerFormulario
import com.example.proyecto1_compi1_1s_2026.backend.generate.forms.ParserFormulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoInstruccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TipoError
import java.io.StringReader

data class ResultadoParseoFormulario(
    val instrucciones: List<NodoInstruccion> = emptyList(),
    val erroresLexicos: List<ErrorInfo> = emptyList(),
    val erroresSintacticos: List<ErrorInfo> = emptyList(),
    val erroresSemanticos: List<ErrorInfo> = emptyList()
) {
    val exitoso: Boolean
        get() = erroresLexicos.isEmpty() && erroresSintacticos.isEmpty() && erroresSemanticos.isEmpty()
}

class FormularioAstParser {

    fun parsear(codigoFuente: String): ResultadoParseoFormulario {
        var lexer: LexerFormulario? = null
        var parser: ParserFormulario? = null

        return try {
            lexer = LexerFormulario(StringReader(codigoFuente))
            parser = ParserFormulario(lexer)
            val resultado = parser.parse()

            val erroresLexicos = lexer.lexicalErrors
            val erroresSintacticos = parser.erroresSintacticos
            if (erroresLexicos.isNotEmpty() || erroresSintacticos.isNotEmpty()) {
                return ResultadoParseoFormulario(
                    erroresLexicos = erroresLexicos,
                    erroresSintacticos = erroresSintacticos
                )
            }

            if (resultado?.value !is List<*>) {
                return ResultadoParseoFormulario(
                    erroresSemanticos = listOf(
                        ErrorInfo(TipoError.SEMANTICO, "No se pudo construir el AST del formulario", 0, 0)
                    )
                )
            }

            @Suppress("UNCHECKED_CAST")
            ResultadoParseoFormulario(instrucciones = resultado.value as List<NodoInstruccion>)
        } catch (e: Exception) {
            val errLex = lexer?.lexicalErrors ?: emptyList()
            val errSin = parser?.erroresSintacticos ?: emptyList()

            if (errLex.isNotEmpty() || errSin.isNotEmpty()) {
                ResultadoParseoFormulario(
                    erroresLexicos = errLex,
                    erroresSintacticos = errSin
                )
            } else {
                ResultadoParseoFormulario(
                    erroresSemanticos = listOf(
                        ErrorInfo(TipoError.SEMANTICO, "Excepción inesperada: ${e.message}", 0, 0)
                    )
                )
            }
        }
    }
}