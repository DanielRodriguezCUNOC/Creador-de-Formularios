package com.example.proyecto1_compi1_1s_2026.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorScreen(
    errores: List<ErrorInfo>,
    onBack: () -> Unit
) {
    val horizontalScroll = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tabla de errores") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, Color(0xFF444444), RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
                    .horizontalScroll(horizontalScroll)
            ) {
                SelectionContainer {
                    Column(modifier = Modifier.width(900.dp)) {
                        FilaTablaHeader()
                        Divider(color = Color(0xFF444444))

                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            itemsIndexed(errores) { _, error ->
                                FilaTablaError(error)
                                Divider(color = Color(0xFF333333))
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onBack) {
                    Text("Volver")
                }
            }
        }
    }
}

@Composable
private fun FilaTablaHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2B2B2B))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CeldaTabla("Lexema", 0.14f, true)
        CeldaTabla("Línea", 0.10f, true)
        CeldaTabla("Columna", 0.12f, true)
        CeldaTabla("Tipo", 0.14f, true)
        CeldaTabla("Descripción", 0.50f, true)
    }
}

@Composable
private fun FilaTablaError(error: ErrorInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        CeldaTabla(extraerLexema(error), 0.14f)
        CeldaTabla(error.linea.toString(), 0.10f)
        CeldaTabla(error.columna.toString(), 0.12f)
        CeldaTabla(error.tipo.nombre, 0.14f)
        CeldaTabla(error.mensaje, 0.50f)
    }
}

@Composable
private fun RowScope.CeldaTabla(texto: String, peso: Float, esEncabezado: Boolean = false) {
    Text(
        text = texto,
        modifier = Modifier
            .weight(peso)
            .padding(horizontal = 4.dp),
        color = if (esEncabezado) Color(0xFFFFD700) else Color.White,
        fontWeight = if (esEncabezado) FontWeight.Bold else FontWeight.Normal,
        fontFamily = FontFamily.Monospace,
        fontSize = if (esEncabezado) 12.sp else 11.sp,
        textAlign = TextAlign.Start
    )
}

private fun extraerLexema(error: ErrorInfo): String {
    val mensaje = error.mensaje
    val encontrado = Regex("Pero se encontró:\\s*'([^']*)'").find(mensaje)?.groupValues?.getOrNull(1)
    if (!encontrado.isNullOrBlank()) return encontrado

    val caracter = Regex("Carácter no reconocido\\s*'([^']*)'").find(mensaje)?.groupValues?.getOrNull(1)
    if (!caracter.isNullOrBlank()) return caracter

    return "-"
}
