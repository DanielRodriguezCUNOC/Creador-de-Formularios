package com.example.proyecto1_compi1_1s_2026.ui.integration

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Formulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo

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

class FormularioUiCoordinator(
    private val parser: FormularioAstParser = FormularioAstParser(),
    private val validator: FormularioValidationService = FormularioValidationService(),
    private val builder: FormularioBuildService = FormularioBuildService()
) {

    fun analizar(codigoFuente: String): ResultadoAnalisisUi {
        val resultadoParseo = parser.parsear(codigoFuente)
        if (!resultadoParseo.exitoso) {
            return ResultadoAnalisisUi(
                erroresLexicos = resultadoParseo.erroresLexicos,
                erroresSintacticos = resultadoParseo.erroresSintacticos,
                erroresSemanticos = resultadoParseo.erroresSemanticos
            )
        }

        val resultadoValidacion = validator.validar(resultadoParseo.instrucciones)
        if (!resultadoValidacion.exitoso) {
            return ResultadoAnalisisUi(erroresSemanticos = resultadoValidacion.erroresSemanticos)
        }

        val resultadoConstruccion = builder.construir(resultadoValidacion.instrucciones)
        if (!resultadoConstruccion.exitoso) {
            return ResultadoAnalisisUi(erroresSemanticos = resultadoConstruccion.erroresSemanticos)
        }

        return ResultadoAnalisisUi(
            formulario = resultadoConstruccion.formulario,
            codigoPkm = resultadoConstruccion.codigoPkm
        )
    }
}
