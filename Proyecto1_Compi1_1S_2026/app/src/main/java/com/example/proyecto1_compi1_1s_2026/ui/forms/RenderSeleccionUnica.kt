package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaSeleccionUnica

/**
 * Renderiza una [PreguntaSeleccionUnica] con su etiqueta y botones de radio.
 */
@Composable
fun RenderSeleccionUnica(pregunta: PreguntaSeleccionUnica) {
    var seleccionada by remember { mutableIntStateOf(pregunta.seleccionada) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(pregunta.estilos.backgroundColor.toComposeColor())
            .let { pregunta.estilos.applyBorder(it) }
            .padding(FormularioConstants.PADDING_INTERNO),
        verticalArrangement = Arrangement.spacedBy(FormularioConstants.SPACING_VERTICAL)
    ) {
        Text(
            text  = pregunta.label,
            style = pregunta.estilos.toTextStyle()
        )
        pregunta.opciones.forEachIndexed { index, opcion ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier         = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected      = seleccionada == index,
                    onClick       = {
                        seleccionada          = index
                        pregunta.seleccionada = index
                    }
                )
                Text(text = opcion)
            }
        }
    }
}
