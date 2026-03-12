package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models

data class TextoFormulario(
    override val width: Float? = null,
    override val height: Float? = null,
    val contenido: String,
    override val estilos: EstiloElemento = EstiloElemento()
) : ElementoFormulario()
