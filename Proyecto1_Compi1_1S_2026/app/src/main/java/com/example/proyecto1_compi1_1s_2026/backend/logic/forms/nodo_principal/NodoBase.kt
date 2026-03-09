package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal

interface NodoBase {
    /** Línea del código fuente donde inicia este nodo (1-based, 0 = desconocida). */
    val linea: Int
    /** Columna del código fuente donde inicia este nodo (1-based, 0 = desconocida). */
    val columna: Int

    fun <T> accept(visitor: Visitor<T>): T
}