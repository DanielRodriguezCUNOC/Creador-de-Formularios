package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaDesplegable

/**
 * Renderiza una [PreguntaDesplegable] con su etiqueta y un menú desplegable.
 * La opción seleccionada se guarda de vuelta en el modelo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenderPreguntaDesplegable(pregunta: PreguntaDesplegable) {
    var expanded    by remember { mutableStateOf(false) }
    var seleccionada by remember { mutableIntStateOf(pregunta.seleccionada) }
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

        ExposedDropdownMenuBox(
            expanded        = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value         = if (seleccionada >= 0) pregunta.opciones[seleccionada] else "Selecciona una opción...",
                onValueChange = {},
                readOnly      = true,
                textStyle     = pregunta.estilos.toTextStyle(),
                modifier      = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedContainerColor = backgroundColor,
                    unfocusedContainerColor = backgroundColor,
                    focusedBorderColor = textColor,
                    unfocusedBorderColor = textColor,
                    focusedTrailingIconColor = textColor,
                    unfocusedTrailingIconColor = textColor
                )
            )
            ExposedDropdownMenu(
                expanded        = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(backgroundColor)
            ) {
                pregunta.opciones.forEachIndexed { index, opcion ->
                    DropdownMenuItem(
                        text    = { Text(opcion, style = pregunta.estilos.toTextStyle()) },
                        colors = MenuDefaults.itemColors(
                            textColor = textColor
                        ),
                        onClick = {
                            seleccionada          = index
                            pregunta.seleccionada = index
                            expanded              = false
                        }
                    )
                }
            }
        }
    }
}
