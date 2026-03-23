package com.example.proyecto1_compi1_1s_2026.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1_1s_2026.ui.util.decodeTextRobust
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    codigoPkmActual: String,
    apiBaseUrl: String,
    onApiBaseUrlChange: (String) -> Unit,
    onSavedFormsClick: () -> Unit,
    onAbrirArchivoPkm: (nombre: String, contenido: String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var mostrarDialogoUrl by remember { mutableStateOf(false) }
    var borradorUrl by remember(apiBaseUrl) { mutableStateOf(apiBaseUrl) }
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

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) {
            return@rememberLauncherForActivityResult
        }

        val contenido = leerTextoDesdeUri(context, uri)
        if (contenido.isNullOrBlank()) {
            Toast.makeText(context, "No se pudo leer el archivo seleccionado", Toast.LENGTH_LONG).show()
            return@rememberLauncherForActivityResult
        }

        val nombre = obtenerNombreArchivo(context, uri) ?: generarNombreArchivoPkm()
        onAbrirArchivoPkm(nombre, contenido)
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

            Text(
                text = if (apiBaseUrl.isBlank()) {
                    "API sin configurar"
                } else {
                    "API actual: $apiBaseUrl"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            MenuOptionButton(label = "Importar plantilla")   { /* TODO */ }
            MenuOptionButton(label = "Abrir archivo") {
                openDocumentLauncher.launch(arrayOf("text/plain", "*/*"))
            }
            MenuOptionButton(label = "Formularios guardados") { onSavedFormsClick() }
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

            MenuOptionButton(label = "Configurar URL API (ngrok)") {
                borradorUrl = apiBaseUrl
                mostrarDialogoUrl = true
            }
            MenuOptionButton(label = "Acerca de")          { /* TODO */ }
        }
    }

    if (mostrarDialogoUrl) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoUrl = false },
            title = { Text("Configurar URL de API") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ingresa la URL base de ngrok. Ejemplo: https://xxxx.ngrok-free.app")
                    OutlinedTextField(
                        value = borradorUrl,
                        onValueChange = { borradorUrl = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("URL base") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onApiBaseUrlChange(borradorUrl)
                        mostrarDialogoUrl = false
                    }
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoUrl = false }) {
                    Text("Cancelar")
                }
            }
        )
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

private fun leerTextoDesdeUri(context: Context, uri: Uri): String? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { entrada ->
            decodeTextRobust(entrada.readBytes())
        }
    } catch (_: Exception) {
        null
    }
}

private fun obtenerNombreArchivo(context: Context, uri: Uri): String? {
    return try {
        val cursor = context.contentResolver.query(uri, null, null, null, null) ?: return null
        cursor.use {
            val index = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
            if (index >= 0 && it.moveToFirst()) it.getString(index) else null
        }
    } catch (_: Exception) {
        null
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
