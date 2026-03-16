package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteUI
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

/**
 * Nodo AST para la declaración de una variable de tipo special.
 *
 * A diferencia de NodoDeclaracion, la variable special siempre debe
 * inicializarse con un componente de pregunta (ComponenteUI). Por eso
 * el valor de inicio no es una NodoExpresion sino directamente un ComponenteUI.
 */
class NodoDeclaracionSpecial(
    val id: String,
    val pregunta: ComponenteUI,
    override val linea: Int = 0,
    override val columna: Int = 0
) : NodoInstruccion {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)

    override fun validarSemantica(contexto: ContextoSemantico) {
        // La tabla ya tiene esta variable (registrada por RecolectorSimbolos).
        // Solo validamos el componente interno.
        pregunta.validarSemantica(contexto)
    }
}
