package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.TablaFormulario

/**
 * Renderiza una [TablaFormulario] como una cuadrícula de elementos.
 */
@Composable
fun RenderTabla(tabla: TablaFormulario) {
    val modifier = Modifier
        .then(if (tabla.width != null) Modifier.width(tabla.width.dp) else Modifier.fillMaxWidth())
        .then(if (tabla.height != null) Modifier.height(tabla.height.dp) else Modifier.wrapContentHeight())
        .background(tabla.estilos.backgroundColor.toComposeColor())
        .let { tabla.estilos.applyBorder(it) }
        .padding(4.dp)

    Column(modifier = modifier) {
        tabla.filas.forEach { fila ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                fila.forEach { celda ->
                    Box(modifier = Modifier.weight(1f)) {
                        RenderElemento(celda)
                    }
                }
            }
        }
    }
}
