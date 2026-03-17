package com.example.proyecto1_compi1_1s_2026.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Formulario
import com.example.proyecto1_compi1_1s_2026.ui.screens.ErrorScreen
import com.example.proyecto1_compi1_1s_2026.ui.screens.MainScreen
import com.example.proyecto1_compi1_1s_2026.ui.screens.MenuScreen
import com.example.proyecto1_compi1_1s_2026.ui.screens.FillFormScreen


sealed class Screen {
    object Main : Screen()
    object Menu : Screen()
    object  FillForm: Screen()
    object Errors : Screen()
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }
    var erroresActuales by remember { mutableStateOf<List<ErrorInfo>>(emptyList()) }
    var editorValue by remember { mutableStateOf(TextFieldValue("")) }
    var formularioPreview by remember { mutableStateOf<Formulario?>(null) }
    var mostrarFormularioPreview by remember { mutableStateOf(false) }

    when (currentScreen) {
        is Screen.Main -> MainScreen(
            editorValue = editorValue,
            onEditorValueChange = { editorValue = it },
            formularioActual = formularioPreview,
            mostrarFormulario = mostrarFormularioPreview,
            onFormularioActualChange = { formularioPreview = it },
            onMostrarFormularioChange = { mostrarFormularioPreview = it },
            onMenuClick = { currentScreen = Screen.Menu },
            onFinalize = { formulario ->
                formularioPreview = formulario
                currentScreen = Screen.FillForm
            },
            onViewErrors = { errores ->
                erroresActuales = errores
                currentScreen = Screen.Errors
            }
        )
        is Screen.Menu -> MenuScreen(
            onBack = { currentScreen = Screen.Main }
        )
        is Screen.FillForm -> FillFormScreen(
            formulario = formularioPreview,
            onBack = { currentScreen = Screen.Main }
        )
        is Screen.Errors -> ErrorScreen(
            errores = erroresActuales,
            onBack = { currentScreen = Screen.Main }
        )
    }
}
