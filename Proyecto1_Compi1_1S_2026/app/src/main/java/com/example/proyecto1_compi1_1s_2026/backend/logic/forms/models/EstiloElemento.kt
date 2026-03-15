package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models

import androidx.compose.ui.graphics.Color

data class EstiloElemento(
    val color: Color = Color.Black,
    val backgroundColor: Color = Color.White,
    val fontFamily: String = "SANS_SERIF",
    val textSize: Float = 14f,
    val border: BorderEstilo? = null
)
