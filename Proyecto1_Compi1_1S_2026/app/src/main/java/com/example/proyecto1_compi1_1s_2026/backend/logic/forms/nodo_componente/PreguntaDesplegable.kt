package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

class PreguntaDesplegable(
    atributos: Map<String, Object>,
    linea: Int = 0,
    columna: Int = 0
) : ComponenteUI(atributos, linea, columna) {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)
}