package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal

interface NodoBase{
    fun <T> accept(visitor: Visitor<T>): T
}