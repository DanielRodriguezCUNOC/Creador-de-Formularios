package com.example.proyecto1_compi1_1s_2026.ui.screens

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1_1s_2026.data.remote.client.FormApiClient
import com.example.proyecto1_compi1_1s_2026.data.remote.dto.FormularioMetadataDto
import com.example.proyecto1_compi1_1s_2026.ui.util.decodeTextRobust
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedFormsScreen(
    apiBaseUrl: String,
    onContestarFormulario: (nombre: String, contenidoPkm: String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var cargando by remember { mutableStateOf(true) }
    var formularios by remember { mutableStateOf<List<FormularioMetadataDto>>(emptyList()) }

    LaunchedEffect(apiBaseUrl) {
        if (apiBaseUrl.isBlank()) {
            formularios = emptyList()
            cargando = false
            return@LaunchedEffect
        }

        formularios = cargarFormulariosDesdeApi(apiBaseUrl)
        cargando = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Formularios guardados") },
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
        if (cargando) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Cargando formularios...")
            }
        } else if (apiBaseUrl.isBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Debes configurar la URL de API (ngrok) en el menú antes de listar formularios.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else if (formularios.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No hay formularios disponibles en la API.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(formularios, key = { it.id }) { formulario ->
                    FormularioGuardadoCard(
                        formulario = formulario,
                        onContestar = {
                            coroutineScope.launch {
                                val contenido = descargarPkmDesdeApi(apiBaseUrl, formulario.id)
                                if (contenido == null) {
                                    snackbarHostState.showSnackbar(
                                        message = "No se pudo cargar formulario id=${formulario.id}",
                                        duration = SnackbarDuration.Short
                                    )
                                    return@launch
                                }

                                val nombre = formulario.nombreFormulario ?: "formulario_${formulario.id}"
                                onContestarFormulario(nombre, contenido)
                            }
                        },
                        onDescargar = {
                            coroutineScope.launch {
                                val contenido = descargarPkmDesdeApi(apiBaseUrl, formulario.id)
                                if (contenido == null) {
                                    snackbarHostState.showSnackbar(
                                        message = "No se pudo descargar formulario id=${formulario.id}",
                                        duration = SnackbarDuration.Short
                                    )
                                    return@launch
                                }

                                val nombreArchivo = (formulario.nombreFormulario ?: "formulario_${formulario.id}") + ".pkm"
                                val descargado = guardarEnDescargas(
                                    context = context,
                                    nombreArchivo = nombreArchivo,
                                    contenido = contenido
                                )

                                snackbarHostState.showSnackbar(
                                    message = if (descargado) {
                                        "Descargado en Descargas/$nombreArchivo"
                                    } else {
                                        "No se pudo guardar $nombreArchivo"
                                    },
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FormularioGuardadoCard(
    formulario: FormularioMetadataDto,
    onContestar: () -> Unit,
    onDescargar: () -> Unit
) {
    val fecha = remember(formulario.fechaCreacion) {
        formulario.fechaCreacion ?: "Sin fecha"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = formulario.nombreFormulario ?: "formulario_${formulario.id}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Autor: ${formulario.autor ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Fecha: $fecha",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tamano: ${formulario.tamanioBytes} bytes",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(onClick = onContestar) {
                Text("Contestar")
            }
            Button(onClick = onDescargar) {
                Text("Descargar PKM")
            }
        }
    }
}

private suspend fun cargarFormulariosDesdeApi(baseUrl: String): List<FormularioMetadataDto> {
    return try {
        val service = FormApiClient.create(baseUrl)
        val response = service.listarFormularios(page = 1, size = 50)
        if (!response.isSuccessful) return emptyList()
        response.body()?.formularios.orEmpty()
    } catch (_: Exception) {
        emptyList()
    }
}

private suspend fun descargarPkmDesdeApi(baseUrl: String, id: Long): String? {
    return try {
        val service = FormApiClient.create(baseUrl)
        val response = service.descargarFormularioPorId(id)
        if (!response.isSuccessful) return null
        val bytes = response.body()?.bytes() ?: return null
        decodeTextRobust(bytes)
    } catch (_: Exception) {
        null
    }
}

private fun guardarEnDescargas(context: Context, nombreArchivo: String, contenido: String): Boolean {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: return false

            try {
                resolver.openOutputStream(uri)?.use { salida ->
                    salida.write(contenido.toByteArray(Charsets.UTF_8))
                    salida.flush()
                } ?: throw IllegalStateException("No se pudo abrir stream")

                val finalizar = ContentValues().apply {
                    put(MediaStore.MediaColumns.IS_PENDING, 0)
                }
                resolver.update(uri, finalizar, null, null)
                true
            } catch (_: Exception) {
                resolver.delete(uri, null, null)
                false
            }
        } else {
            val carpeta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!carpeta.exists()) {
                carpeta.mkdirs()
            }
            val archivo = File(carpeta, nombreArchivo)
            archivo.writeText(contenido, Charsets.UTF_8)
            true
        }
    } catch (_: Exception) {
        false
    }
}
