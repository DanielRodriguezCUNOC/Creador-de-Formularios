package com.example.proyecto1_compi1_1s_2026.ui.integration.forms

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoInstruccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.RecolectorSimbolos
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TablaSimbolos
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ValidadorEstructural
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ValidadorSemantico

data class ResultadoValidacionFormulario(
    val instrucciones: List<NodoInstruccion>,
    val erroresSemanticos: List<ErrorInfo> = emptyList()
) {
    val exitoso: Boolean
        get() = erroresSemanticos.isEmpty()
}

class FormularioValidationService {

    fun validar(instrucciones: List<NodoInstruccion>): ResultadoValidacionFormulario {
        val recolector = RecolectorSimbolos(TablaSimbolos(null))
        val resultadoRecoleccion = recolector.recolectar(instrucciones)
        if (resultadoRecoleccion.errores.isNotEmpty()) {
            return ResultadoValidacionFormulario(
                instrucciones = instrucciones,
                erroresSemanticos = resultadoRecoleccion.errores
            )
        }

        val erroresEstructurales = ValidadorEstructural().validar(instrucciones)
        if (erroresEstructurales.isNotEmpty()) {
            return ResultadoValidacionFormulario(
                instrucciones = instrucciones,
                erroresSemanticos = erroresEstructurales
            )
        }

        val erroresSemanticos = ValidadorSemantico(resultadoRecoleccion.tablaSimbolos).validar(instrucciones)
        return ResultadoValidacionFormulario(
            instrucciones = instrucciones,
            erroresSemanticos = erroresSemanticos
        )
    }
}
