package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteSeccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteTabla
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteTexto
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteUI
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.NodoAtributo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaAbierta
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaDesplegable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaSeleccionUnica
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.PreguntaSeleccionadaMultiple
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoAccesoVariable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoExpresion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.NodoListaExpresiones
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
    private var entornoActual = TablaSimbolos(null)
    private val textSanitizer = PkmTextSanitizer()
    private val expressionWriter = PkmExpressionWriter(textSanitizer)
    private val expressionBuilder = ExpressionNodeBuilder(
        obtenerEntorno = { entornoActual },
        agregarErrorSemantico = { mensaje, linea, columna ->
            errores.add(ErrorInfo(TipoError.SEMANTICO, mensaje, linea, columna))
        }
    )
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
        entornoActual = TablaSimbolos(null)
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

    private fun evaluarExpresion(expresion: NodoExpresion): Any? {
        return when (expresion) {
            is NodoLiteral -> expressionBuilder.construirLiteral(expresion)
            is NodoAccesoVariable -> expressionBuilder.construirAccesoVariable(expresion)
            is NodoLlamadaApi -> expressionBuilder.construirLlamadaApi(expresion) { evaluarExpresion(it) }
            is NodoOperacionUnaria -> expressionBuilder.construirOperacionUnaria(expresion) { evaluarExpresion(it) }
            is NodoOperacionBinaria -> expressionBuilder.construirOperacionBinaria(expresion) { evaluarExpresion(it) }
            else -> null
        }
    }

    private fun crearScope(): TablaSimbolos = TablaSimbolos(entornoActual)

    private fun pushScope(nuevo: TablaSimbolos): TablaSimbolos {
        val anterior = entornoActual
        entornoActual = nuevo
        return anterior
    }

    private fun popScope(anterior: TablaSimbolos) {
        entornoActual = anterior
    }

    private fun valorDefecto(tipo: String): Any {
        return when (tipo) {
            "number" -> 0.0
            "string" -> ""
            else -> ""
        }
    }

    private fun materializarValor(valor: Any?): Any? {
        return when (valor) {
            is NodoExpresion -> evaluarExpresion(valor)
            is NodoAtributo -> NodoAtributo(valor.nombre, materializarValor(valor.valor) ?: "")
            is List<*> -> valor.map { item -> materializarValor(item) ?: "" }
            else -> valor
        }
    }

    private fun materializarAtributos(atributos: List<NodoAtributo>): List<NodoAtributo> {
        return atributos.map { attr ->
            NodoAtributo(attr.nombre, materializarValor(attr.valor) ?: "")
        }
    }

    private fun materializarComponente(componente: ComponenteUI): ComponenteUI {
        val attrs = materializarAtributos(componente.atributos)
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

    private fun reemplazarComodinesEnColor(colorRaw: String, state: WildcardState): String {
        var color = colorRaw
        while (color.contains('?')) {
            val parametro = siguienteParametro(state) ?: break
            val reemplazo = expressionWriter.expresionComoTexto(parametro)
            color = color.replaceFirst("?", reemplazo)
        }
        return color
    }

    private fun reemplazarComodinesEnValor(valor: Any, state: WildcardState): Any {
        return when (valor) {
            is NodoExpresion -> reemplazarComodinesEnExpresion(valor, state)
            is NodoAtributo -> NodoAtributo(valor.nombre, reemplazarComodinesEnValor(valor.valor, state))
            is List<*> -> valor.map { item -> if (item == null) "" else reemplazarComodinesEnValor(item, state) }
            is ComponenteUI -> reemplazarComodinesEnComponente(valor, state)
            else -> valor
        }
    }

    private fun reemplazarComodinesEnAtributos(attrs: List<NodoAtributo>, state: WildcardState): List<NodoAtributo> {
        return attrs.map { attr -> NodoAtributo(attr.nombre, reemplazarComodinesEnValor(attr.valor, state)) }
    }

    private fun reemplazarComodinesEnComponente(componente: ComponenteUI, state: WildcardState): ComponenteUI {
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

    override fun visit(node: NodoLiteral) {}

    override fun visit(node: NodoListaExpresiones): Unit {
        // Evaluables: recursión en elementos (sin hacer nada específico para PKM)
    }

    override fun visit(node: NodoOperacionBinaria) {}

    override fun visit(node: NodoAccesoVariable) {}

    override fun visit(node: NodoLlamadaApi) {}

    override fun visit(node: NodoOperacionUnaria) {}

    override fun visit(node: NodoDeclaracion) {
        val valor = node.valorInicio?.let { evaluarExpresion(it) } ?: valorDefecto(node.tipo)
        entornoActual.almacenarVariable(node.id, valor, node.tipo)
    }

    override fun visit(node: NodoDeclaracionSpecial) {
        especiales[node.id] = node.pregunta
    }

    override fun visit(node: NodoAsignacion) {
        val nuevoValor = evaluarExpresion(node.nuevoValor)
        try {
            entornoActual.reasignarVariable(node.id, nuevoValor ?: "")
        } catch (e: Exception) {
            errores.add(
                ErrorInfo(
                    TipoError.SEMANTICO,
                    e.message ?: "Error de asignación",
                    node.linea,
                    node.columna
                )
            )
        }
    }

    override fun visit(node: NodoSentenciaIf) {
        val condicion = expressionBuilder.toBool(evaluarExpresion(node.condicion))
        val nuevoEntorno = crearScope()
        val anterior = pushScope(nuevoEntorno)
        try {
            if (condicion) {
                for (instruccion in node.instruccionesIf) {
                    instruccion.accept(this)
                }
            } else {
                node.instruccionesElse?.forEach { instruccion ->
                    instruccion.accept(this)
                }
            }
        } finally {
            popScope(anterior)
        }
    }

    override fun visit(node: NodoCicloWhile) {
        var iteraciones = 0
        while (expressionBuilder.toBool(evaluarExpresion(node.condicion))) {
            iteraciones++
            if (iteraciones > MAX_ITERACIONES_CICLO) {
                break
            }

            val nuevoEntorno = crearScope()
            val anterior = pushScope(nuevoEntorno)
            try {
                for (instruccion in node.instruccionesWhile) {
                    instruccion.accept(this)
                }
            } finally {
                popScope(anterior)
            }
        }
    }

    override fun visit(node: NodoCicloDoWhile) {
        var iteraciones = 0
        do {
            iteraciones++
            if (iteraciones > MAX_ITERACIONES_CICLO) {
                break
            }

            val nuevoEntorno = crearScope()
            val anterior = pushScope(nuevoEntorno)
            try {
                for (instruccion in node.instrucciones) {
                    instruccion.accept(this)
                }
            } finally {
                popScope(anterior)
            }
        } while (expressionBuilder.toBool(evaluarExpresion(node.condicion)))
    }

    override fun visit(node: NodoCicloFor) {
        if (!node.esImperativo) {
            val inicio = expressionBuilder.toDouble(node.rangoInicio?.let { evaluarExpresion(it) })?.toInt() ?: 0
            val fin = expressionBuilder.toDouble(evaluarExpresion(node.rangoFin))?.toInt() ?: 0

            for (i in inicio..fin) {
                val nuevoEntorno = crearScope()
                if (node.idVariable != null) {
                    nuevoEntorno.almacenarVariable(node.idVariable, i.toDouble(), "number")
                }

                val anterior = pushScope(nuevoEntorno)
                try {
                    for (instruccion in node.instruccionesFor) {
                        instruccion.accept(this)
                    }
                } finally {
                    popScope(anterior)
                }
            }
            return
        }

        val nuevoEntorno = crearScope()
        val anterior = pushScope(nuevoEntorno)
        try {
            node.inicializacionImperativa?.accept(this)

            var iteraciones = 0
            while (expressionBuilder.toBool(evaluarExpresion(node.rangoFin))) {
                iteraciones++
                if (iteraciones > MAX_ITERACIONES_CICLO) {
                    break
                }

                for (instruccion in node.instruccionesFor) {
                    instruccion.accept(this)
                }
                node.actualizacionImperativa?.accept(this)
            }
        } finally {
            popScope(anterior)
        }
    }

    override fun visit(node: NodoDraw) {
        val componenteBase = especiales[node.idVariableEspecial]
        if (componenteBase == null) {
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

        val state = WildcardState(node.parametros)
        val componenteConComodines = reemplazarComodinesEnComponente(componenteBase, state)
        val componente = materializarComponente(componenteConComodines)

        statsCollector.registrarDraw()
        renderizarComponenteDesdeDraw(componente)
    }

    override fun visit(node: ComponenteSeccion) {
        val seccion = materializarComponente(node) as ComponenteSeccion
        val hijosInternos = recolectarNodos {
            for (interno in seccion.elementosInternos) {
                interno.accept(this)
            }
        }
        agregarNodos(componentWriter.crearSeccion(seccion, hijosInternos))
    }

    override fun visit(node: ComponenteTabla) {
        val tabla = materializarComponente(node) as ComponenteTabla
        val filasRenderizadas = mutableListOf<List<List<PkmTagNode>>>()

        for (fila in tabla.filas) {
            val celdasRenderizadas = mutableListOf<List<PkmTagNode>>()
            for (celda in fila) {
                if (celda is NodoLiteral && celda.tipo == "table_cell_component" && celda.valor is NodoInstruccion) {
                    val instruccionCelda = celda.valor as NodoInstruccion
                    val nodosCelda = recolectarNodos {
                        instruccionCelda.accept(this)
                    }
                    celdasRenderizadas.add(nodosCelda)
                } else {
                    val valorEvaluado = evaluarExpresion(celda)
                    val texto = if (valorEvaluado != null) {
                        expressionWriter.valorComoTexto(valorEvaluado)
                    } else {
                        expressionWriter.expresionComoTexto(celda)
                    }
                    celdasRenderizadas.add(listOf(PkmTextNode(texto)))
                }
            }
            filasRenderizadas.add(celdasRenderizadas)
        }

        agregarNodo(componentWriter.crearTabla(tabla, filasRenderizadas))
    }

    override fun visit(node: ComponenteTexto) {
        val texto = materializarComponente(node) as ComponenteTexto
        agregarNodo(componentWriter.crearTexto(texto))
    }

    override fun visit(node: PreguntaDesplegable) {
        val pregunta = materializarComponente(node) as PreguntaDesplegable
        agregarNodo(componentWriter.crearPreguntaDesplegable(pregunta))
    }

    override fun visit(node: PreguntaSeleccionUnica) {
        val pregunta = materializarComponente(node) as PreguntaSeleccionUnica
        agregarNodo(componentWriter.crearPreguntaSeleccion(pregunta))
    }

    override fun visit(node: PreguntaSeleccionadaMultiple) {
        val pregunta = materializarComponente(node) as PreguntaSeleccionadaMultiple
        agregarNodo(componentWriter.crearPreguntaMultiple(pregunta))
    }

    override fun visit(node: PreguntaAbierta) {
        val pregunta = materializarComponente(node) as PreguntaAbierta
        agregarNodo(componentWriter.crearPreguntaAbierta(pregunta))
    }

    companion object {
        private const val MAX_ITERACIONES_CICLO = 1_000
    }
}
