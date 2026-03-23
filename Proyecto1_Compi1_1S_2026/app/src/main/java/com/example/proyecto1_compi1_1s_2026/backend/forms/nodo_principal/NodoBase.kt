package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal

interface NodoBase {

    val linea: Int

    val columna: Int

    fun <T> accept(visitor: Visitor<T>): T
}