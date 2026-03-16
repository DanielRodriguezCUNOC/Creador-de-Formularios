package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteSeccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteTabla
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteTexto
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteUI
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaAbierta
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaDesplegable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaSeleccionUnica
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaSeleccionadaMultiple
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoAccesoVariable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoLiteral
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoLlamadaApi
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoOperacionBinaria
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoOperacionUnaria
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoAsignacion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoCicloDoWhile
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoCicloFor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoCicloWhile
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoDeclaracion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoDeclaracionSpecial
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoDraw
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoInstruccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoSentenciaIf
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

// Genera el contenido base del archivo .pkm usando el AST validado.

class GeneradorPkm(private val autorPorDefecto: String = "Sistema") : Visitor<Unit> {

    private val errores = mutableListOf<ErrorInfo>()
    private val especiales = mutableMapOf<String, ComponenteUI>()

    private val lineWriter = PkmLineWriter()
    private val statsCollector = PkmStatsCollector()
    private val textSanitizer = PkmTextSanitizer()
    private val expressionWriter = PkmExpressionWriter(textSanitizer)
    private val metadataBuilder = PkmMetadataBuilder()
    private val componentWriter = PkmComponentWriter(
        lineWriter = lineWriter,
        expressionWriter = expressionWriter,
        stats = statsCollector
    )

    fun generar(instrucciones: List<NodoInstruccion>, autor: String? = null): ResultadoGeneracionPkm {
        limpiarEstado()

        for (instruccion in instrucciones) {
            instruccion.accept(this)
        }

        val stats = statsCollector.snapshot()
        val metadatos = metadataBuilder.construir(autor ?: autorPorDefecto, stats)
        val cuerpo = lineWriter.obtenerTexto()
        val codigoFinal = if (cuerpo.isBlank()) metadatos else metadatos + "\n\n" + cuerpo

        return ResultadoGeneracionPkm(codigoFinal, errores.toList())
    }

    private fun limpiarEstado() {
        errores.clear()
        especiales.clear()
        lineWriter.limpiar()
        statsCollector.limpiar()
    }

    private fun renderizarComponenteDesdeDraw(componente: ComponenteUI) {
        when (componente) {
            is PreguntaAbierta -> visit(componente)
            is PreguntaDesplegable -> visit(componente)
            is PreguntaSeleccionUnica -> visit(componente)
            is PreguntaSeleccionadaMultiple -> visit(componente)
            is ComponenteTexto -> visit(componente)
            is ComponenteTabla -> visit(componente)
            is ComponenteSeccion -> visit(componente)
        }
    }

    override fun visit(node: NodoLiteral) {}

    override fun visit(node: NodoOperacionBinaria) {}

    override fun visit(node: NodoAccesoVariable) {}

    override fun visit(node: NodoLlamadaApi) {}

    override fun visit(node: NodoOperacionUnaria) {}

    override fun visit(node: NodoDeclaracion) {
        
    }

    override fun visit(node: NodoDeclaracionSpecial) {
        especiales[node.id] = node.pregunta
    }

    override fun visit(node: NodoAsignacion) {
       
    }

    override fun visit(node: NodoSentenciaIf) {
       
    }

    override fun visit(node: NodoCicloWhile) {
        .
    }

    override fun visit(node: NodoCicloDoWhile) {
       
    }

    override fun visit(node: NodoCicloFor) {
       
    }

    override fun visit(node: NodoDraw) {
        val componente = especiales[node.idVariableEspecial]
        if (componente == null) {
            errores.add(
                ErrorInfo(
                    TipoError.SEMANTICO,
                    "No se puede generar .pkm: la variable special '${node.idVariableEspecial}' no existe para draw()",
                    node.linea,
                    node.columna
                )
            )
            return
        }
        renderizarComponenteDesdeDraw(componente)
    }

    override fun visit(node: ComponenteSeccion) {
        componentWriter.escribirSeccion(node) { instruccion ->
            instruccion.accept(this)
        }
    }

    override fun visit(node: ComponenteTabla) {
        componentWriter.escribirTabla(node) { componente ->
            renderizarComponenteDesdeDraw(componente)
        }
    }

    override fun visit(node: ComponenteTexto) {
        componentWriter.escribirTexto(node)
    }

    override fun visit(node: PreguntaDesplegable) {
        componentWriter.escribirPreguntaDesplegable(node)
    }

    override fun visit(node: PreguntaSeleccionUnica) {
        componentWriter.escribirPreguntaSeleccion(node)
    }

    override fun visit(node: PreguntaSeleccionadaMultiple) {
        componentWriter.escribirPreguntaMultiple(node)
    }

    override fun visit(node: PreguntaAbierta) {
        componentWriter.escribirPreguntaAbierta(node)
    }
}
