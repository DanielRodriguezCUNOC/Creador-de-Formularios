package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.NodoBase
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

interface NodoExpresion: NodoBase {
	fun validarSemantica(contexto: ContextoSemantico) {}

	fun inferirTipo(contexto: ContextoSemantico): String = "unknown"
}