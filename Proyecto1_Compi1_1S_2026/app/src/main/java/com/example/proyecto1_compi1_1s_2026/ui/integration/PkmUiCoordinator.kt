package com.example.proyecto1_compi1_1s_2026.ui.integration

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo

/**
 * Solo valida errores de analisis (lexico/sintactico) y
 * deja lista la misma estructura de salida usada por la UI.
 */
class PkmUiCoordinator(
    private val parser: PkmAstParser = PkmAstParser()
) {

    fun analizar(codigoPkm: String): ResultadoAnalisisPkm {
        val resultadoParseo = parser.parsear(codigoPkm)

        if (!resultadoParseo.exitoso) {
            return ResultadoAnalisisPkm(
                erroresLexicos = resultadoParseo.erroresLexicos,
                erroresSintacticos = resultadoParseo.erroresSintacticos,
                erroresSemanticos = resultadoParseo.erroresSemanticos
            )
        }

        // Se retorna exito de analisis sin errores.
        return ResultadoAnalisisPkm()
    }
}
