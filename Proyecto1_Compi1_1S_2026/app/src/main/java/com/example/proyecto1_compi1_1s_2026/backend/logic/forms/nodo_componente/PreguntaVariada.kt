package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

class PreguntaDesplegable(
    atributos: Map<String, Object>
): ComponenteUI(atributos) {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}

class PreguntaSeleccionUnica(
    atributos: Map<String, Object>
): ComponenteUI(atributos){
    override fun <T> accept(visitor: Visitor<T>):T{
        return visitor.visit(this)
    }
}

class PreguntaSeleccionadaMultiple(
    atributos: Map<String, Object>
): ComponenteUI(atributos){
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visit(this)
    }
}