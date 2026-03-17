package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.integracion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.ElementoFormulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.TextoFormulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoLiteral
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoDraw
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.ErrorInfo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.TipoError

/**
 * Compone y gestiona la construcción de elementos UI del formulario.
 * Responsable de mantener la lista de elementos y coordinar todas las operaciones
 * de creación de componentes (secciones, preguntas, tablas, etc.).
 *
 * GRASP Experto: Esta clase es experta en composición de UI porque:
 * - Mantiene el estado de elementos acumulados
 * - Decide cómo agregar, remover y reorganizar elementos
 * - Coordina la construcción de componentes anidados
 * - Conoce cómo el intérprete contribuye elementos a través del patrón Visitor
 */
class ComponentComposer(
    private val executionContext: ExecutionContext,
    private val evaluarExpresion: (Any?) -> Any?,
    private val erroresRef: MutableList<ErrorInfo>
) {
    private val elementos = mutableListOf<ElementoFormulario>()
    private val uiBuilder by lazy {
        UiNodeBuilder { expr -> expr }  // Será actualizado con el visitor real desde Interprete
    }

    // ─── Acceso a elementos ─────────────────────────────────────────────────

    fun obtenerElementos(): List<ElementoFormulario> = elementos.toList()

    fun limpiarElementos() {
        elementos.clear()
    }

    // ─── Composición de componentes UI ───────────────────────────────────────

    fun agregarSección(node: ComponenteSeccion, elementosInternos: List<ElementoFormulario>) {
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

    // ─── Manejo de draw() ────────────────────────────────────────────────────

    fun procesarDraw(node: NodoDraw) {
        try {
            val elemento = executionContext.obtenerVariable(node.idVariableEspecial)
            if (elemento is ElementoFormulario) {
                elementos.add(elemento)
            }
        } catch (e: Exception) {
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

    // ─── Aislamiento de scope para construcción anidada ─────────────────────

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
