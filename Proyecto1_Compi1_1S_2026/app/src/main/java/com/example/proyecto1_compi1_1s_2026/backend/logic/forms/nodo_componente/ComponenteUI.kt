package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoInstruccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

abstract class ComponenteUI(
    //* Almacenar por clave valor width -> Literal(100)
    val atributos: Map<String, Object>
): NodoInstruccion{
    abstract override fun <T> accept(visitor: Visitor<T>):T
}