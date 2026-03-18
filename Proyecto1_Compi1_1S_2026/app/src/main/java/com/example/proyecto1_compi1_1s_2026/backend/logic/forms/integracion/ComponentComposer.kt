package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.integracion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.ElementoFormulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoDraw
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TipoError
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.UiNodeBuilder
import kotlin.getValue

/**
 * Compone y gestiona la construcción de elementos UI del formulario.
 */
class ComponentComposer(
    private val executionContext: ExecutionContext,
    private val evaluarExpresion: (Any?) -> Any?,
    private val erroresRef: MutableList<ErrorInfo>
) {
    private val elementos = mutableListOf<ElementoFormulario>()
    
    // Inicialización explícita para evitar errores de inferencia en el lazy
    private val uiBuilder: UiNodeBuilder by lazy {
        UiNodeBuilder { expr: NodoExpresion -> 
            evaluarExpresion(expr) 
        }
    }

    fun obtenerElementos(): List<ElementoFormulario> = elementos.toList()

    fun limpiarElementos() {
        elementos.clear()
    }

    fun agregarSeccion(node: ComponenteSeccion, elementosInternos: List<ElementoFormulario>) {
        val seccion = uiBuilder.construirSeccion(node, elementosInternos)
        elementos.add(seccion)
    }

    fun agregarTexto(node: ComponenteTexto) {
        val texto = uiBuilder.construirTexto(node)
        elementos.add(texto)
    }

    fun agregarPreguntaAbierta(node: PreguntaAbierta) {
        val pregunta = uiBuilder.construirPreguntaAbierta(node)
        elementos.add(pregunta)
    }

    fun agregarPreguntaDesplegable(node: PreguntaDesplegable) {
        val pregunta = uiBuilder.construirPreguntaDesplegable(node)
        elementos.add(pregunta)
    }

    fun agregarPreguntaSeleccionUnica(node: PreguntaSeleccionUnica) {
        val pregunta = uiBuilder.construirPreguntaSeleccionUnica(node)
        elementos.add(pregunta)
    }

    fun agregarPreguntaSeleccionMultiple(node: PreguntaSeleccionadaMultiple) {
        val pregunta = uiBuilder.construirPreguntaSeleccionMultiple(node)
        elementos.add(pregunta)
    }

    fun agregarTabla(node: ComponenteTabla, filasEvaluadas: List<List<ElementoFormulario>>) {
        val tabla = uiBuilder.construirTabla(node, filasEvaluadas)
        elementos.add(tabla)
    }

    fun procesarDraw(node: NodoDraw) {
        try {
            val elemento = executionContext.obtenerVariable(node.idVariableEspecial)
            if (elemento is ElementoFormulario) {
                elementos.add(elemento)
            }
        } catch (_: Exception) {
            erroresRef.add(
                ErrorInfo(
                    TipoError.SEMANTICO,
                    "Variable '${node.idVariableEspecial}' no declarada",
                    node.linea,
                    node.columna
                )
            )
        }
    }

    fun guardarEstadoElementos(): List<ElementoFormulario> = elementos.toList()

    fun restaurarEstadoElementos(estado: List<ElementoFormulario>) {
        elementos.clear()
        elementos.addAll(estado)
    }

    fun obtenerYLimpiarElementosNuevos(tamanoPrevio: Int): List<ElementoFormulario> {
        val nuevos = elementos.drop(tamanoPrevio)
        repeat(nuevos.size) {
            elementos.removeAt(elementos.lastIndex)
        }
        return nuevos
    }
}
