package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.integracion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.ElementoFormulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoAccesoVariable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoLiteral
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoLlamadaApi
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoOperacionBinaria
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoOperacionUnaria
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

    private class WildcardState(
        val parametros: List<NodoExpresion>,
        var indice: Int = 0
    )

    private fun siguienteParametro(state: WildcardState): NodoExpresion? {
        val parametro = state.parametros.getOrNull(state.indice)
        if (parametro != null) {
            state.indice++
        }
        return parametro
    }

    private fun reemplazarComodinesEnColor(colorRaw: String, state: WildcardState): String {
        var color = colorRaw
        while (color.contains('?')) {
            val parametro = siguienteParametro(state)
            val reemplazo = if (parametro != null) {
                evaluarExpresion(parametro)?.toString() ?: ""
            } else {
                ""
            }
            color = color.replaceFirst("?", reemplazo)
        }
        return color
    }

    private fun reemplazarComodinesEnExpresion(exp: NodoExpresion, state: WildcardState): NodoExpresion {
        return when (exp) {
            is NodoLiteral -> {
                if (exp.tipo == "comodin") {
                    siguienteParametro(state) ?: exp
                } else if (exp.tipo == "color" && exp.valor.toString().contains('?')) {
                    val reemplazado = reemplazarComodinesEnColor(exp.valor.toString(), state)
                    NodoLiteral(reemplazado, exp.tipo, exp.linea, exp.columna)
                } else {
                    exp
                }
            }
            is NodoOperacionBinaria -> NodoOperacionBinaria(
                reemplazarComodinesEnExpresion(exp.izq, state),
                exp.operador,
                reemplazarComodinesEnExpresion(exp.der, state),
                exp.linea,
                exp.columna
            )
            is NodoOperacionUnaria -> NodoOperacionUnaria(
                exp.operador,
                reemplazarComodinesEnExpresion(exp.expresion, state),
                exp.linea,
                exp.columna
            )
            is NodoLlamadaApi -> NodoLlamadaApi(
                exp.tipo,
                reemplazarComodinesEnExpresion(exp.rangoInicio, state),
                reemplazarComodinesEnExpresion(exp.rangoFin, state),
                exp.linea,
                exp.columna
            )
            is NodoAccesoVariable -> exp
            else -> exp
        }
    }

    private fun reemplazarComodinesEnValor(valor: Any, state: WildcardState): Any {
        return when (valor) {
            is NodoExpresion -> reemplazarComodinesEnExpresion(valor, state)
            is NodoAtributo -> NodoAtributo(valor.nombre, reemplazarComodinesEnValor(valor.valor, state))
            is List<*> -> valor.map { item -> if (item == null) "" else reemplazarComodinesEnValor(item, state) }
            else -> valor
        }
    }

    private fun reemplazarComodinesEnAtributos(attrs: List<NodoAtributo>, state: WildcardState): List<NodoAtributo> {
        return attrs.map { attr -> NodoAtributo(attr.nombre, reemplazarComodinesEnValor(attr.valor, state)) }
    }

    private fun reemplazarComodinesEnComponente(componente: ComponenteUI, parametros: List<NodoExpresion>): ComponenteUI {
        val state = WildcardState(parametros)
        val attrs = reemplazarComodinesEnAtributos(componente.atributos, state)
        return when (componente) {
            is PreguntaAbierta -> PreguntaAbierta(attrs, componente.linea, componente.columna)
            is PreguntaDesplegable -> PreguntaDesplegable(attrs, componente.linea, componente.columna)
            is PreguntaSeleccionUnica -> PreguntaSeleccionUnica(attrs, componente.linea, componente.columna)
            is PreguntaSeleccionadaMultiple -> PreguntaSeleccionadaMultiple(attrs, componente.linea, componente.columna)
            is ComponenteTexto -> ComponenteTexto(attrs, componente.linea, componente.columna)
            is ComponenteTabla -> ComponenteTabla(attrs, componente.filas, componente.linea, componente.columna)
            is ComponenteSeccion -> ComponenteSeccion(attrs, componente.elementosInternos, componente.linea, componente.columna)
            else -> componente
        }
    }

    private fun renderizarTemplate(componente: ComponenteUI, draw: NodoDraw) {
        val componenteRender = reemplazarComodinesEnComponente(componente, draw.parametros)
        when (componenteRender) {
            is PreguntaAbierta -> agregarPreguntaAbierta(componenteRender)
            is PreguntaDesplegable -> agregarPreguntaDesplegable(componenteRender)
            is PreguntaSeleccionUnica -> agregarPreguntaSeleccionUnica(componenteRender)
            is PreguntaSeleccionadaMultiple -> agregarPreguntaSeleccionMultiple(componenteRender)
            is ComponenteTexto -> agregarTexto(componenteRender)
            is ComponenteTabla -> elementos.add(uiBuilder.construirTabla(componenteRender))
            is ComponenteSeccion -> elementos.add(uiBuilder.construirSeccion(componenteRender, emptyList()))
        }
    }

    fun procesarDraw(node: NodoDraw) {
        try {
            val elemento = executionContext.obtenerVariable(node.idVariableEspecial)
            when (elemento) {
                is ElementoFormulario -> elementos.add(elemento)
                is ComponenteUI -> renderizarTemplate(elemento, node)
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
