package com.example.proyecto1_compi1_1s_2026.ui.integration.forms

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoInstruccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo

data class ResultadoParseoFormulario(
    val instrucciones: List<NodoInstruccion> = emptyList(),
    val erroresLexicos: List<ErrorInfo> = emptyList(),
    val erroresSintacticos: List<ErrorInfo> = emptyList(),
    val erroresSemanticos: List<ErrorInfo> = emptyList()
) {
    val exitoso: Boolean
        get() = erroresLexicos.isEmpty() && erroresSintacticos.isEmpty() && erroresSemanticos.isEmpty()
}