package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.integracion.ExecutionContext
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.integracion.ComponentComposer
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.ElementoFormulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Formulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.TextoFormulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

/**
 * Intérprete del AST. Implementa [Visitor]<Any?> para orquestar la ejecución
 * delegando a especialistas: ExecutionContext (variables/scopes) y ComponentComposer (UI).
 *
 * GRASP Controlador: Coordina el flujo de ejecución sin mezclar responsabilidades.
 *
 * Los componentes UI añaden el [ElementoFormulario] construido a través de ComponentComposer
 * lo que permite componerlos dentro de secciones anidadas.
 */
class Interprete(entornoActual: TablaSimbolos) : Visitor<Any?> {

    private val errores = mutableListOf<ErrorInfo>()
    private val executionContext = ExecutionContext(entornoActual, errores)
    private val composer by lazy {
        ComponentComposer(executionContext, { expr ->
            if (expr is NodoExpresion) expr.accept(this) else expr
        }, errores)
    }
    private val uiBuilder by lazy {
        UiNodeBuilder { expr -> expr.accept(this) }
    }
    private val exprBuilder by lazy {
        ExpressionNodeBuilder(
            obtenerEntorno = { executionContext.getEntornoActual() },
            agregarErrorSemantico = { mensaje, linea, columna ->
                errores.add(ErrorInfo(TipoError.SEMANTICO, mensaje, linea, columna))
            }
        )
    }

    // ─── Punto de entrada ────────────────────────────────────────────────────

    fun interpretar(instrucciones: List<NodoInstruccion>): ResultadoInterpretacion {
        composer.limpiarElementos()
        errores.clear()
        for (instruccion in instrucciones) {
            instruccion.accept(this)
        }
        return ResultadoInterpretacion(
            formulario = Formulario(composer.obtenerElementos()),
            errores    = errores.toList()
        )
    }

    // ─── Expresiones ─────────────────────────────────────────────────────────

    override fun visit(node: NodoLiteral): Any? = exprBuilder.construirLiteral(node)

    override fun visit(node: NodoAccesoVariable): Any? {
        return exprBuilder.construirAccesoVariable(node)
    }

    override fun visit(node: NodoLlamadaApi): Any? {
        return exprBuilder.construirLlamadaApi(node) { expr -> expr.accept(this) }
    }

    override fun visit(node: NodoOperacionUnaria): Any? {
        return exprBuilder.construirOperacionUnaria(node) { expr -> expr.accept(this) }
    }

    override fun visit(node: NodoOperacionBinaria): Any? {
        return exprBuilder.construirOperacionBinaria(node) { expr -> expr.accept(this) }
    }

    // ─── Instrucciones de variables ──────────────────────────────────────────

    override fun visit(node: NodoDeclaracion): Any? {
        val valor = if (node.valorInicio != null) {
            node.valorInicio.accept(this)
        } else {
            null
        }
        executionContext.declararVariable(node.id, valor, node.tipo)
        return null
    }

    override fun visit(node: NodoDeclaracionSpecial): Any? {
        // Guardar la plantilla del componente special; draw(...) aplicará parámetros/comodines.
        executionContext.almacenarVariableEspecial(node.id, node.pregunta)
        return null
    }

    override fun visit(node: NodoAsignacion): Any? {
        val valor = node.nuevoValor.accept(this)
        try {
            executionContext.reasignarVariable(node.id, valor)
        } catch (e: Exception) {
            errores.add(ErrorInfo(TipoError.SEMANTICO, e.message ?: "Error de asignación", node.linea, node.columna))
        }
        return null
    }

    // ─── Instrucciones de control de flujo ──────────────────────────────────

    override fun visit(node: NodoSentenciaIf): Any? {
        val condicion = exprBuilder.toBool(node.condicion.accept(this))
        val nuevoEntorno = executionContext.crearNuevoScope()
        val entornoAnterior = executionContext.getEntornoActual()
        executionContext.pushScope(nuevoEntorno)
        
        if (condicion) {
            for (instruccion in node.instruccionesIf) {
                instruccion.accept(this)
            }
        } else if (node.instruccionesElse != null) {
            for (instruccion in node.instruccionesElse) {
                instruccion.accept(this)
            }
        }
        
        executionContext.popScope(entornoAnterior)
        return null
    }

    override fun visit(node: NodoCicloWhile): Any? {
        var iter = 0
        while (exprBuilder.toBool(node.condicion.accept(this))) {
            if (++iter > MAX_ITER) {
                break
            }
            val nuevoEntorno = executionContext.crearNuevoScope()
            val entornoAnterior = executionContext.getEntornoActual()
            executionContext.pushScope(nuevoEntorno)
            
            for (instruccion in node.instruccionesWhile) {
                instruccion.accept(this)
            }
            
            executionContext.popScope(entornoAnterior)
        }
        return null
    }

    override fun visit(node: NodoCicloDoWhile): Any? {
        var iter = 0
        do {
            if (++iter > MAX_ITER) {
                break
            }
            val nuevoEntorno = executionContext.crearNuevoScope()
            val entornoAnterior = executionContext.getEntornoActual()
            executionContext.pushScope(nuevoEntorno)
            
            for (instruccion in node.instrucciones) {
                instruccion.accept(this)
            }
            
            executionContext.popScope(entornoAnterior)
        } while (exprBuilder.toBool(node.condicion.accept(this)))
        return null
    }

