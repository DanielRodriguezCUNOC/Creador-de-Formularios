package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

class ValidadorSemantico(var entornoActual: TablaSimbolos) : Visitor<List<ErrorInfo>> {

    private val errores = mutableListOf<ErrorInfo>()
    private lateinit var contexto: ContextoSemantico

    fun validar(instrucciones: List<NodoInstruccion>): List<ErrorInfo> {
        errores.clear()
        contexto = ContextoSemantico(entornoActual, errores)
        for (instruccion in instrucciones) {
            instruccion.accept(this)
        }
        return errores
    }

    override fun visit(node: NodoLiteral): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: NodoOperacionBinaria): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: NodoAccesoVariable): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: NodoLlamadaApi): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: NodoOperacionUnaria): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: NodoDeclaracion): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: NodoDeclaracionSpecial): List<ErrorInfo> {
        if (contexto.entornoActual.obtenerTipo(node.id) != null) {
            contexto.reportarError(
                "La variable '${node.id}' ya fue declarada y no puede redefinirse",
                node.linea,
                node.columna
            )
            return errores
        }

        // Registrar la variable en la tabla de símbolos como tipo "special"
        contexto.entornoActual.almacenarVariable(node.id, node.pregunta, "special")
        // Delegar la validación semántica a la pregunta interna
        node.pregunta.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: NodoAsignacion): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: NodoSentenciaIf): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: NodoCicloWhile): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: NodoCicloDoWhile): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: NodoCicloFor): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: NodoDraw): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    // ─── Componentes UI ───────────────────────────────────────────────────────

    override fun visit(node: ComponenteSeccion): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: ComponenteTabla): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: ComponenteTexto): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: PreguntaAbierta): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: PreguntaDesplegable): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: PreguntaSeleccionUnica): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

    override fun visit(node: PreguntaSeleccionadaMultiple): List<ErrorInfo> {
        node.validarSemantica(contexto)
        return errores
    }

}