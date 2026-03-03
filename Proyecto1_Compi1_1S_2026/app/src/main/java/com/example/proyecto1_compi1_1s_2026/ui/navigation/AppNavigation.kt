package com.example.proyecto1_compi1_1s_2026.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.proyecto1_compi1_1s_2026.ui.screens.MainScreen
import com.example.proyecto1_compi1_1s_2026.ui.screens.MenuScreen
import com.example.proyecto1_compi1_1s_2026.ui.screens.FillForm


sealed class Screen {
    object Main : Screen()
    object Menu : Screen()
    object  FillForm: Screen()
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }
    var formText by remember { mutableStateOf("") }

    when (currentScreen) {
        is Screen.Main -> MainScreen(
            onMenuClick = { currentScreen = Screen.Menu },
            onFinalize = { text ->
                formText = text
                currentScreen = Screen.FillForm
            }
        )
        is Screen.Menu -> MenuScreen(
            onBack = { currentScreen = Screen.Main }
        )
        is Screen.FillForm -> FillForm(
            formText = formText,
            onBack = { currentScreen = Screen.Main }
        )
    }
}
