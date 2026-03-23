package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models

sealed class ElementoFormulario {
    abstract val width: Float?
    abstract val height: Float?
    abstract val estilos: EstiloElemento
}
