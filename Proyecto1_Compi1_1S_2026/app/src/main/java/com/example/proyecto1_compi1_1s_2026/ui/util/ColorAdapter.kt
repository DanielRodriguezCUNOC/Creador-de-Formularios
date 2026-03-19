package com.example.proyecto1_compi1_1s_2026.ui.util

import androidx.compose.ui.graphics.Color as ComposeColor
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Color as DomainColor

/**
 * Adaptador que convierte Color de dominio a Color de Compose.
 * Encapsula la conversión en un punto central de la UI layer.
 *
 * GRASP Experto & Bajo Acoplamiento: El backend nunca conoce sobre Compose.
 * La conversión ocurre únicamente en la UI layer.
 */
object ColorAdapter {
    
    /**
     * Convierte un Color de dominio a Color de Compose.
     */
    fun alColorCompose(colorDominio: DomainColor): ComposeColor {
        return ComposeColor(
            red = colorDominio.rojo / 255f,
            green = colorDominio.verde / 255f,
            blue = colorDominio.azul / 255f,
            alpha = colorDominio.alfa / 255f
        )
    }

    /**
     * Convierte un Color de Compose a Color de dominio.
     */
    fun alColorDominio(colorCompose: ComposeColor): DomainColor {
        return DomainColor(
            rojo = (colorCompose.red * 255).toInt(),
            verde = (colorCompose.green * 255).toInt(),
            azul = (colorCompose.blue * 255).toInt(),
            alfa = (colorCompose.alpha * 255).toInt()
        )
    }

    /**
     * Convierte una lista de colores de dominio a Compose.
     */
    fun alColorCompose(coloresDominio: List<DomainColor>): List<ComposeColor> {
        return coloresDominio.map { alColorCompose(it) }
    }
}
