package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Formulario

/**
 * Resultado devuelto por el Interprete tras recorrer el AST.
 *
 * @param formulario  Modelo visual construido a partir del código analizado.
 * @param errores     Errores de ejecución detectados durante la interpretación
 *                    (división por cero, variable no declarada, bucle infinito).
 */
data class ResultadoInterpretacion(
    val formulario: Formulario,
    val errores: List<ErrorInfo>
)
