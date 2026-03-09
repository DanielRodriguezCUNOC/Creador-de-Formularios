package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoInstruccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

abstract class ComponenteUI(
    /** Almacena pares clave-valor de atributos, p.ej. width -> Literal(100) */
    val atributos: Map<String, Object>,
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoInstruccion {
    abstract override fun <T> accept(visitor: Visitor<T>): T
}