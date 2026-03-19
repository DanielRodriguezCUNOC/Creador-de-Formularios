package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models

data class EstiloElemento(
    val color: Color = Color(0, 0, 0), // Negro por defecto
    val backgroundColor: Color = Color(255, 255, 255, 0), // Transparente/Blanco por defecto
    val fontFamily: String = "SANS_SERIF",
    val textSize: Float = 14f,
    val border: BorderEstilo? = null
)
