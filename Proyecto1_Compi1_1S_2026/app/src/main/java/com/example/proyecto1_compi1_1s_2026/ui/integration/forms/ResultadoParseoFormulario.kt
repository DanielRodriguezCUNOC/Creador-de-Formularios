package com.example.proyecto1_compi1_1s_2026.ui.integration.forms

data class ResultadoParseoFormulario(
    val instrucciones: List<NodoInstruccion> = emptyList(),
    val erroresLexicos: List<ErrorInfo> = emptyList(),
    val erroresSintacticos: List<ErrorInfo> = emptyList(),
    val erroresSemanticos: List<ErrorInfo> = emptyList()
) {
    val exitoso: Boolean
        get() = erroresLexicos.isEmpty() && erroresSintacticos.isEmpty() && erroresSemanticos.isEmpty()
}