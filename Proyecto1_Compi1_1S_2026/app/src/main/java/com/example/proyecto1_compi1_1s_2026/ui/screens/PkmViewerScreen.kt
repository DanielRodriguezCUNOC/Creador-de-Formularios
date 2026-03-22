package com.example.proyecto1_compi1_1s_2026.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Formulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo
import com.example.proyecto1_compi1_1s_2026.ui.integration.PkmUiCoordinator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PkmViewerScreen(
    titulo: String,
    codigoPkm: String,
    onBack: () -> Unit,
    onContestarExitoso: (Formulario) -> Unit,
    onViewErrors: (List<ErrorInfo>) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val pkmCoordinator = remember { PkmUiCoordinator() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PKM: $titulo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
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
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, Color(0xFF404040), RoundedCornerShape(8.dp))
                    .background(SyntaxHighlighter.bgColor, RoundedCornerShape(8.dp))
            ) {
                val verticalScroll = rememberScrollState()
                val horizontalScroll = rememberScrollState()

                Text(
                    text = SyntaxHighlighter.highlight(codigoPkm),
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = SyntaxHighlighter.textColor,
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(verticalScroll)
                        .horizontalScroll(horizontalScroll)
                        .padding(12.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val resultado = pkmCoordinator.analizar(codigoPkm)
                        if (!resultado.exitoso || resultado.formulario == null) {
                            onViewErrors(resultado.errores)
                            return@Button
                        }

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Formulario PKM cargado para contestar",
                                duration = SnackbarDuration.Short
                            )
                        }
                        onContestarExitoso(resultado.formulario)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Contestar")
                }
            }
        }
    }
}
