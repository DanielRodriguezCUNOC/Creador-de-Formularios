package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
                // En móviles, usamos fillMaxWidth para que tome el espacio de la celda/columna,
                // usando el width del DSL solo como mínimo si es necesario
                Modifier.widthIn(min = widthEscalado.dp).fillMaxWidth()
            } else if (fillWidthByDefault) {
                Modifier.fillMaxWidth()
            } else {
                Modifier
            }
        )
        .then(
            if (heightEscalado != null) {
                Modifier.heightIn(min = heightEscalado.dp)
            } else {
                Modifier.wrapContentHeight()
            }
        )
}
