package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

class PreguntaSeleccionadaMultiple(
    atributos: List<NodoAtributo>,
    linea: Int = 0,
    columna: Int = 0
) : ComponenteUI(atributos, linea, columna) {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)

    override fun validarSemantica(contexto: ContextoSemantico) {
        validarAtributosObligatorios(contexto, listOf("options"), "MULTIPLE_QUESTION")
        validarOpcionesNoVacias(contexto, "MULTIPLE_QUESTION")
        validarCorrectMultiple(contexto, "MULTIPLE_QUESTION")
    }
}