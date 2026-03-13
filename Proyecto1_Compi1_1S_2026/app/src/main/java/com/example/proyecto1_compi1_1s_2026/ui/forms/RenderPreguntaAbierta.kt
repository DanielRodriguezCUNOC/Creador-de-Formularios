package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaAbierta

/**
 * Renderiza una [PreguntaAbierta] con su etiqueta y un campo de texto libre.
 * La respuesta se guarda de vuelta en el modelo para poder recuperarla al enviar.
 */
@Composable
fun RenderPreguntaAbierta(pregunta: PreguntaAbierta) {
    var respuesta by remember { mutableStateOf(pregunta.respuesta) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(pregunta.estilos.backgroundColor)
            .let { pregunta.estilos.applyBorder(it) }
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
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
            placeholder = { Text("Escribe tu respuesta...") },
            singleLine  = false,
            minLines    = 2
        )
    }
}
