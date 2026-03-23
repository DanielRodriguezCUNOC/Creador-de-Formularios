package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models

data class PreguntaSeleccionMultiple(
    override val width: Float? = null,
    override val height: Float? = null,
    val label: String,
    val opciones: List<String>,
    val correctas: List<Int> = emptyList(),
    override val estilos: EstiloElemento = EstiloElemento(),
    var seleccionadas: MutableSet<Int> = mutableSetOf()
) : ElementoFormulario()
