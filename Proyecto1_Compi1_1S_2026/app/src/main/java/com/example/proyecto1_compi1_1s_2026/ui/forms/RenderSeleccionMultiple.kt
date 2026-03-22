package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaSeleccionMultiple

/**
 * Renderiza una [PreguntaSeleccionMultiple] con su etiqueta y una lista de casillas de verificación.
 */
@Composable
fun RenderSeleccionMultiple(pregunta: PreguntaSeleccionMultiple) {
    val seleccionadas = remember { mutableStateListOf<Int>().apply { addAll(pregunta.seleccionadas) } }

    Column(
        modifier = Modifier
            .applyFormDimensions(pregunta.width, pregunta.height, fillWidthByDefault = true)
            .background(pregunta.estilos.backgroundColor.toComposeColor())
            .let { pregunta.estilos.applyBorder(it) }
            .padding(FormularioConstants.PADDING_INTERNO),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text  = pregunta.label,
            style = pregunta.estilos.toTextStyle(),
            modifier = Modifier.padding(bottom = 2.dp)
        )
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
            pregunta.opciones.forEachIndexed { index, opcion ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier         = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                ) {
                    Checkbox(
                        checked        = seleccionadas.contains(index),
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                seleccionadas.add(index)
                                pregunta.seleccionadas.add(index)
                            } else {
                                seleccionadas.remove(index)
                                pregunta.seleccionadas.remove(index)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = opcion)
                }
            }
        }
    }
}
