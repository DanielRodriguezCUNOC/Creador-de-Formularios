package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models

data class PreguntaDesplegable(
    override val width: Float? = null,
    override val height: Float? = null,
    val label: String,
    val opciones: List<String>,
    val correcta: Int? = null,
    override val estilos: EstiloElemento = EstiloElemento(),
    var seleccionada: Int = -1
) : ElementoFormulario()
