package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.*

/**
 * Composable raíz que renderiza un [Formulario] completo.
 * Muestra todos sus elementos en una columna con scroll vertical y añade
 * el botón "Enviar Formulario" al final.
 *
 * @param formulario  Modelo construido por el [Interprete].
 * @param onEnviar    Callback invocado al pulsar el botón de envío.
 */
@Composable
fun FormularioRenderer(
    formulario: Formulario,
    onEnviar: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        formulario.elementos.forEach { elemento ->
            RenderElemento(elemento)
        }

        // Botón de envío obligatorio por defecto
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick  = onEnviar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar Formulario")
        }
    }
}

/**
 * Dispatcher central: delega el renderizado al Composable específico
 * de cada tipo de [ElementoFormulario].
 */
@Composable
fun RenderElemento(elemento: ElementoFormulario) {
    when (elemento) {
        is SeccionFormulario       -> RenderSeccion(elemento)
        is TextoFormulario         -> RenderTexto(elemento)
        is PreguntaAbierta         -> RenderPreguntaAbierta(elemento)
        is PreguntaDesplegable     -> RenderPreguntaDesplegable(elemento)
        is PreguntaSeleccionUnica  -> RenderSeleccionUnica(elemento)
        is PreguntaSeleccionMultiple -> RenderSeleccionMultiple(elemento)
        is TablaFormulario         -> RenderTabla(elemento)
    }
}
