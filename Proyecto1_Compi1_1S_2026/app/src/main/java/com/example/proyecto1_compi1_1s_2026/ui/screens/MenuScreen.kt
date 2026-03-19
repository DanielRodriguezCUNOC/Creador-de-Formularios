package com.example.proyecto1_compi1_1s_2026.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    codigoPkmActual: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri: Uri? ->
        if (uri == null) {
            return@rememberLauncherForActivityResult
        }

        val guardadoExitoso = guardarTextoEnUri(context, uri, codigoPkmActual)
        if (guardadoExitoso) {
            Toast.makeText(context, "Archivo .pkm guardado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No se pudo guardar el archivo", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menú") },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Opciones",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            MenuOptionButton(label = "Importar plantilla")   { /* TODO */ }
            MenuOptionButton(label = "Abrir archivo")      { /* TODO */ }
            MenuOptionButton(label = "Guardar") {
                if (codigoPkmActual.isBlank()) {
                    Toast.makeText(
                        context,
                        "Primero analiza o finaliza para generar PKM",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    createDocumentLauncher.launch(generarNombreArchivoPkm())
                }
            }
            MenuOptionButton(label = "Exportar PDF")       { /* TODO */ }

            HorizontalDivider()

            MenuOptionButton(label = "Configuración")      { /* TODO */ }
            MenuOptionButton(label = "Acerca de")          { /* TODO */ }
        }
    }
}

private fun generarNombreArchivoPkm(): String {
    val formato = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    return "formulario_${formato.format(Date())}.pkm"
}

private fun guardarTextoEnUri(context: Context, uri: Uri, contenido: String): Boolean {
    return try {
        context.contentResolver.openOutputStream(uri)?.use { salida ->
            salida.write(contenido.toByteArray(Charsets.UTF_8))
            salida.flush()
        }
        true
    } catch (_: Exception) {
        false
    }
}

@Composable
private fun MenuOptionButton(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}
