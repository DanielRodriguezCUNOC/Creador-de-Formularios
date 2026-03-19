package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Orientacion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.SeccionFormulario

@Composable
fun RenderSeccion(seccion: SeccionFormulario) {
    // Aplicar escala a las dimensiones
    val widthEscalado = FormularioConstants.escalarDimension(seccion.width)
    val heightEscalado = FormularioConstants.escalarDimension(seccion.height)

    val modifier = Modifier
        .then(if (widthEscalado != null) Modifier.width(widthEscalado.dp) else Modifier.fillMaxWidth())
        .then(if (heightEscalado != null) Modifier.height(heightEscalado.dp) else Modifier.wrapContentHeight())
        .background(seccion.estilos.backgroundColor.toComposeColor())
        .let { seccion.estilos.applyBorder(it) }
        .padding(FormularioConstants.PADDING_ELEMENTO)

    if (seccion.orientacion == Orientacion.HORIZONTAL) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(FormularioConstants.SPACING_HORIZONTAL)
        ) {
            seccion.elementos.forEach { elemento ->
                Box(modifier = Modifier.weight(1f)) {
                    RenderElemento(elemento)
                }
            }
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(FormularioConstants.SPACING_VERTICAL)
        ) {
            seccion.elementos.forEach { elemento ->
                RenderElemento(elemento)
            }
        }
    }
}
