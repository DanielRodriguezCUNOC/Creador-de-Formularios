package com.example.proyecto1_compi1_1s_2026.ui.integration


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