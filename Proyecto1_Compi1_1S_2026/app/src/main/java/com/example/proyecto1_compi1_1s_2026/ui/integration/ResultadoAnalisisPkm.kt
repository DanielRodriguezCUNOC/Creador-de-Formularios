package com.example.proyecto1_compi1_1s_2026.ui.integration
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo
data class ResultadoAnalisisPkm(
    val erroresLexicos: List<ErrorInfo> = emptyList(),
    val erroresSintacticos: List<ErrorInfo> = emptyList(),
    val erroresSemanticos: List<ErrorInfo> = emptyList()
) {
    val errores: List<ErrorInfo>
        get() = erroresLexicos + erroresSintacticos + erroresSemanticos

    val exitoso: Boolean
        get() = errores.isEmpty()
}