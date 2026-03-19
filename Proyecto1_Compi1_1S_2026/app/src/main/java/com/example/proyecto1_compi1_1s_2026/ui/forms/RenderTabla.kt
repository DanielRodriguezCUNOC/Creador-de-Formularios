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
    // Aplicar escala a las dimensiones
    val widthEscalado = FormularioConstants.escalarDimension(tabla.width)
    val heightEscalado = FormularioConstants.escalarDimension(tabla.height)

    val modifier = Modifier
        .then(if (widthEscalado != null) Modifier.width(widthEscalado.dp) else Modifier.fillMaxWidth())
        .then(if (heightEscalado != null) Modifier.height(heightEscalado.dp) else Modifier.wrapContentHeight())
        .background(tabla.estilos.backgroundColor.toComposeColor())
        .let { tabla.estilos.applyBorder(it) }
        .padding(FormularioConstants.PADDING_TABLA)

    Column(modifier = modifier) {
        tabla.filas.forEach { fila ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(FormularioConstants.SPACING_HORIZONTAL)
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
