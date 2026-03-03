package com.example.proyecto1_compi1_1s_2026.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onMenuClick: () -> Unit, onFinalize: (String) -> Unit = {}) {
    var editorText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Creador de Formularios") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Abrir menú"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Lienzo ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(Color.White, RoundedCornerShape(8.dp))
            )
            {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 48f
                        isAntiAlias = true
                        typeface = android.graphics.Typeface.MONOSPACE
                    }

                    val paddingLeft = 24f
                    val paddingTop  = 60f
                    val lineHeight  = 60f
                    val maxWidth    = size.width - paddingLeft * 2

                    var y = paddingTop

                    // Dividir por saltos de línea reales primero
                    val lines = editorText.split("\n")
                    for (rawLine in lines) {
                        if (rawLine.isEmpty()) {
                            y += lineHeight
                            continue
                        }
                        // Word-wrap dentro de cada línea
                        val words = rawLine.split(" ")
                        var current = ""
                        for (word in words) {
                            val test = if (current.isEmpty()) word else "$current $word"
                            if (paint.measureText(test) <= maxWidth) {
                                current = test
                            } else {
                                drawContext.canvas.nativeCanvas.drawText(current, paddingLeft, y, paint)
                                y += lineHeight
                                current = word
                            }
                        }
                        if (current.isNotEmpty()) {
                            drawContext.canvas.nativeCanvas.drawText(current, paddingLeft, y, paint)
                            y += lineHeight
                        }
                    }
                }

            }

            // ── Editor de texto ─────────────────────────────────────────
            OutlinedTextField(
                value = editorText,
                onValueChange = { editorText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                label = { Text("Editor de texto") },
                placeholder = { Text("Escribe aquí...") },
                maxLines = Int.MAX_VALUE
            )

            // ── Botones de acción ────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = { /* TODO: Add */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Colores")
                }

                Button(
                    onClick = { /* TODO: Add */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Agregar")
                }
                Button(
                    onClick = { onFinalize(editorText) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Finalizar")
                }
            }
        }
    }
}
