package com.example.proyecto1_compi1_1s_2026.ui.forms

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.EstiloElemento
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Color as DomainColor
import androidx.compose.ui.graphics.Color as ComposeColor

// ── Conversión de Colores ───────────────────────────────────────────────────

/**
 * Convierte el color del dominio (backend) al color de Jetpack Compose.
 */
fun DomainColor.toComposeColor(): ComposeColor {
    return ComposeColor(red = rojo / 255f, green = verde / 255f, blue = azul / 255f, alpha = alfa / 255f)
}

// ── Fuentes ──────────────────────────────────────────────────────────────────

fun obtenerFontFamily(nombre: String): FontFamily = when (nombre.uppercase()) {
    "MONO"      -> FontFamily.Monospace
    "CURSIVE"   -> FontFamily.Cursive
    "SERIF"     -> FontFamily.Serif
    else        -> FontFamily.SansSerif
}

// ── TextStyle ─────────────────────────────────────────────────────────────────

fun EstiloElemento.toTextStyle(): TextStyle = TextStyle(
    color      = color.toComposeColor(),
    fontSize   = textSize.sp,
    fontFamily = obtenerFontFamily(fontFamily)
)

// ── Modifier de borde personalizado ──────────────────────────────────────────

fun EstiloElemento.applyBorder(modifier: Modifier): Modifier {
    val b = border ?: return modifier
    return modifier.drawBehind {
        val stroke = when (b.tipo) {
            "DOTTED" -> Stroke(
                width       = b.grosor,
                pathEffect  = PathEffect.dashPathEffect(floatArrayOf(b.grosor * 3, b.grosor * 2), 0f)
            )
            else -> Stroke(width = b.grosor)
        }

        // Rectángulo exterior
        drawRect(color = b.color.toComposeColor(), style = stroke)

        // Para DOUBLE, dibuja un segundo rectángulo interior
        if (b.tipo == "DOUBLE") {
            val offset = b.grosor * 3
            drawRect(
                color   = b.color.toComposeColor(),
                topLeft = Offset(offset, offset),
                size    = androidx.compose.ui.geometry.Size(size.width - offset * 2, size.height - offset * 2),
                style   = Stroke(width = b.grosor)
            )
        }
    }
}
