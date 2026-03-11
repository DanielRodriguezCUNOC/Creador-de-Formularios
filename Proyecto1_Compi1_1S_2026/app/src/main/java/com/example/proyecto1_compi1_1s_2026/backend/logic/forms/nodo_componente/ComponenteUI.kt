package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoInstruccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

abstract class ComponenteUI(
    /** Lista de atributos del componente. Se usa List en lugar de Map porque
     *  el número de atributos es pequeño (≤7) y la búsqueda lineal O(n)
     *  es más eficiente que el overhead del hashing de un HashMap. */
    val atributos: List<NodoAtributo>,
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoInstruccion {
    abstract override fun <T> accept(visitor: Visitor<T>): T
}