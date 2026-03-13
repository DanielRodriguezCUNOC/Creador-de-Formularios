package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.NodoBase
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ContextoSemantico

interface NodoInstruccion: NodoBase {
	fun validarSemantica(contexto: ContextoSemantico) {}
}