package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

// Representa el árbol intermedio de etiquetas .pkm.
sealed class PkmTagNode

class PkmGroupNode(
    val hijos: MutableList<PkmTagNode> = mutableListOf()
) : PkmTagNode()

class PkmElementNode(
    val apertura: String,
    val cierre: String? = null,
    val hijos: MutableList<PkmTagNode> = mutableListOf()
) : PkmTagNode()

class PkmTextNode(
    val texto: String
) : PkmTagNode()
