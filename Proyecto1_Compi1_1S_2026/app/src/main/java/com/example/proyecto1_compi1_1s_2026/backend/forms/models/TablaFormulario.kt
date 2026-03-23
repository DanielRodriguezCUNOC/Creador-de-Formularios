package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models

data class TablaFormulario(
    override val width: Float?,
    override val height: Float?,
    val pointX: Float = 0f,
    val pointY: Float = 0f,
    val filas: List<List<ElementoFormulario>> = emptyList(),
    override val estilos: EstiloElemento = EstiloElemento()
) : ElementoFormulario()
