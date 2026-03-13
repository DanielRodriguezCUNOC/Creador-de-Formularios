package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoInstruccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

class ComponenteSeccion(
    atributos: List<NodoAtributo>,
    /** Almacena los componentes que irán dentro de la sección */
    val elementosInternos: List<NodoInstruccion>,
    linea: Int = 0,
    columna: Int = 0
) : ComponenteUI(atributos, linea, columna) {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)

    override fun validarSemantica(contexto: ContextoSemantico) {
        validarAtributosObligatorios(
            contexto,
            listOf("width", "height", "pointX", "pointY"),
            "SECTION"
        )
        validarBorde(contexto)
        elementosInternos.forEach { it.validarSemantica(contexto) }
    }
}