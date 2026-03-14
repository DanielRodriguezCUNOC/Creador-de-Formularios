package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

/**
 * Contexto compartido durante la validación semántica.
 *
 * Mantiene:
 * - `entornoActual`: tabla de símbolos activa.
 * - `errores`: acumulador de errores semánticos.
 */
class ContextoSemantico(
	var entornoActual: TablaSimbolos,
	private val errores: MutableList<ErrorInfo> = mutableListOf()
) {

	fun reportarError(mensaje: String, linea: Int, columna: Int) {
		errores.add(
			ErrorInfo(
				tipo = TipoError.SEMANTICO,
				mensaje = mensaje,
				linea = linea,
				columna = columna
			)
		)
	}

	fun obtenerErrores(): List<ErrorInfo> = errores
}

