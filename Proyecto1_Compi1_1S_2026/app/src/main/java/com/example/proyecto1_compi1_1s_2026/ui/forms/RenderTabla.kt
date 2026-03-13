package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.TablaFormulario

/**
 * Renderiza una [TablaFormulario] como una cuadrícula de filas y celdas.
 * Cada celda puede contener cualquier [ElementoFormulario].
 * Si `width` y `height` están definidos, el contenedor usa ese tamaño fijo.
 */
@Composable
fun RenderTabla(tabla: TablaFormulario) {
    val modifier = Modifier
        .then(
            if (tabla.width != null && tabla.height != null)
                Modifier.size(tabla.width.dp, tabla.height.dp)
            else
                Modifier.fillMaxWidth().wrapContentHeight()
        )
        .background(tabla.estilos.backgroundColor)
        .let { tabla.estilos.applyBorder(it) }
        .horizontalScroll(rememberScrollState())

    Column(modifier = modifier) {
        tabla.filas.forEach { fila ->
            Row(
                modifier      = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                fila.forEach { celda ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(0.5.dp, Color.Gray)
                            .padding(6.dp)
                    ) {
                        RenderElemento(celda)
                    }
                }
            }
        }
    }
}
