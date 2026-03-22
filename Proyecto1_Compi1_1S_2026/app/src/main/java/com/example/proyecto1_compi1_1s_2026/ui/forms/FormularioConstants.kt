package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.ui.unit.dp

/**
 * Constantes globales para el renderizado de formularios.
 * Define factores de escala y dimensiones consistentes.
 */
object FormularioConstants {
    
    const val ESCALA_DIMENSION = 1f

    /**
     * Espaciado vertical estándar entre elementos principales
     */
    val PADDING_PRINCIPAL = 6.dp

    /**
     * Espaciado entre elementos en secciones/columnas
     */
    val PADDING_ELEMENTO = 4.dp

    /**
     * Espaciado interno dentro de componentes
     */
    val PADDING_INTERNO = 2.dp

    /**
     * Espaciado mínimo para tablas
     */
    val PADDING_TABLA = 4.dp

    /**
     * Espacio entre elementos en layout horizontal
     */
    val SPACING_HORIZONTAL = 5.dp

    /**
     * Espacio entre elementos en layout vertical
     */
    val SPACING_VERTICAL = 5.dp

    /**
     * Limite recomendado para distribucion horizontal en telefono.
     * Si hay mas elementos, se envuelven en filas nuevas.
     */
    const val MAX_COLUMNAS_MOVIL = 3

    /**
     * Altura mínima para celdas de tabla para alineación visual uniforme
     */
    val MIN_ALTURA_CELDA_TABLA = 60.dp

    /**
     * Convierte una dimensión del DSL a dp aplicando la escala
     */
    fun escalarDimension(valor: Float?): Float? {
        return valor?.let { it * ESCALA_DIMENSION }
    }
}
