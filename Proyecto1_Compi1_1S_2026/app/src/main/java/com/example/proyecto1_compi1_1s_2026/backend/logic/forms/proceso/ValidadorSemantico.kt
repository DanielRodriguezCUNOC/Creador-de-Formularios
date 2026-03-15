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
        // RecolectorSimbolos ya verificó redeclaraciones y registró las variables globales.
        // Aquí solo validamos el tipo del valor inicial.
        if (node.valorInicio != null) {
            node.valorInicio.validarSemantica(contexto)
            val tipoValor = node.valorInicio.inferirTipo(contexto)
            if (tipoValor != node.tipo && !(node.tipo == "number" && tipoValor == "double")) {
                contexto.reportarError(
                    "Tipo incompatible en declaración '${node.id}': se esperaba ${node.tipo}, pero obtuvo $tipoValor",
                    node.linea,
                    node.columna
                )
            }
        }
        // Si la variable no está en el entorno (declaración dentro de un scope local),
        // la registramos para que las instrucciones siguientes puedan resolverla.
        if (contexto.entornoActual.obtenerTipo(node.id) == null) {
            val valorInicial: Any = when (node.tipo) { "number" -> 0.0; else -> "" }
            contexto.entornoActual.almacenarVariable(node.id, valorInicial, node.tipo)
        }
        return errores
    }

    override fun visit(node: NodoDeclaracionSpecial): List<ErrorInfo> {
        // Delega al nodo; la tabla ya tiene la variable del RecolectorSimbolos.
        node.validarSemantica(contexto)
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