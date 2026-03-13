package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.*

interface Visitor<T>{

    //* NodoExpresion
    fun visit(node: NodoLiteral):T
    fun visit(node: NodoOperacionBinaria):T
    fun visit(node: NodoAccesoVariable):T
    fun visit(node: NodoLlamadaApi):T
    fun visit(node: NodoOperacionUnaria):T

    //* NodoInstruccion
    fun visit(node: NodoDeclaracion):T
    fun visit(node: NodoDeclaracionSpecial):T
    fun visit(node: NodoAsignacion):T
    fun visit(node: NodoSentenciaIf):T
    fun visit(node: NodoCicloWhile):T
    fun visit(node: NodoCicloDoWhile):T
    fun visit(node: NodoCicloFor):T
    fun visit(node: NodoDraw):T

    //* Componentes de la UI
    fun visit(node: ComponenteSeccion):T
    fun visit(node: ComponenteTabla):T
    fun visit(node: ComponenteTexto):T
    fun visit(node: PreguntaDesplegable):T
    fun visit(node: PreguntaSeleccionUnica):T
    fun visit(node: PreguntaSeleccionadaMultiple):T
    fun visit(node: PreguntaAbierta):T

}