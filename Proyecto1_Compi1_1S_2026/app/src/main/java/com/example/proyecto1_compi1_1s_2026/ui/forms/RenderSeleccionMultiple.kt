package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaSeleccionMultiple

/**
 * Renderiza una [PreguntaSeleccionMultiple] con su etiqueta y checkboxes.
 * Las opciones seleccionadas se guardan de vuelta en el modelo.
 */
@Composable
fun RenderSeleccionMultiple(pregunta: PreguntaSeleccionMultiple) {
    // Mapa de estado: index → checked
    val seleccionadas = remember {
        mutableStateMapOf<Int, Boolean>().apply {
            pregunta.seleccionadas.forEach { put(it, true) }
        }
    }

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
                Checkbox(
                    checked         = seleccionadas[index] == true,
                    onCheckedChange = { checked ->
                        seleccionadas[index] = checked
                        if (checked) pregunta.seleccionadas.add(index)
                        else         pregunta.seleccionadas.remove(index)
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
