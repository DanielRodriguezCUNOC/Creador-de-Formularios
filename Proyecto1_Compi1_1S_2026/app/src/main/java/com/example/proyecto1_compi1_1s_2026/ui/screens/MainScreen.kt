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
import com.example.proyecto1_compi1_1s_2026.backend.generate.forms.LexerFormulario
import com.example.proyecto1_compi1_1s_2026.backend.generate.forms.ParserFormulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Formulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.NodoInstruccion
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.Interprete
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.RecolectorSimbolos
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TablaSimbolos
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TipoError
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ValidadorSemantico
import com.example.proyecto1_compi1_1s_2026.ui.forms.FormularioRenderer
import kotlinx.coroutines.launch
import java.io.StringReader

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
    onFinalize: (String) -> Unit = {},
    onViewErrors: (List<ErrorInfo>) -> Unit = {}
) {
    var erroresLexicos by remember { mutableStateOf(emptyList<ErrorInfo>()) }
    var erroresSintacticos by remember { mutableStateOf(emptyList<ErrorInfo>()) }
    var erroresSemanticos by remember { mutableStateOf(emptyList<ErrorInfo>()) }
    var mostrarErrores by remember { mutableStateOf(false) }
    var mensajeResultado by remember { mutableStateOf("") }
    var astResultado by remember { mutableStateOf("") }
    var tipoMensaje by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
                    onClick = { onFinalize(editorValue.text) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Finalizar")
                }

                Button(
                    onClick = {
                        // Declarar fuera del try para acceder desde el catch
                        var lexer: LexerFormulario? = null
                        var parser: ParserFormulario? = null

                        try {
                            lexer = LexerFormulario(StringReader(editorValue.text))
                            parser = ParserFormulario(lexer)
                            val resultado = parser.parse()

                            erroresLexicos = lexer.lexicalErrors
                            erroresSintacticos = parser.erroresSintacticos
                            erroresSemanticos = emptyList()

                            // Validar semánticamente si no hay errores léxicos/sintácticos
                            if (erroresLexicos.isEmpty() && erroresSintacticos.isEmpty()) {
                                if (resultado?.value is List<*>) {
                                    @Suppress("UNCHECKED_CAST")
                                    val instrucciones = resultado.value as List<NodoInstruccion>

                                    // Pasadas principales.
                                    val recolector = RecolectorSimbolos(TablaSimbolos(null))
                                    val resultadoRecoleccion = recolector.recolectar(instrucciones)
                                    erroresSemanticos = resultadoRecoleccion.errores

                                    if (erroresSemanticos.isEmpty()) {
                                        val validador = ValidadorSemantico(resultadoRecoleccion.tablaSimbolos)
                                        erroresSemanticos = validador.validar(instrucciones)

                                        if (erroresSemanticos.isEmpty()) {
                                            val interprete = Interprete(TablaSimbolos(null))
                                            val resultadoInterp = interprete.interpretar(instrucciones)
                                            if (resultadoInterp.errores.isEmpty()) {
                                                onFormularioActualChange(resultadoInterp.formulario)
                                                onMostrarFormularioChange(true)
                                            } else {
                                                erroresSemanticos = resultadoInterp.errores
                                                onMostrarFormularioChange(false)
                                            }
                                        } else {
                                            onMostrarFormularioChange(false)
                                        }
                                    } else {
                                        onMostrarFormularioChange(false)
                                    }
                                }
                            } else {
                                onMostrarFormularioChange(false)
                            }

                            val totalErrores = erroresLexicos.size + erroresSintacticos.size + erroresSemanticos.size

                            if (totalErrores == 0) {
                                tipoMensaje = "exito"
                                mensajeResultado = "Sintaxis correcta - Sin errores"
                                astResultado = resultado?.value?.toString() ?: "AST vacío"
                                mostrarErrores = false

                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Formulario construido exitosamente",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            } else {
                                tipoMensaje = "error"
                                mensajeResultado = "Se encontraron $totalErrores error(es)"
                                mostrarErrores = true
                                onMostrarFormularioChange(false)

                                val primerError = erroresLexicos.firstOrNull()
                                    ?: erroresSintacticos.firstOrNull()
                                    ?: erroresSemanticos.firstOrNull()
                                val todosLosErrores = erroresLexicos + erroresSintacticos + erroresSemanticos

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

                        } catch (e: Exception) {
                            
                            val errLex = lexer?.lexicalErrors ?: emptyList()
                            val errSin = parser?.erroresSintacticos ?: emptyList()

                            if (errLex.isNotEmpty() || errSin.isNotEmpty()) {
                                erroresLexicos = errLex
                                erroresSintacticos = errSin
                                erroresSemanticos = emptyList()
                                tipoMensaje = "error"
                                mostrarErrores = true
                                onMostrarFormularioChange(false)

                                val primerError = errLex.firstOrNull() ?: errSin.firstOrNull()
                                val todosLosErrores = errLex + errSin
                                coroutineScope.launch {
                                    val resultadoSnackbar = snackbarHostState.showSnackbar(
                                        message = primerError?.toDetailedString() ?: "Error de sintaxis",
                                        actionLabel = "Ver Todos",
                                        duration = SnackbarDuration.Long
                                    )
                                    if (resultadoSnackbar == SnackbarResult.ActionPerformed) {
                                        onViewErrors(todosLosErrores)
                                    }
                                }
                            } else {
                                tipoMensaje = "error"
                                mostrarErrores = true
                                erroresSemanticos = listOf(
                                    ErrorInfo(TipoError.SEMANTICO, "Excepción inesperada: ${e.message}", 0, 0)
                                )
                                onMostrarFormularioChange(false)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Error inesperado: ${e.message}",
                                        duration = SnackbarDuration.Long
                                    )
                                }
                            }
                            e.printStackTrace()
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