package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.TextoFormulario

/**
 * Renderiza un [TextoFormulario] (etiqueta o contenido estático).
 */
@Composable
fun RenderTexto(texto: TextoFormulario) {
    Text(
        text  = texto.contenido,
        style = texto.estilos.toTextStyle(),
        modifier = Modifier
            .applyFormDimensions(texto.width, texto.height)
            .background(texto.estilos.backgroundColor.toComposeColor())
            .let { texto.estilos.applyBorder(it) }
            .padding(FormularioConstants.PADDING_INTERNO)
    )
}
