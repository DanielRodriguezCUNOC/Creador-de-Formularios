package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.TablaFormulario

/**
 * Renderiza una [TablaFormulario] como una cuadrícula de elementos.
 */
@Composable
fun RenderTabla(tabla: TablaFormulario) {
    val modifier = Modifier
        .applyFormDimensions(tabla.width, tabla.height)
        .background(tabla.estilos.backgroundColor.toComposeColor())
        .let { tabla.estilos.applyBorder(it) }
        .padding(FormularioConstants.PADDING_TABLA)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FormularioConstants.SPACING_VERTICAL)
    ) {
        tabla.filas
            .flatMap { fila ->
                if (fila.size <= FormularioConstants.MAX_COLUMNAS_MOVIL) {
                    listOf(fila)
                } else {
                    fila.chunked(FormularioConstants.MAX_COLUMNAS_MOVIL)
                }
            }
            .forEach { filaRender ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(FormularioConstants.SPACING_HORIZONTAL)
                ) {
                    filaRender.forEach { celda ->
                        Box(modifier = Modifier
                            .weight(1f)
                            .heightIn(min = FormularioConstants.MIN_ALTURA_CELDA_TABLA)) {
                            RenderElemento(celda)
                        }
                    }

                    repeat(FormularioConstants.MAX_COLUMNAS_MOVIL - filaRender.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
    }
}
