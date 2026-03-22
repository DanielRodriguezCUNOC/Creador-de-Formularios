package com.example.proyecto1_compi1_1s_2026.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import com.example.proyecto1_compi1_1s_2026.data.config.ApiConfigStore
import com.example.proyecto1_compi1_1s_2026.data.remote.client.FormApiClient
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Formulario
import com.example.proyecto1_compi1_1s_2026.ui.screens.ErrorScreen
import com.example.proyecto1_compi1_1s_2026.ui.screens.MainScreen
import com.example.proyecto1_compi1_1s_2026.ui.screens.MenuScreen
import com.example.proyecto1_compi1_1s_2026.ui.screens.FillFormScreen
import com.example.proyecto1_compi1_1s_2026.ui.screens.PkmViewerScreen
import com.example.proyecto1_compi1_1s_2026.ui.screens.SavedFormsScreen


sealed class Screen {
    object Main : Screen()
    object Menu : Screen()
    object  FillForm: Screen()
    object PkmViewer : Screen()
    object SavedForms : Screen()
    object Errors : Screen()
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val apiConfigStore = remember { ApiConfigStore(context) }

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }
    var erroresActuales by remember { mutableStateOf<List<ErrorInfo>>(emptyList()) }
    var editorValue by remember { mutableStateOf(TextFieldValue("")) }
    var formularioPreview by remember { mutableStateOf<Formulario?>(null) }
    var mostrarFormularioPreview by remember { mutableStateOf(false) }
    var codigoPkmActual by remember { mutableStateOf("") }
    var pkmSoloLecturaActual by remember { mutableStateOf("") }
    var pkmTituloActual by remember { mutableStateOf("Archivo PKM") }
    var apiBaseUrl by remember { mutableStateOf(apiConfigStore.getBaseUrl()) }
    var pantallaRetornoErrores by remember { mutableStateOf<Screen>(Screen.Main) }
    var pantallaRetornoPkm by remember { mutableStateOf<Screen>(Screen.Menu) }

    when (currentScreen) {
        is Screen.Main -> MainScreen(
            editorValue = editorValue,
            onEditorValueChange = { editorValue = it },
            formularioActual = formularioPreview,
            mostrarFormulario = mostrarFormularioPreview,
            onFormularioActualChange = { formularioPreview = it },
            onMostrarFormularioChange = { mostrarFormularioPreview = it },
            onMenuClick = { currentScreen = Screen.Menu },
            onFinalize = { formulario, _ ->
                formularioPreview = formulario
                currentScreen = Screen.FillForm
            },
            onViewErrors = { errores ->
                erroresActuales = errores
                pantallaRetornoErrores = Screen.Main
                currentScreen = Screen.Errors
            },
            onSubirPkmApi = { codigo, autor, nombreFormulario ->
                if (apiBaseUrl.isBlank()) {
                    false
                } else {
                    FormApiClient.subirPkm(apiBaseUrl, codigo, autor, nombreFormulario)
                }
            },
            onCodigoPkmGenerado = { codigo ->
                codigoPkmActual = codigo
            }
        )
        is Screen.Menu -> MenuScreen(
            codigoPkmActual = codigoPkmActual,
            apiBaseUrl = apiBaseUrl,
            onApiBaseUrlChange = { nuevaUrl ->
                apiBaseUrl = nuevaUrl
                apiConfigStore.setBaseUrl(nuevaUrl)
            },
            onSavedFormsClick = { currentScreen = Screen.SavedForms },
            onAbrirArchivoPkm = { nombre, contenido ->
                pkmTituloActual = nombre
                pkmSoloLecturaActual = contenido
                pantallaRetornoPkm = Screen.Menu
                currentScreen = Screen.PkmViewer
            },
            onBack = { currentScreen = Screen.Main }
        )
        is Screen.FillForm -> FillFormScreen(
            formulario = formularioPreview,
            onBack = { currentScreen = Screen.Main }
        )
        is Screen.SavedForms -> SavedFormsScreen(
            apiBaseUrl = apiBaseUrl,
            onContestarFormulario = { nombre, contenido ->
                pkmTituloActual = nombre
                pkmSoloLecturaActual = contenido
                pantallaRetornoPkm = Screen.SavedForms
                currentScreen = Screen.PkmViewer
            },
            onBack = { currentScreen = Screen.Menu }
        )
        is Screen.PkmViewer -> PkmViewerScreen(
            titulo = pkmTituloActual,
            codigoPkm = pkmSoloLecturaActual,
            onBack = { currentScreen = pantallaRetornoPkm },
            onViewErrors = { errores ->
                erroresActuales = errores
                pantallaRetornoErrores = Screen.PkmViewer
                currentScreen = Screen.Errors
            }
        )
        is Screen.Errors -> ErrorScreen(
            errores = erroresActuales,
            onBack = { currentScreen = pantallaRetornoErrores }
        )
    }
}
