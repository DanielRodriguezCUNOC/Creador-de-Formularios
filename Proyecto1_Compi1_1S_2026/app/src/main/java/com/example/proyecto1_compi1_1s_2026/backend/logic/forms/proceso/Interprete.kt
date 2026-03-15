package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.ElementoFormulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Formulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.TextoFormulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

/**
 * Intérprete del AST. Implementa [Visitor]<Any?> para poder evaluar tanto
 * expresiones (devuelven su valor) como instrucciones (ejecutan efectos laterales).
 *
 * Los componentes UI añaden el [ElementoFormulario] construido a la lista interna
 * y también lo devuelven, lo que permite componerlos dentro de secciones anidadas.
 */
class Interprete(private var entornoActual: TablaSimbolos) : Visitor<Any?> {

    private val elementos = mutableListOf<ElementoFormulario>()
    private val errores   = mutableListOf<ErrorInfo>()
    private val uiBuilder by lazy {
        UiNodeBuilder { expr -> expr.accept(this) }
    }
    private val exprBuilder by lazy {
        ExpressionNodeBuilder(
            obtenerEntorno = { entornoActual },
            agregarErrorSemantico = { mensaje, linea, columna ->
                errores.add(ErrorInfo(TipoError.SEMANTICO, mensaje, linea, columna))
            }
        )
    }

    // ─── Punto de entrada ────────────────────────────────────────────────────

    fun interpretar(instrucciones: List<NodoInstruccion>): ResultadoInterpretacion {
        elementos.clear()
        errores.clear()
        for (instruccion in instrucciones) {
            instruccion.accept(this)
        }
        return ResultadoInterpretacion(
            formulario = Formulario(elementos.toList()),
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
            when (node.tipo) {
                "number" -> 0.0
                "string" -> ""
                else     -> null
            }
        }
        entornoActual.almacenarVariable(node.id, valor ?: "", node.tipo)
        return null
    }

    override fun visit(node: NodoDeclaracionSpecial): Any? {
        // Interpretar el componente de forma aislada para no contaminar el flujo principal
        val elementosPrevios = elementos.toMutableList()
        elementos.clear()
        val componente = node.pregunta.accept(this)
        elementos.clear()
        elementos.addAll(elementosPrevios)
        // Guardar el ElementoFormulario construido para que .draw() lo añada al flujo cuando sea llamado
        entornoActual.almacenarVariable(node.id, componente ?: "", "special")
        return null
    }

    override fun visit(node: NodoAsignacion): Any? {
        val valor = node.nuevoValor.accept(this)
        try {
            entornoActual.reasignarVariable(node.id, valor ?: "")
        } catch (e: Exception) {
            errores.add(ErrorInfo(TipoError.SEMANTICO, e.message ?: "Error de asignación", node.linea, node.columna))
        }
        return null
    }

    // ─── Instrucciones de control de flujo ──────────────────────────────────

    override fun visit(node: NodoSentenciaIf): Any? {
        val condicion = exprBuilder.toBool(node.condicion.accept(this))
        val nuevoEntorno = TablaSimbolos(entornoActual)
        val temp = entornoActual
        entornoActual = nuevoEntorno
        if (condicion) {
            for (instruccion in node.instruccionesIf) {
                instruccion.accept(this)
            }
        } else if (node.instruccionesElse != null) {
            for (instruccion in node.instruccionesElse) {
                instruccion.accept(this)
            }
        }
        entornoActual = temp
        return null
    }

    override fun visit(node: NodoCicloWhile): Any? {
        var iter = 0
        while (exprBuilder.toBool(node.condicion.accept(this))) {
            if (++iter > MAX_ITER) {
                break
            }
            val nuevoEntorno = TablaSimbolos(entornoActual)
            val temp = entornoActual
            entornoActual = nuevoEntorno
            for (instruccion in node.instruccionesWhile) {
                instruccion.accept(this)
            }
            entornoActual = temp
        }
        return null
    }

    override fun visit(node: NodoCicloDoWhile): Any? {
        var iter = 0
        do {
            if (++iter > MAX_ITER) {
                break
            }
            val nuevoEntorno = TablaSimbolos(entornoActual)
            val temp = entornoActual
            entornoActual = nuevoEntorno
            for (instruccion in node.instrucciones) {
                instruccion.accept(this)
            }
            entornoActual = temp
        } while (exprBuilder.toBool(node.condicion.accept(this)))
        return null
    }

