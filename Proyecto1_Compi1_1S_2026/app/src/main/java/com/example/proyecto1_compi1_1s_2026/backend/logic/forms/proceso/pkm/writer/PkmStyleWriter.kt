package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.NodoAtributo

// Renderiza el bloque <style>...</style> en formato PKM.
class PkmStyleWriter(
    private val expressionWriter: PkmExpressionWriter,
    private val stats: PkmStatsCollector
) {

    fun crearBloqueStylesSiExiste(attrs: List<NodoAtributo>): PkmTagNode? {
        val stylesRaw = NodoAtributo.valor(attrs, "styles") ?: return null
        stats.registrarEstilo()
        val stylesText = expressionWriter.valorComoTexto(stylesRaw)

        return PkmElementNode(
            PkmSerializationContract.tagStyleOpen(),
            PkmSerializationContract.tagStyleClose(),
            mutableListOf(PkmTextNode(stylesText))
        )
    }
}
