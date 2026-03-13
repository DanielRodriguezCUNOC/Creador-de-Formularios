package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaSeleccionUnica

/**
 * Renderiza una [PreguntaSeleccionUnica] con su etiqueta y botones de radio.
 * La opción seleccionada se guarda de vuelta en el modelo.
 */
@Composable
fun RenderSeleccionUnica(pregunta: PreguntaSeleccionUnica) {
    var seleccionada by remember { mutableIntStateOf(pregunta.seleccionada) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(pregunta.estilos.backgroundColor)
            .let { pregunta.estilos.applyBorder(it) }
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text  = pregunta.label,
            style = pregunta.estilos.toTextStyle()
        )

        pregunta.opciones.forEachIndexed { index, opcion ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = seleccionada == index,
                    onClick  = {
                        seleccionada          = index
                        pregunta.seleccionada = index
                    }
                )
                Text(
                    text     = opcion,
                    style    = pregunta.estilos.toTextStyle(),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}
