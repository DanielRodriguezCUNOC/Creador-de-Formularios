package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models

data class PreguntaAbierta(
    override val width: Float? = null,
    override val height: Float? = null,
    val label: String,
    override val estilos: EstiloElemento = EstiloElemento(),
    var respuesta: String = ""
) : ElementoFormulario()