    override fun visit(node: NodoCicloFor): Any? {
        if (!node.esImperativo) {
            // FOR clásica: FOR ID IN inicio..fin { }
            val inicio = exprBuilder.toDouble(node.rangoInicio?.accept(this))?.toInt() ?: 0
            val fin    = exprBuilder.toDouble(node.rangoFin.accept(this))?.toInt() ?: 0
            for (i in inicio..fin) {
                val nuevoEntorno = TablaSimbolos(entornoActual)
                if (node.idVariable != null) {
                    nuevoEntorno.almacenarVariable(node.idVariable, i.toDouble(), "number")
                }
                val temp = entornoActual
                entornoActual = nuevoEntorno
                for (instruccion in node.instruccionesFor) {
                    instruccion.accept(this)
                }
                entornoActual = temp
            }
        } else {
            // FOR imperativa: FOR (i = init; cond; i = inc) { }
            val nuevoEntorno = TablaSimbolos(entornoActual)
            val temp = entornoActual
            entornoActual = nuevoEntorno

            node.inicializacionImperativa?.accept(this)
            
            var iter = 0
            // Condición: mientras se cumpla
            while (exprBuilder.toBool(node.rangoFin.accept(this))) {
                if (++iter > MAX_ITER) {
                    break
                }
                // Cuerpo
                for (instruccion in node.instruccionesFor) {
                    instruccion.accept(this)
                }
                node.actualizacionImperativa?.accept(this)
            }
            entornoActual = temp
        }
        return null
    }

    /** Recupera la variable special y añade el [ElementoFormulario] almacenado al formulario. */
    override fun visit(node: NodoDraw): Any? {
        return try {
            val elemento = entornoActual.obtenerVariable(node.idVariableEspecial)
            if (elemento is ElementoFormulario) {
                // Aplicar comodines de node.parametros cuando se implemente
                elementos.add(elemento)
            }
            null
        } catch (e: Exception) {
            errores.add(ErrorInfo(TipoError.SEMANTICO, "Variable '${node.idVariableEspecial}' no declarada", node.linea, node.columna))
            null
        }
    }

    // ─── Componentes UI ──────────────────────────────────────────────────────

    override fun visit(node: ComponenteSeccion): Any? {
        // Recolectar los elementos internos de forma aislada
        val elementosPrevios = elementos.toMutableList()
        elementos.clear()
        for (interno in node.elementosInternos) {
            interno.accept(this)
        }
        val internos = elementos.toList()
        elementos.clear()
        elementos.addAll(elementosPrevios)

        val seccion = uiBuilder.construirSeccion(node, internos)
        elementos.add(seccion)
        return seccion
    }

    override fun visit(node: ComponenteTexto): Any? {
        val texto = uiBuilder.construirTexto(node)
        elementos.add(texto)
        return texto
    }

    override fun visit(node: PreguntaAbierta): Any? {
        val pregunta = uiBuilder.construirPreguntaAbierta(node)
        elementos.add(pregunta)
        return pregunta
    }

    override fun visit(node: PreguntaDesplegable): Any? {
        val pregunta = uiBuilder.construirPreguntaDesplegable(node)
        elementos.add(pregunta)
        return pregunta
    }

    override fun visit(node: PreguntaSeleccionUnica): Any? {
        val pregunta = uiBuilder.construirPreguntaSeleccionUnica(node)
        elementos.add(pregunta)
        return pregunta
    }

    override fun visit(node: PreguntaSeleccionadaMultiple): Any? {
        val pregunta = uiBuilder.construirPreguntaSeleccionMultiple(node)
        elementos.add(pregunta)
        return pregunta
    }

    override fun visit(node: ComponenteTabla): Any? {
        // Construir filas y celdas de forma iterativa (sin lambdas)
        val filasEvaluadas = mutableListOf<List<ElementoFormulario>>()

        for (fila in node.filas) {
            val celdas = mutableListOf<ElementoFormulario>()

            for (celdaExpr in fila) {
                val celda: ElementoFormulario

                if (celdaExpr is NodoLiteral && celdaExpr.tipo == "table_cell_component") {
                    // La celda contiene un componente UI: visitarlo y rescatar el ElementoFormulario
                    val tamanoPrevio = elementos.size
                    val comp = celdaExpr.valor as ComponenteUI
                    comp.accept(this)

                    // Recoger todos los elementos que el componente añadió
                    val elementosNuevos = mutableListOf<ElementoFormulario>()
                    var indice = tamanoPrevio
                    while (indice < elementos.size) {
                        elementosNuevos.add(elementos[indice])
                        indice++
                    }

                    // Quitarlos de la lista principal (no pertenecen al nivel raíz)
                    var cantidad = elementosNuevos.size
                    while (cantidad > 0) {
                        elementos.removeAt(elementos.lastIndex)
                        cantidad--
                    }

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

        val tabla = uiBuilder.construirTabla(node, filasEvaluadas)
        elementos.add(tabla)
        return tabla
    }

    companion object {
        /** Límite de iteraciones para detectar bucles infinitos. */
        private const val MAX_ITER = 1_000
    }
}