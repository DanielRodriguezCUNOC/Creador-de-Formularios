package com.example.proyecto1_compi1_1s_2026.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1_compi1_1s_2026.backend.generate.forms.LexerFormulario
import com.example.proyecto1_compi1_1s_2026.backend.generate.forms.ParserFormulario
import java.io.StringReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onMenuClick: () -> Unit, onFinalize: (String) -> Unit = {}) {
    var editorValue by remember { mutableStateOf(TextFieldValue("")) }
    var erroresLexicos by remember { mutableStateOf(emptyList<String>()) }
    var erroresSintacticos by remember { mutableStateOf(emptyList<String>()) }
    var mostrarErrores by remember { mutableStateOf(false) }

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

            // ── Vista previa con resaltado de sintaxis ──────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(
                        width = 1.dp,
                        color = Color(0xFF404040),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(SyntaxHighlighter.bgColor, RoundedCornerShape(8.dp))
            ) {
                val verticalScroll   = rememberScrollState()
                val horizontalScroll = rememberScrollState()

                Text(
                    text = SyntaxHighlighter.highlight(editorValue.text),
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize   = 14.sp,
                        color      = SyntaxHighlighter.textColor,
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(verticalScroll)
                        .horizontalScroll(horizontalScroll)
                        .padding(12.dp)
                )
            }

            // ── Editor de código ─────────────────────────────────────────
            OutlinedTextField(
                value = editorValue,
                onValueChange = { newValue ->
                    editorValue = TextFieldValue(
                        annotatedString = SyntaxHighlighter.highlight(newValue.text),
                        selection       = newValue.selection,
                        composition     = newValue.composition
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                label       = { Text("Editor de código") },
                placeholder = { Text("Escribe aquí...") },
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize   = 14.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor   = SyntaxHighlighter.bgColor,
                    unfocusedContainerColor = SyntaxHighlighter.bgColor,
                    focusedTextColor        = SyntaxHighlighter.textColor,
                    unfocusedTextColor      = SyntaxHighlighter.textColor,
                    focusedBorderColor      = Color(0xFF569CD6),
                    unfocusedBorderColor    = Color(0xFF404040),
                    cursorColor             = Color.White,
                    focusedLabelColor       = Color(0xFF9CDCFE),
                    unfocusedLabelColor     = Color(0xFF6E7681)
                ),
                maxLines = Int.MAX_VALUE
            )
            // ── Panel de Errores (si hay) ────────────────────────────────
            if (mostrarErrores && (erroresLexicos.isNotEmpty() || erroresSintacticos.isNotEmpty())) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Red,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Errores Léxicos
                        if (erroresLexicos.isNotEmpty()) {
                            Text(
                                text = " Errores Léxicos (${erroresLexicos.size}):",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = Color(0xFFFF6B6B)
                                )
                            )
                            for (error in erroresLexicos) {
                                Text(
                                    text = "  • $error",
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = Color(0xFFFFB3B3)
                                    )
                                )
                            }
                        }

                        // Errores Sintácticos
                        if (erroresSintacticos.isNotEmpty()) {
                            Text(
                                text = "Errores Sintácticos (${erroresSintacticos.size}):",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = Color(0xFFFFD700)
                                )
                            )
                            for (error in erroresSintacticos) {
                                Text(
                                    text = "  • $error",
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = Color(0xFFFFE082)
                                    )
                                )
                            }
                        }
                    }
                }
            }

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
                    onClick = { onFinalize(editorValue.text) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Finalizar")
                }

                Button(
                    onClick = {
                        try {
                            val lexer = LexerFormulario(StringReader(editorValue.text))
                            val parser = ParserFormulario(lexer)
                            val resultado = parser.parse()

                            erroresLexicos = lexer.lexicalErrors
                            erroresSintacticos = parser.erroresSintacticos


                            if(parser.erroresSintacticos.isEmpty() && erroresSintacticos.isEmpty()){
                                println("Sintaxis correcta")
                                println("AST: ${resultado?.value}")
                                mostrarErrores = false
                            }else {
                                println("Errores Encontrados:")
                                mostrarErrores = true

                                if (erroresLexicos.isNotEmpty()) {
                                    println("Errores Léxicos:")
                                    for (error in erroresLexicos) {
                                        println(" - $error")
                                    }
                                }
                                if (erroresSintacticos.isNotEmpty()) {
                                    println("Errores Sintácticos:")
                                    for (error in erroresSintacticos) {
                                        println(" - $error")
                                    }
                                }
                            }
                        }catch (e: Exception){
                            println("Excepcion mientras se analizaba")
                            e.printStackTrace()
                            mostrarErrores = true
                            erroresLexicos = listOf("Excepcion: ${e.message}")
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Analizar")
                }
            }
        }
    }
}
