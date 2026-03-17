package com.example.proyecto1_compi1_1s_2026.ui.integration

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Formulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoInstruccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.GeneradorPkm
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.Interprete
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TablaSimbolos

data class ResultadoConstruccionFormulario(
    val formulario: Formulario? = null,
    val codigoPkm: String = "",
    val erroresSemanticos: List<ErrorInfo> = emptyList()
) {
    val exitoso: Boolean
        get() = erroresSemanticos.isEmpty() && formulario != null
}

class FormularioBuildService(
    private val crearGeneradorPkm: () -> GeneradorPkm = { GeneradorPkm() },
    private val crearInterprete: () -> Interprete = { Interprete(TablaSimbolos(null)) }
) {

    fun construir(instrucciones: List<NodoInstruccion>): ResultadoConstruccionFormulario {
        val generadorPkm = crearGeneradorPkm()
        val resultadoPkm = generadorPkm.generar(instrucciones)
        if (resultadoPkm.errores.isNotEmpty()) {
            return ResultadoConstruccionFormulario(erroresSemanticos = resultadoPkm.errores)
        }

        val interprete = crearInterprete()
        val resultadoInterpretacion = interprete.interpretar(instrucciones)
        if (resultadoInterpretacion.errores.isNotEmpty()) {
            return ResultadoConstruccionFormulario(erroresSemanticos = resultadoInterpretacion.errores)
        }

        return ResultadoConstruccionFormulario(
            formulario = resultadoInterpretacion.formulario,
            codigoPkm = resultadoPkm.codigo
        )
    }
}