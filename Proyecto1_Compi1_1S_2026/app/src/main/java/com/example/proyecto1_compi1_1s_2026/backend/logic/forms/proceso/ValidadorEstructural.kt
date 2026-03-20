package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteSeccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteTabla
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteTexto
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

// Valida reglas de ubicación y estructura del AST.
class ValidadorEstructural : Visitor<List<ErrorInfo>> {

    private val errores = mutableListOf<ErrorInfo>()
    private var nivelContenedorRender = 0

    fun validar(instrucciones: List<NodoInstruccion>): List<ErrorInfo> {
        errores.clear()
        nivelContenedorRender = 0

        for (instruccion in instrucciones) {
            instruccion.accept(this)
        }

        return errores.toList()
    }

    override fun visit(node: NodoLiteral): List<ErrorInfo> {
        return errores
    }

    override fun visit(node: NodoListaExpresiones): List<ErrorInfo> {
        node.elementos.forEach { elem ->
            if (elem is NodoExpresion) {
                elem.accept(this)
            }
        }
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
        if (node.valorInicio != null) {
            node.valorInicio.accept(this)
        }
        return errores
    }

    override fun visit(node: NodoDeclaracionSpecial): List<ErrorInfo> {
        node.pregunta.accept(this)
        return errores
    }

    override fun visit(node: NodoAsignacion): List<ErrorInfo> {
        node.nuevoValor.accept(this)
        return errores
    }

    override fun visit(node: NodoSentenciaIf): List<ErrorInfo> {
        node.condicion.accept(this)

        for (instruccion in node.instruccionesIf) {
            instruccion.accept(this)
        }

        if (node.instruccionesElse != null) {
            for (instruccion in node.instruccionesElse) {
                instruccion.accept(this)
            }
        }

        return errores
    }

    override fun visit(node: NodoCicloWhile): List<ErrorInfo> {
        node.condicion.accept(this)
        for (instruccion in node.instruccionesWhile) {
            instruccion.accept(this)
        }
        return errores
    }

    override fun visit(node: NodoCicloDoWhile): List<ErrorInfo> {
        for (instruccion in node.instrucciones) {
            instruccion.accept(this)
        }
        node.condicion.accept(this)
        return errores
    }

    override fun visit(node: NodoCicloFor): List<ErrorInfo> {
        if (!node.esImperativo) {
            if (node.rangoInicio != null) {
                node.rangoInicio.accept(this)
            }
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

        return errores
    }

    override fun visit(node: NodoDraw): List<ErrorInfo> {
        // draw() solo tiene sentido en un contenedor visual.
        if (nivelContenedorRender <= 0) {
            errores.add(
                ErrorInfo(
                    TipoError.SEMANTICO,
                    "No se puede mostrar una pregunta fuera de SECTION/TABLE",
                    node.linea,
                    node.columna
                )
            )
        }

        for (parametro in node.parametros) {
            parametro.accept(this)
        }

        return errores
    }

    override fun visit(node: ComponenteSeccion): List<ErrorInfo> {
        nivelContenedorRender++
        for (instruccion in node.elementosInternos) {
            instruccion.accept(this)
        }
        nivelContenedorRender--
        return errores
    }

    override fun visit(node: ComponenteTabla): List<ErrorInfo> {
        for (fila in node.filas) {
            for (celda in fila) {
                celda.accept(this)
            }
        }
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
