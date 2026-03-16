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
    private val nodosDocumento = mutableListOf<PkmTagNode>()
    private val pilaContenedores = ArrayDeque<MutableList<PkmTagNode>>()

    private val statsCollector = PkmStatsCollector()
    private val textSanitizer = PkmTextSanitizer()
    private val expressionWriter = PkmExpressionWriter(textSanitizer)
    private val metadataBuilder = PkmMetadataBuilder()
    private val tagTreeWriter = PkmTagTreeWriter()
    private val componentWriter = PkmComponentWriter(
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
        val cuerpo = tagTreeWriter.escribir(nodosDocumento)
        val codigoFinal = if (cuerpo.isBlank()) metadatos else metadatos + "\n\n" + cuerpo

        return ResultadoGeneracionPkm(codigoFinal, errores.toList())
    }

    private fun limpiarEstado() {
        errores.clear()
        especiales.clear()
        nodosDocumento.clear()
        pilaContenedores.clear()
        statsCollector.limpiar()
    }

    private fun agregarNodo(nodo: PkmTagNode) {
        if (pilaContenedores.isEmpty()) {
            nodosDocumento.add(nodo)
        } else {
            pilaContenedores.last().add(nodo)
        }
    }

    private fun agregarNodos(nodos: List<PkmTagNode>) {
        for (nodo in nodos) {
            agregarNodo(nodo)
        }
    }

    private fun recolectarNodos(bloque: () -> Unit): List<PkmTagNode> {
        val contenedor = mutableListOf<PkmTagNode>()
        pilaContenedores.addLast(contenedor)
        try {
            bloque()
        } finally {
            pilaContenedores.removeLast()
        }
        return contenedor
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
        statsCollector.registrarDraw()
        renderizarComponenteDesdeDraw(componente)
    }

    override fun visit(node: ComponenteSeccion) {
        val hijosInternos = recolectarNodos {
            for (interno in node.elementosInternos) {
                interno.accept(this)
            }
        }
        agregarNodos(componentWriter.crearSeccion(node, hijosInternos))
    }

    override fun visit(node: ComponenteTabla) {
        val filasRenderizadas = mutableListOf<List<List<PkmTagNode>>>()

        for (fila in node.filas) {
            val celdasRenderizadas = mutableListOf<List<PkmTagNode>>()
            for (celda in fila) {
                if (celda is NodoLiteral && celda.tipo == "table_cell_component" && celda.valor is ComponenteUI) {
                    val componente = celda.valor as ComponenteUI
                    val nodosCelda = recolectarNodos {
                        renderizarComponenteDesdeDraw(componente)
                    }
                    celdasRenderizadas.add(nodosCelda)
                } else {
                    celdasRenderizadas.add(listOf(PkmTextNode(expressionWriter.expresionComoTexto(celda))))
                }
            }
            filasRenderizadas.add(celdasRenderizadas)
        }

        agregarNodo(componentWriter.crearTabla(node, filasRenderizadas))
    }

    override fun visit(node: ComponenteTexto) {
        agregarNodo(componentWriter.crearTexto(node))
    }

    override fun visit(node: PreguntaDesplegable) {
        agregarNodo(componentWriter.crearPreguntaDesplegable(node))
    }

    override fun visit(node: PreguntaSeleccionUnica) {
        agregarNodo(componentWriter.crearPreguntaSeleccion(node))
    }

    override fun visit(node: PreguntaSeleccionadaMultiple) {
        agregarNodo(componentWriter.crearPreguntaMultiple(node))
    }

    override fun visit(node: PreguntaAbierta) {
        agregarNodo(componentWriter.crearPreguntaAbierta(node))
    }
}
