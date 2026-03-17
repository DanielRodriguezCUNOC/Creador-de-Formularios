package com.example.proyecto1_compi1_1s_2026.ui.integration

import com.example.proyecto1_compi1_1s_2026.backend.generate.forms.LexerFormulario
import com.example.proyecto1_compi1_1s_2026.backend.generate.forms.ParserFormulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Formulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoInstruccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.GeneradorPkm
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.Interprete
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.RecolectorSimbolos
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TablaSimbolos
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TipoError
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ValidadorEstructural
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ValidadorSemantico
import java.io.StringReader

data class ResultadoAnalisisUi(
    val formulario: Formulario? = null,
    val codigoPkm: String = "",
    val erroresLexicos: List<ErrorInfo> = emptyList(),
    val erroresSintacticos: List<ErrorInfo> = emptyList(),
    val erroresSemanticos: List<ErrorInfo> = emptyList()
) {
    val errores: List<ErrorInfo>
        get() = erroresLexicos + erroresSintacticos + erroresSemanticos

    val exitoso: Boolean
        get() = errores.isEmpty() && formulario != null

    val primerError: ErrorInfo?
        get() = errores.firstOrNull()
}

class FormularioUiCoordinator {

    fun analizar(codigoFuente: String): ResultadoAnalisisUi {
        var lexer: LexerFormulario? = null
        var parser: ParserFormulario? = null

        return try {
            lexer = LexerFormulario(StringReader(codigoFuente))
            parser = ParserFormulario(lexer)
            val resultado = parser.parse()

            val erroresLexicos = lexer.lexicalErrors
            val erroresSintacticos = parser.erroresSintacticos
            if (erroresLexicos.isNotEmpty() || erroresSintacticos.isNotEmpty()) {
                return ResultadoAnalisisUi(
                    erroresLexicos = erroresLexicos,
                    erroresSintacticos = erroresSintacticos
                )
            }

            if (resultado?.value !is List<*>) {
                return ResultadoAnalisisUi(
                    erroresSemanticos = listOf(
                        ErrorInfo(TipoError.SEMANTICO, "No se pudo construir el AST del formulario", 0, 0)
                    )
                )
            }

            @Suppress("UNCHECKED_CAST")
            val instrucciones = resultado.value as List<NodoInstruccion>

            val recolector = RecolectorSimbolos(TablaSimbolos(null))
            val resultadoRecoleccion = recolector.recolectar(instrucciones)
            if (resultadoRecoleccion.errores.isNotEmpty()) {
                return ResultadoAnalisisUi(erroresSemanticos = resultadoRecoleccion.errores)
            }

            val erroresEstructurales = ValidadorEstructural().validar(instrucciones)
            if (erroresEstructurales.isNotEmpty()) {
                return ResultadoAnalisisUi(erroresSemanticos = erroresEstructurales)
            }

            val erroresSemanticos = ValidadorSemantico(resultadoRecoleccion.tablaSimbolos).validar(instrucciones)
            if (erroresSemanticos.isNotEmpty()) {
                return ResultadoAnalisisUi(erroresSemanticos = erroresSemanticos)
            }

            val resultadoPkm = GeneradorPkm().generar(instrucciones)
            if (resultadoPkm.errores.isNotEmpty()) {
                return ResultadoAnalisisUi(erroresSemanticos = resultadoPkm.errores)
            }

            val resultadoInterpretacion = Interprete(TablaSimbolos(null)).interpretar(instrucciones)
            if (resultadoInterpretacion.errores.isNotEmpty()) {
                return ResultadoAnalisisUi(erroresSemanticos = resultadoInterpretacion.errores)
            }

            ResultadoAnalisisUi(
                formulario = resultadoInterpretacion.formulario,
                codigoPkm = resultadoPkm.codigo
            )
        } catch (e: Exception) {
            val errLex = lexer?.lexicalErrors ?: emptyList()
            val errSin = parser?.erroresSintacticos ?: emptyList()

            if (errLex.isNotEmpty() || errSin.isNotEmpty()) {
                ResultadoAnalisisUi(
                    erroresLexicos = errLex,
                    erroresSintacticos = errSin
                )
            } else {
                ResultadoAnalisisUi(
                    erroresSemanticos = listOf(
                        ErrorInfo(TipoError.SEMANTICO, "Excepción inesperada: ${e.message}", 0, 0)
                    )
                )
            }
        }
    }
}
