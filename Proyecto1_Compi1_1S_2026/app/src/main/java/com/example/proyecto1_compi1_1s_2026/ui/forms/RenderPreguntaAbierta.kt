package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaAbierta

/**
 * Renderiza una [PreguntaAbierta] con su etiqueta y un campo de texto libre.
 * La respuesta se guarda de vuelta en el modelo para poder recuperarla al enviar.
 */
@Composable
fun RenderPreguntaAbierta(pregunta: PreguntaAbierta) {
    var respuesta by remember { mutableStateOf(pregunta.respuesta) }
    val textColor = pregunta.estilos.color.toComposeColor()
    val backgroundColor = pregunta.estilos.backgroundColor.toComposeColor()

    Column(
        modifier = Modifier
            .applyFormDimensions(pregunta.width, pregunta.height)
            .background(pregunta.estilos.backgroundColor.toComposeColor())
            .let { pregunta.estilos.applyBorder(it) }
            .padding(FormularioConstants.PADDING_INTERNO),
        verticalArrangement = Arrangement.spacedBy(FormularioConstants.SPACING_VERTICAL)
    ) {
        Text(
            text  = pregunta.label,
            style = pregunta.estilos.toTextStyle()
        )
        OutlinedTextField(
            value         = respuesta,
            onValueChange = {
                respuesta          = it
                pregunta.respuesta = it
            },
            modifier    = Modifier.fillMaxWidth(),
            textStyle   = pregunta.estilos.toTextStyle(),
            placeholder = { Text("Escribe tu respuesta...", color = textColor.copy(alpha = 0.6f)) },
            singleLine  = false,
            minLines    = 2,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                focusedBorderColor = textColor,
                unfocusedBorderColor = textColor,
                cursorColor = textColor,
                focusedPlaceholderColor = textColor.copy(alpha = 0.6f),
                unfocusedPlaceholderColor = textColor.copy(alpha = 0.6f)
            )
        )
    }
}
