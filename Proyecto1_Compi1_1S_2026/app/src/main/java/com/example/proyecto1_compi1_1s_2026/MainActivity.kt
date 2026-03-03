package com.example.proyecto1_compi1_1s_2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.proyecto1_compi1_1s_2026.ui.navigation.AppNavigation
import com.example.proyecto1_compi1_1s_2026.ui.theme.Proyecto1_Compi1_1S_2026Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Proyecto1_Compi1_1S_2026Theme {
                AppNavigation()
            }
        }
    }
}