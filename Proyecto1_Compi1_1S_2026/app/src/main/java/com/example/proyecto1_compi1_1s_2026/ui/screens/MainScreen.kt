package com.example.proyecto1_compi1_1s_2026.ui.screens

import android.content.Context
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
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
import com.example.proyecto1_compi1_1s_2026.ui.util.bloquesDisponibles
import com.example.proyecto1_compi1_1s_2026.ui.util.ColorPickerDialog
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    onFinalize: (Formulario, String) -> Unit = { _, _ -> },
    onViewErrors: (List<ErrorInfo>) -> Unit = {},
    onSubirPkmApi: suspend (codigoPkm: String, autor: String, nombreFormulario: String) -> Boolean = { _, _, _ -> false },
    onCodigoPkmGenerado: (String) -> Unit = {}
) {
    var erroresLexicos by remember { mutableStateOf(emptyList<ErrorInfo>()) }
    var erroresSintacticos by remember { mutableStateOf(emptyList<ErrorInfo>()) }
    var erroresSemanticos by remember { mutableStateOf(emptyList<ErrorInfo>()) }
    var tipoMensaje by remember { mutableStateOf("") }
    var mostrarDialogoGuardarDb by remember { mutableStateOf(false) }
    var formularioPendienteFinalizar by remember { mutableStateOf<Formulario?>(null) }
    var pkmPendienteFinalizar by remember { mutableStateOf("") }
    var autorInput by remember { mutableStateOf("") }
    var nombreFormularioInput by remember { mutableStateOf("") }
    var mostrarDialogoAgregar by remember { mutableStateOf(false) }
    var mostrarDialogoColor by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val coordinator = remember { FormularioUiCoordinator() }
    val context = LocalContext.current
    val bloquesDisponibles = remember { bloquesDisponibles }

    fun aplicarResultado(resultado: ResultadoAnalisisUi, navegarAlFormulario: Boolean) {
        erroresLexicos = resultado.erroresLexicos
        erroresSintacticos = resultado.erroresSintacticos
        erroresSemanticos = resultado.erroresSemanticos

        if (resultado.exitoso) {
            onFormularioActualChange(resultado.formulario)
            onMostrarFormularioChange(true)
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
                        // ── Vista previa con resaltado de sintaxis usando el lexer ───────────
                        val verticalScroll  = rememberScrollState()
                        val horizontalScroll = rememberScrollState()
                        val tokens = remember(editorValue.text) {
                            LexerFormulario.analizar(editorValue.text)
                        }
                        Text(
                            text = LexerSyntaxHighlighter.highlightWithTokens(editorValue.text, tokens),
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize   = 14.sp,
                                color      = LexerSyntaxHighlighter.colorDefault,
                                lineHeight = 22.sp
                            ),
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(verticalScroll)
                                .horizontalScroll(horizontalScroll)
                                .padding(12.dp)
                        )
                pkmPendienteFinalizar = resultado.codigoPkm
                mostrarDialogoGuardarDb = true
            }
            return
        }

        onFormularioActualChange(null)
        onMostrarFormularioChange(false)
        onCodigoPkmGenerado("")
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
                    onCodigoPkmGenerado("")
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
                    onClick = { mostrarDialogoColor = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Colores")
                }

                Button(
                    onClick = { mostrarDialogoAgregar = true },
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

    // Dialogo de selección de color personalizado
    if (mostrarDialogoColor) {
        ColorPickerDialog(
            onDismiss = { mostrarDialogoColor = false },
            onColorSelected = { colorString ->
                // Insertar el color en la posición del cursor
                val cursor = editorValue.selection.start
                val textoAntes = editorValue.text.substring(0, cursor)
                val textoDespues = editorValue.text.substring(cursor)
                val nuevoTexto = textoAntes + colorString + textoDespues
                val nuevaPos = (textoAntes + colorString).length
                onEditorValueChange(
                    TextFieldValue(
                        text = nuevoTexto,
                        selection = TextRange(nuevaPos)
                    )
                )
            }
        )
    }

    //* Permite que el usuario pueda seleccionar un elemento para agregar al editor
    if (mostrarDialogoAgregar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAgregar = false },
            title = { Text("Selecciona un bloque") },
            text = {
                Column {
                    bloquesDisponibles.forEach { bloque ->
                        Button(
                            onClick = {
                                // Inserta el bloque en la posición del cursor
                                val cursor = editorValue.selection.start
                                val textoAntes = editorValue.text.substring(0, cursor)
                                val textoDespues = editorValue.text.substring(cursor)
                                val nuevoTexto = textoAntes + bloque.plantilla + textoDespues
                                val nuevaPos = (textoAntes + bloque.plantilla).length
                                onEditorValueChange(
                                    TextFieldValue(
                                        text = nuevoTexto,
                                        selection = TextRange(nuevaPos)
                                    )
                                )
                                mostrarDialogoAgregar = false
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Text(bloque.nombre)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { mostrarDialogoAgregar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }




    if (mostrarDialogoGuardarDb && formularioPendienteFinalizar != null) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoGuardarDb = false
                autorInput = ""
                nombreFormularioInput = ""
            },
            title = { Text("Guardar PKM") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Completa los datos para guardar el formulario:")
                    
                    OutlinedTextField(
                        value = autorInput,
                        onValueChange = { autorInput = it },
                        label = { Text("Autor") },
                        placeholder = { Text("Tu nombre o empresa") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = nombreFormularioInput,
                        onValueChange = { nombreFormularioInput = it },
                        label = { Text("Nombre del Formulario") },
                        placeholder = { Text("Ej: Formulario de Encuesta") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val formulario = formularioPendienteFinalizar
                        val codigo = pkmPendienteFinalizar
                        val autor = autorInput.ifBlank { "AndroidApp" }
                        val nombre = nombreFormularioInput
                        
                        mostrarDialogoGuardarDb = false
                        formularioPendienteFinalizar = null
                        pkmPendienteFinalizar = ""
                        autorInput = ""
                        nombreFormularioInput = ""

                        if (formulario == null || codigo.isBlank()) {
                            return@TextButton
                        }

                        coroutineScope.launch {
                            val guardado = onSubirPkmApi(codigo, autor, nombre)
                            snackbarHostState.showSnackbar(
                                message = if (guardado) {
                                    "PKM subido exitosamente a la API"
                                } else {
                                    "No se pudo subir PKM a la API. Revisa URL ngrok"
                                },
                                duration = SnackbarDuration.Short
                            )
                            onFinalize(formulario, codigo)
                        }
                    }
                ) {
                    Text("Subir")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        val formulario = formularioPendienteFinalizar
                        val codigo = pkmPendienteFinalizar
                        mostrarDialogoGuardarDb = false
                        formularioPendienteFinalizar = null
                        pkmPendienteFinalizar = ""
                        autorInput = ""
                        nombreFormularioInput = ""

                        if (formulario != null) {
                            onFinalize(formulario, codigo)
                        }
                    }
                ) {
                    Text("Omitir")
                }
            }
        )
    }
}


private data class ResultadoGuardadoPkm(
    val exitoso: Boolean,
    val rutaMostrada: String,
    val uri: Uri? = null
)

private fun guardarCodigoPkmEnDocumentos(context: Context, codigoPkm: String): ResultadoGuardadoPkm {
    if (codigoPkm.isBlank()) {
        return ResultadoGuardadoPkm(
            exitoso = false,
            rutaMostrada = "Documentos/CreadorFormularios"
        )
    }

    val nombreArchivo = "formulario_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pkm"

    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val rutaRelativa = "${Environment.DIRECTORY_DOCUMENTS}/CreadorFormularios"
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, rutaRelativa)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), values)
                ?: return ResultadoGuardadoPkm(false, "Documentos/CreadorFormularios/$nombreArchivo")

            try {
                resolver.openOutputStream(uri)?.use { salida ->
                    salida.write(codigoPkm.toByteArray(Charsets.UTF_8))
                    salida.flush()
                } ?: throw IllegalStateException("No se pudo abrir stream de escritura")

                val finalizar = ContentValues().apply {
                    put(MediaStore.MediaColumns.IS_PENDING, 0)
                }
                resolver.update(uri, finalizar, null, null)

                ResultadoGuardadoPkm(
                    exitoso = true,
                    rutaMostrada = "Documentos/CreadorFormularios/$nombreArchivo",
                    uri = uri
                )
            } catch (_: Exception) {
                resolver.delete(uri, null, null)
                ResultadoGuardadoPkm(false, "Documentos/CreadorFormularios/$nombreArchivo")
            }
        } else {
            val carpeta = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "CreadorFormularios"
            )
            if (!carpeta.exists()) {
                carpeta.mkdirs()
            }

            val archivo = File(carpeta, nombreArchivo)
            archivo.writeText(codigoPkm, Charsets.UTF_8)

            ResultadoGuardadoPkm(
                exitoso = true,
                rutaMostrada = archivo.absolutePath,
                uri = Uri.fromFile(archivo)
            )
        }
    } catch (_: Exception) {
        ResultadoGuardadoPkm(false, "Documentos/CreadorFormularios/$nombreArchivo")
    }
}

private fun abrirArchivoGuardado(context: Context, uri: Uri) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "text/plain")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Abrir archivo PKM"))
    } catch (_: Exception) {
        // Si no existe app para abrir el archivo, omitimos la acción.
    }
}