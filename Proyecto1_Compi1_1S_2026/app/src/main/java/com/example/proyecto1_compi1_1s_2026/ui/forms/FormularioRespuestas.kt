package com.example.proyecto1_compi1_1s_2026.ui.forms

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.ElementoFormulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Formulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaAbierta
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaDesplegable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaSeleccionMultiple
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaSeleccionUnica
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.SeccionFormulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.TablaFormulario

/**
 * Respuesta normalizada de un componente de entrada.
 * key usa una ruta de posicion para mantener identidad en ausencia de id en el modelo.
 */
data class RespuestaFormulario(
    val key: String,
    val tipo: String,
    val label: String,
    val valor: String
)

fun recolectarRespuestas(formulario: Formulario): List<RespuestaFormulario> {
    val respuestas = mutableListOf<RespuestaFormulario>()

    formulario.elementos.forEachIndexed { index, elemento ->
        recolectarElemento(
            elemento = elemento,
            path = "root.$index",
            sink = respuestas
        )
    }

    return respuestas
}

private fun recolectarElemento(
    elemento: ElementoFormulario,
    path: String,
    sink: MutableList<RespuestaFormulario>
) {
    when (elemento) {
        is SeccionFormulario -> {
            elemento.elementos.forEachIndexed { index, hijo ->
                recolectarElemento(hijo, "$path.section.$index", sink)
            }
        }

        is TablaFormulario -> {
            elemento.filas.forEachIndexed { filaIndex, fila ->
                fila.forEachIndexed { colIndex, celda ->
                    recolectarElemento(celda, "$path.table.$filaIndex.$colIndex", sink)
                }
            }
        }

        is PreguntaAbierta -> {
            sink.add(
                RespuestaFormulario(
                    key = path,
                    tipo = "pregunta_abierta",
                    label = elemento.label,
                    valor = elemento.respuesta
                )
            )
        }

        is PreguntaDesplegable -> {
            val valor = elemento.opciones.getOrNull(elemento.seleccionada).orEmpty()
            sink.add(
                RespuestaFormulario(
                    key = path,
                    tipo = "pregunta_desplegable",
                    label = elemento.label,
                    valor = valor
                )
            )
        }

        is PreguntaSeleccionUnica -> {
            val valor = elemento.opciones.getOrNull(elemento.seleccionada).orEmpty()
            sink.add(
                RespuestaFormulario(
                    key = path,
                    tipo = "pregunta_seleccion_unica",
                    label = elemento.label,
                    valor = valor
                )
            )
        }

        is PreguntaSeleccionMultiple -> {
            val valor = elemento.seleccionadas
                .toList()
                .sorted()
                .mapNotNull { index -> elemento.opciones.getOrNull(index) }
                .joinToString(separator = " | ")

            sink.add(
                RespuestaFormulario(
                    key = path,
                    tipo = "pregunta_seleccion_multiple",
                    label = elemento.label,
                    valor = valor
                )
            )
        }

        else -> Unit
    }
}
