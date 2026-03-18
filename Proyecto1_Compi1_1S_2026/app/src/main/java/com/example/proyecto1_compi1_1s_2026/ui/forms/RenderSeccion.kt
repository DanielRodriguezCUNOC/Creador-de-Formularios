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
    val modifier = Modifier
        .then(if (seccion.width != null) Modifier.width(seccion.width.dp) else Modifier.fillMaxWidth())
        .then(if (seccion.height != null) Modifier.height(seccion.height.dp) else Modifier.wrapContentHeight())
        .background(seccion.estilos.backgroundColor.toComposeColor())
        .let { seccion.estilos.applyBorder(it) }
        .padding(8.dp)

    if (seccion.orientacion == Orientacion.HORIZONTAL) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            seccion.elementos.forEach { elemento ->
                RenderElemento(elemento)
            }
        }
    }
}
