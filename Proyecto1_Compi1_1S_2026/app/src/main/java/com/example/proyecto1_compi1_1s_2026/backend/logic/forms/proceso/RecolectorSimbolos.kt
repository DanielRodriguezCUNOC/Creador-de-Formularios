package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteSeccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteTabla
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteTexto
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

// Recorre el árbol para registrar símbolos antes de otras etapas.
class RecolectorSimbolos(private val entornoGlobal: TablaSimbolos = TablaSimbolos(null)) : Visitor<List<ErrorInfo>> {

    private val errores = mutableListOf<ErrorInfo>()
    private var entornoActual = entornoGlobal

    fun recolectar(instrucciones: List<NodoInstruccion>): ResultadoRecoleccionSimbolos {
        // Punto de entrada.
        errores.clear()
        entornoActual = entornoGlobal

        for (instruccion in instrucciones) {
            instruccion.accept(this)
        }

        return ResultadoRecoleccionSimbolos(entornoGlobal, errores.toList())
    }

    override fun visit(node: NodoLiteral): List<ErrorInfo> {
        return errores
    }

    override fun visit(node: NodoOperacionBinaria): List<ErrorInfo> {
        node.izq.accept(this)
        node.der.accept(this)
        return errores
    }

    override fun visit(node: NodoAccesoVariable): List<ErrorInfo> {
        return errores
    }

    override fun visit(node: NodoLlamadaApi): List<ErrorInfo> {
        node.rangoInicio.accept(this)
        node.rangoFin.accept(this)
        return errores
    }

    override fun visit(node: NodoOperacionUnaria): List<ErrorInfo> {
        node.expresion.accept(this)
        return errores
    }

    override fun visit(node: NodoDeclaracion): List<ErrorInfo> {
        if (entornoActual.obtenerTipo(node.id) != null) {
            errores.add(
                ErrorInfo(
                    TipoError.SEMANTICO,
                    "La variable '${node.id}' ya fue declarada y no puede redefinirse",
                    node.linea,
                    node.columna
                )
            )
            return errores
        }

        val valorInicial = when (node.tipo) {
            "number" -> 0.0
            "string" -> ""
            else -> ""
        }
        entornoActual.almacenarVariable(node.id, valorInicial, node.tipo)
        return errores
    }

    override fun visit(node: NodoDeclaracionSpecial): List<ErrorInfo> {
        if (entornoActual.obtenerTipo(node.id) != null) {
            errores.add(
                ErrorInfo(
                    TipoError.SEMANTICO,
                    "La variable '${node.id}' ya fue declarada y no puede redefinirse",
                    node.linea,
                    node.columna
                )
            )
            return errores
        }

        entornoActual.almacenarVariable(node.id, node.pregunta, "special")
        return errores
    }

    override fun visit(node: NodoAsignacion): List<ErrorInfo> {
        node.nuevoValor.accept(this)
        return errores
    }

    override fun visit(node: NodoSentenciaIf): List<ErrorInfo> {
        node.condicion.accept(this)

        val entornoPrevio = entornoActual
        val entornoIf = TablaSimbolos(entornoPrevio)
        entornoActual = entornoIf
        for (instruccion in node.instruccionesIf) {
            instruccion.accept(this)
        }
        entornoActual = entornoPrevio

        if (node.instruccionesElse != null) {
            val entornoElse = TablaSimbolos(entornoPrevio)
            entornoActual = entornoElse
            for (instruccion in node.instruccionesElse) {
                instruccion.accept(this)
            }
            entornoActual = entornoPrevio
        }

        return errores
    }

    override fun visit(node: NodoCicloWhile): List<ErrorInfo> {
        node.condicion.accept(this)

        val entornoPrevio = entornoActual
        val entornoWhile = TablaSimbolos(entornoPrevio)
        entornoActual = entornoWhile
        for (instruccion in node.instruccionesWhile) {
            instruccion.accept(this)
        }
        entornoActual = entornoPrevio

        return errores
    }

    override fun visit(node: NodoCicloDoWhile): List<ErrorInfo> {
        val entornoPrevio = entornoActual
        val entornoDoWhile = TablaSimbolos(entornoPrevio)
        entornoActual = entornoDoWhile
        for (instruccion in node.instrucciones) {
            instruccion.accept(this)
        }
        entornoActual = entornoPrevio

        node.condicion.accept(this)
        return errores
    }

    override fun visit(node: NodoCicloFor): List<ErrorInfo> {
        val entornoPrevio = entornoActual
        val entornoFor = TablaSimbolos(entornoPrevio)
        entornoActual = entornoFor

        if (!node.esImperativo) {
            if (node.idVariable != null) {
                val tipoExistente = entornoActual.obtenerTipo(node.idVariable)
                if (tipoExistente != null && tipoExistente != "number") {
                    errores.add(
                        ErrorInfo(
                            TipoError.SEMANTICO,
                            "La variable '${node.idVariable}' del FOR ya existe y no es de tipo number",
                            node.linea,
                            node.columna
                        )
                    )
                }

                if (tipoExistente == null) {
                    entornoActual.almacenarVariable(node.idVariable, 0.0, "number")
                }
            }
            node.rangoInicio?.accept(this)
            node.rangoFin.accept(this)
        } else {
            if (node.inicializacionImperativa != null) {
                node.inicializacionImperativa.accept(this)
            }
            node.rangoFin.accept(this)
            if (node.actualizacionImperativa != null) {
                node.actualizacionImperativa.accept(this)
            }
        }

        for (instruccion in node.instruccionesFor) {
            instruccion.accept(this)
        }

        entornoActual = entornoPrevio
        return errores
    }

    override fun visit(node: NodoDraw): List<ErrorInfo> {
        val tipo = entornoActual.obtenerTipo(node.idVariableEspecial)
        if (tipo == null) {
            errores.add(
                ErrorInfo(
                    TipoError.SEMANTICO,
                    "Variable especial '${node.idVariableEspecial}' no fue declarada",
                    node.linea,
                    node.columna
                )
            )
        } else if (tipo != "special") {
            errores.add(
                ErrorInfo(
                    TipoError.SEMANTICO,
                    "No se puede invocar draw sobre '${node.idVariableEspecial}' porque no es special",
                    node.linea,
                    node.columna
                )
            )
        }
        return errores
    }

    override fun visit(node: ComponenteSeccion): List<ErrorInfo> {
        val entornoPrevio = entornoActual
        val entornoSeccion = TablaSimbolos(entornoPrevio)
        entornoActual = entornoSeccion
        for (instruccion in node.elementosInternos) {
            instruccion.accept(this)
        }
        entornoActual = entornoPrevio
        return errores
    }

    override fun visit(node: ComponenteTabla): List<ErrorInfo> {
        return errores
    }

    override fun visit(node: ComponenteTexto): List<ErrorInfo> {
        return errores
    }

    override fun visit(node: PreguntaDesplegable): List<ErrorInfo> {
        return errores
    }

    override fun visit(node: PreguntaSeleccionUnica): List<ErrorInfo> {
        return errores
    }

    override fun visit(node: PreguntaSeleccionadaMultiple): List<ErrorInfo> {
        return errores
    }

    override fun visit(node: PreguntaAbierta): List<ErrorInfo> {
        return errores
    }
}

data class ResultadoRecoleccionSimbolos(
    val tablaSimbolos: TablaSimbolos,
    val errores: List<ErrorInfo>
)
