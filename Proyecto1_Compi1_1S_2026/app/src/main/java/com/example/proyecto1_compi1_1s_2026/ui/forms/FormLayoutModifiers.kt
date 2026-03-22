package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.applyFormDimensions(
    width: Float?,
    height: Float?,
    fillWidthByDefault: Boolean = true
): Modifier {
    val widthEscalado = FormularioConstants.escalarDimension(width)
    val heightEscalado = FormularioConstants.escalarDimension(height)

    return this
        .then(
            if (widthEscalado != null) {
                Modifier.width(widthEscalado.dp)
            } else if (fillWidthByDefault) {
                Modifier.fillMaxWidth()
            } else {
                Modifier
            }
        )
        .then(
            if (heightEscalado != null) {
                Modifier.height(heightEscalado.dp)
            } else {
                Modifier.wrapContentHeight()
            }
        )
}
