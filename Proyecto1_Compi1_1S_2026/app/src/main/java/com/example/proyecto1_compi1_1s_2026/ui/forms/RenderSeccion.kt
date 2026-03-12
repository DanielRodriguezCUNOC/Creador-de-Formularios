package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Orientacion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.SeccionFormulario

/**
 * Renderiza una [SeccionFormulario].
 *
 * - Si `width` y `height` están definidos, el contenedor usa ese tamaño fijo en dp.
 * - Si no, ocupa el ancho disponible y envuelve su contenido.
 * - La orientación determina si los hijos se apilan en [Column] o [Row].
 */
@Composable
fun RenderSeccion(seccion: SeccionFormulario) {
    val modifier = Modifier
        .then(
            if (seccion.width != null && seccion.height != null)
                Modifier.size(seccion.width.dp, seccion.height.dp)
            else
                Modifier.fillMaxWidth().wrapContentHeight()
        )
        .background(seccion.estilos.backgroundColor)
        .let { seccion.estilos.applyBorder(it) }
        .padding(8.dp)

    if (seccion.orientacion == Orientacion.VERTICAL) {
        Column(
            modifier            = modifier,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            seccion.elementos.forEach { RenderElemento(it) }
        }
    } else {
        Row(
            modifier                = modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement   = Arrangement.spacedBy(6.dp)
        ) {
            seccion.elementos.forEach { RenderElemento(it) }
        }
    }
}
