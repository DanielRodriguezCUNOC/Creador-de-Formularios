package com.example.proyecto1_compi1_1s_2026.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Formulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo
import com.example.proyecto1_compi1_1s_2026.ui.forms.FormularioRenderer
import com.example.proyecto1_compi1_1s_2026.ui.integration.FormularioUiCoordinator
import com.example.proyecto1_compi1_1s_2026.ui.integration.ResultadoAnalisisUi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    editorValue: TextFieldValue,
    onEditorValueChange: (TextFieldValue) -> Unit,
    formularioActual: Formulario?,
    mostrarFormulario: Boolean,
    onFormularioActualChange: (Formulario?) -> Unit,
    onMostrarFormularioChange: (Boolean) -> Unit,
    onMenuClick: () -> Unit,
    onFinalize: (Formulario) -> Unit = {},
    onViewErrors: (List<ErrorInfo>) -> Unit = {}
) {
    var erroresLexicos by remember { mutableStateOf(emptyList<ErrorInfo>()) }
    var erroresSintacticos by remember { mutableStateOf(emptyList<ErrorInfo>()) }
    var erroresSemanticos by remember { mutableStateOf(emptyList<ErrorInfo>()) }
    var tipoMensaje by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val coordinator = remember { FormularioUiCoordinator() }

    fun aplicarResultado(resultado: ResultadoAnalisisUi, navegarAlFormulario: Boolean) {
        erroresLexicos = resultado.erroresLexicos
        erroresSintacticos = resultado.erroresSintacticos
        erroresSemanticos = resultado.erroresSemanticos

        if (resultado.exitoso) {
            onFormularioActualChange(resultado.formulario)
            onMostrarFormularioChange(true)
            tipoMensaje = "exito"

            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = if (navegarAlFormulario) "Formulario listo para contestar" else "Formulario construido exitosamente",
                    duration = SnackbarDuration.Short
                )
            }

            if (navegarAlFormulario && resultado.formulario != null) {
                onFinalize(resultado.formulario)
            }
            return
        }

        onFormularioActualChange(null)
        onMostrarFormularioChange(false)
        tipoMensaje = "error"

        val primerError = resultado.primerError
        val todosLosErrores = resultado.errores

        coroutineScope.launch {
            val resultadoSnackbar = snackbarHostState.showSnackbar(
                message = primerError?.toDetailedString() ?: "Errores encontrados",
                actionLabel = "Ver Todos",
                duration = SnackbarDuration.Long
            )
            if (resultadoSnackbar == SnackbarResult.ActionPerformed) {
                onViewErrors(todosLosErrores)
            }
        }
    }

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
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (tipoMensaje == "exito") Color(0xFF00AA00) else Color(0xFFFF6B6B),
                    contentColor = Color.White,
                    actionColor = Color.Yellow
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Vista previa: código con resaltado O formulario construido ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(
                        width = 1.dp,
                        color = Color(0xFF404040),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(
                        if (mostrarFormulario) Color.White else SyntaxHighlighter.bgColor,
                        RoundedCornerShape(8.dp)
                    )
            ) {
                if (mostrarFormulario && formularioActual != null) {
                    // ── Formulario construido ────────────────────────────
                    FormularioRenderer(
                        formulario = formularioActual!!,
                        onEnviar   = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message  = "Formulario enviado",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                } else {
                    // ── Vista previa con resaltado de sintaxis ───────────
                    val verticalScroll  = rememberScrollState()
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
            }

            // ── Editor de código ─────────────────────────────────────────
            OutlinedTextField(
                value = editorValue,
                onValueChange = { newValue ->
                    onEditorValueChange(
                        TextFieldValue(
                        annotatedString = SyntaxHighlighter.highlight(newValue.text),
                        selection = newValue.selection,
                        composition = newValue.composition
                    )
                    )
                    tipoMensaje = ""
                    onMostrarFormularioChange(false)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                label = { Text("Editor de código") },
                placeholder = { Text("Escribe aquí...") },
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SyntaxHighlighter.bgColor,
                    unfocusedContainerColor = SyntaxHighlighter.bgColor,
                    focusedTextColor = SyntaxHighlighter.textColor,
                    unfocusedTextColor = SyntaxHighlighter.textColor,
                    focusedBorderColor = Color(0xFF569CD6),
                    unfocusedBorderColor = Color(0xFF404040),
                    cursorColor = Color.White,
                    focusedLabelColor = Color(0xFF9CDCFE),
                    unfocusedLabelColor = Color(0xFF6E7681)
                ),
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
                    onClick = {
                        val resultado = coordinator.analizar(editorValue.text)
                        aplicarResultado(resultado, navegarAlFormulario = true)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Finalizar")
                }

                Button(
                    onClick = {
                        val resultado = coordinator.analizar(editorValue.text)
                        aplicarResultado(resultado, navegarAlFormulario = false)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Analizar")
                }
            }
        }
    }
}