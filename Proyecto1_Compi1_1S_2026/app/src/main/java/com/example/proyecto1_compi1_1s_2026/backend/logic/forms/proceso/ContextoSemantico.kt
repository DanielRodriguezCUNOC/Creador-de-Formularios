package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

class ContextoSemantico(
    var entornoActual: TablaSimbolos,
    private val errores: MutableList<ErrorInfo> = mutableListOf()
) {
    fun reportarError(mensaje: String, linea: Int, columna: Int) {
        errores.add(ErrorInfo(TipoError.SEMANTICO, mensaje, linea, columna))
    }

    fun obtenerErrores(): List<ErrorInfo> = errores
}