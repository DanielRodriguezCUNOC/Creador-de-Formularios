package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.ComponenteUI
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor

/**
 * Nodo AST para la declaración de una variable de tipo special.
 *
 * Gramática: `miPregunta : special = OPEN_QUESTION [ ... ]`
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
}
