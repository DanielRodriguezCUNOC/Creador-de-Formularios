package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.Expresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.Instruccion

abstract class ComponenteUI(
    //* Almacenar por clave valor width -> Literal(100)
    val atributos: Map<String, Expresion>
): Instruccion