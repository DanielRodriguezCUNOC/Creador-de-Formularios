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

// ── Fuentes ──────────────────────────────────────────────────────────────────

fun obtenerFontFamily(nombre: String): FontFamily = when (nombre.uppercase()) {
    "MONO"      -> FontFamily.Monospace
    "CURSIVE"   -> FontFamily.Cursive
    "SERIF"     -> FontFamily.Serif
    else        -> FontFamily.SansSerif
}

// ── TextStyle ─────────────────────────────────────────────────────────────────

fun EstiloElemento.toTextStyle(): TextStyle = TextStyle(
    color      = color,
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
        drawRect(color = b.color, style = stroke)

        // Para DOUBLE, dibuja un segundo rectángulo interior
        if (b.tipo == "DOUBLE") {
            val offset = b.grosor * 3
            drawRect(
                color   = b.color,
                topLeft = Offset(offset, offset),
                size    = androidx.compose.ui.geometry.Size(size.width - offset * 2, size.height - offset * 2),
                style   = Stroke(width = b.grosor)
            )
        }
    }
}
