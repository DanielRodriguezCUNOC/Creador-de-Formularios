package com.example.proyecto1_compi1_1s_2026.ui.integration

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Formulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TipoError
import java.io.StringReader

class PkmAstParser {

    fun parsear(codigoPkm: String): ResultadoParseoPkm {
        var lexerInstance: Any? = null
        var parserInstance: Any? = null

        return try {
            val lexerClass = Class.forName("com.example.proyecto1_compi1_1s_2026.backend.generate.pkm.LexerPKM")
            val parserClass = Class.forName("com.example.proyecto1_compi1_1s_2026.backend.generate.pkm.ParserPKM")

            lexerInstance = lexerClass.getConstructor(java.io.Reader::class.java)
                .newInstance(StringReader(codigoPkm))
            parserInstance = parserClass.getConstructor(java_cup.runtime.Scanner::class.java)
                .newInstance(lexerInstance)

            val symbolResultado = parserClass.getMethod("parse").invoke(parserInstance) as? java_cup.runtime.Symbol
            val formulario = if (symbolResultado?.value is Formulario) {
                symbolResultado.value as Formulario
            } else {
                null
            }

            val erroresLexicos = obtenerErrores(lexerInstance, "getLexicalErrors")
            val erroresSintacticos = obtenerErrores(parserInstance, "getErroresSintacticos")

            if (erroresLexicos.isEmpty() && erroresSintacticos.isEmpty() && formulario == null) {
                return ResultadoParseoPkm(
                    erroresSemanticos = listOf(
                        ErrorInfo(
                            tipo = TipoError.SEMANTICO,
                            mensaje = "El parser PKM no devolvio un Formulario valido",
                            linea = 0,
                            columna = 0
                        )
                    )
                )
            }

            ResultadoParseoPkm(
                formulario = formulario,
                erroresLexicos = erroresLexicos,
                erroresSintacticos = erroresSintacticos
            )
        } catch (e: Exception) {
            if (e is ClassNotFoundException) {
                return ResultadoParseoPkm(
                    erroresSemanticos = listOf(
                        ErrorInfo(
                            tipo = TipoError.SEMANTICO,
                            mensaje = "No se encontraron LexerPKM/ParserPKM generados. Ejecuta generar_Lexer_&_Parser.sh",
                            linea = 0,
                            columna = 0
                        )
                    )
                )
            }

            val erroresLexicos = obtenerErrores(lexerInstance, "getLexicalErrors")
            val erroresSintacticos = obtenerErrores(parserInstance, "getErroresSintacticos")

            if (erroresLexicos.isNotEmpty() || erroresSintacticos.isNotEmpty()) {
                ResultadoParseoPkm(
                    erroresLexicos = erroresLexicos,
                    erroresSintacticos = erroresSintacticos
                )
            } else {
                ResultadoParseoPkm(
                    erroresSemanticos = listOf(
                        ErrorInfo(
                            tipo = TipoError.SEMANTICO,
                            mensaje = "Excepcion inesperada al analizar PKM: ${e.message}",
                            linea = 0,
                            columna = 0
                        )
                    )
                )
            }
        }
    }

    private fun obtenerErrores(origen: Any?, metodo: String): List<ErrorInfo> {
        if (origen == null) return emptyList()

        return try {
            @Suppress("UNCHECKED_CAST")
            val resultado = origen.javaClass.getMethod(metodo).invoke(origen) as? List<ErrorInfo>
            resultado ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
}
