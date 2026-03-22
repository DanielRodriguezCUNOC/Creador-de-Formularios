package com.example.proyecto1_compi1_1s_2026.ui.integration

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Formulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo

data class ResultadoParseoPkm(
    val formulario: Formulario? = null,
    val erroresLexicos: List<ErrorInfo> = emptyList(),
    val erroresSintacticos: List<ErrorInfo> = emptyList(),
    val erroresSemanticos: List<ErrorInfo> = emptyList()
) {
    val exitoso: Boolean
        get() = erroresLexicos.isEmpty() && erroresSintacticos.isEmpty() && erroresSemanticos.isEmpty() && formulario != null
}