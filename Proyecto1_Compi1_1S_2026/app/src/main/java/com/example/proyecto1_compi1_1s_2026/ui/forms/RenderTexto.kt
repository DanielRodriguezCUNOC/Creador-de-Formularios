package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.TextoFormulario

/**
 * Renderiza un [TextoFormulario] aplicando los estilos definidos
 * (color, fuente, tamaño, fondo y borde).
 */
@Composable
fun RenderTexto(texto: TextoFormulario) {
    val modifier = Modifier
        .background(texto.estilos.backgroundColor)
        .let { texto.estilos.applyBorder(it) }
        .padding(4.dp)

    Text(
        text     = texto.contenido,
        style    = texto.estilos.toTextStyle(),
        modifier = modifier
    )
}