    override fun visit(node: NodoCicloFor): Any? {
        if (!node.esImperativo) {
            // FOR clásica: FOR ID IN inicio..fin { }
            val inicio = exprBuilder.toDouble(node.rangoInicio?.accept(this))?.toInt() ?: 0
            val fin    = exprBuilder.toDouble(node.rangoFin.accept(this))?.toInt() ?: 0
            for (i in inicio..fin) {
                val nuevoEntorno = executionContext.crearNuevoScope()
                if (node.idVariable != null) {
                    nuevoEntorno.almacenarVariable(node.idVariable, i.toDouble(), "number")
                }
                val entornoAnterior = executionContext.getEntornoActual()
                executionContext.pushScope(nuevoEntorno)
                
                for (instruccion in node.instruccionesFor) {
                    instruccion.accept(this)
                }
                
                executionContext.popScope(entornoAnterior)
            }
        } else {
            // FOR imperativa: FOR (i = init; cond; i = inc) { }
            val nuevoEntorno = executionContext.crearNuevoScope()
            val entornoAnterior = executionContext.getEntornoActual()
            executionContext.pushScope(nuevoEntorno)

            node.inicializacionImperativa?.accept(this)
            
            var iter = 0
            while (exprBuilder.toBool(node.rangoFin.accept(this))) {
                if (++iter > MAX_ITER) {
                    break
                }
                for (instruccion in node.instruccionesFor) {
                    instruccion.accept(this)
                }
                node.actualizacionImperativa?.accept(this)
            }
            
            executionContext.popScope(entornoAnterior)
        }
        return null
    }

    /** Recupera la variable special y añade el [ElementoFormulario] almacenado al formulario. */
    override fun visit(node: NodoDraw): Any? {
        composer.procesarDraw(node)
        return null
    }

    // ─── Componentes UI ──────────────────────────────────────────────────────

    override fun visit(node: ComponenteSeccion): Any? {
        // Recolectar los elementos internos de forma aislada
        val elementosPrevios = composer.guardarEstadoElementos()
        composer.limpiarElementos()
        for (interno in node.elementosInternos) {
            interno.accept(this)
        }
        val internos = composer.obtenerElementos()
        composer.limpiarElementos()
        composer.restaurarEstadoElementos(elementosPrevios)

        composer.agregarSeccion(node, internos)
        return internos.firstOrNull()
    }

    override fun visit(node: ComponenteTexto): Any? {
        composer.agregarTexto(node)
        return composer.obtenerElementos().lastOrNull()
    }

    override fun visit(node: PreguntaAbierta): Any? {
        composer.agregarPreguntaAbierta(node)
        return composer.obtenerElementos().lastOrNull()
    }

    override fun visit(node: PreguntaDesplegable): Any? {
        composer.agregarPreguntaDesplegable(node)
        return composer.obtenerElementos().lastOrNull()
    }

    override fun visit(node: PreguntaSeleccionUnica): Any? {
        composer.agregarPreguntaSeleccionUnica(node)
        return composer.obtenerElementos().lastOrNull()
    }

    override fun visit(node: PreguntaSeleccionadaMultiple): Any? {
        composer.agregarPreguntaSeleccionMultiple(node)
        return composer.obtenerElementos().lastOrNull()
    }

    override fun visit(node: ComponenteTabla): Any? {
        // Construir filas y celdas de forma iterativa (sin lambdas)
        val filasEvaluadas = mutableListOf<List<ElementoFormulario>>()

        for (fila in node.filas) {
            val celdas = mutableListOf<ElementoFormulario>()

            for (celdaExpr in fila) {
                val celda: ElementoFormulario

                if (celdaExpr is NodoLiteral && celdaExpr.tipo == "table_cell_component") {
                    // La celda contiene un componente UI o instrucción Draw: visitarlo y rescatar el ElementoFormulario
                    val tamanoPrevio = composer.obtenerElementos().size
                    val comp = celdaExpr.valor as NodoInstruccion
                    comp.accept(this)

                    // Recoger todos los elementos que el componente añadió
                    val elementosNuevos = composer.obtenerYLimpiarElementosNuevos(tamanoPrevio)

                    // Usar el primer elemento como contenido de la celda
                    celda = if (elementosNuevos.isNotEmpty()) elementosNuevos[0]
                            else TextoFormulario(contenido = "")
                } else {
                    // Celda normal: evaluar la expresión y convertir a texto
                    val valor = celdaExpr.accept(this)?.toString() ?: ""
                    celda = TextoFormulario(contenido = valor)
                }

                celdas.add(celda)
            }

            filasEvaluadas.add(celdas)
        }

        composer.agregarTabla(node, filasEvaluadas)
        return composer.obtenerElementos().lastOrNull()
    }

    companion object {
        /** Límite de iteraciones para detectar bucles infinitos. */
        private const val MAX_ITER = 1_000
    }
}
