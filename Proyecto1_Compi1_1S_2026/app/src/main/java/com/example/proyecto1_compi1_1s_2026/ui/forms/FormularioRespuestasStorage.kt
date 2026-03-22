package com.example.proyecto1_compi1_1s_2026.ui.forms

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ResultadoGuardadoRespuestas(
    val exitoso: Boolean,
    val rutaMostrada: String,
    val uri: Uri? = null
)

fun guardarRespuestasEnDocumentos(
    context: Context,
    contenidoPkmRespuestas: String
): ResultadoGuardadoRespuestas {
    if (contenidoPkmRespuestas.isBlank()) {
        return ResultadoGuardadoRespuestas(
            exitoso = false,
            rutaMostrada = "Documentos/CreadorFormularios"
        )
    }

    val nombreArchivo = "respuestas_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pkm"

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
                ?: return ResultadoGuardadoRespuestas(false, "Documentos/CreadorFormularios/$nombreArchivo")

            try {
                resolver.openOutputStream(uri)?.use { salida ->
                    salida.write(contenidoPkmRespuestas.toByteArray(Charsets.UTF_8))
                    salida.flush()
                } ?: throw IllegalStateException("No se pudo abrir stream de escritura")

                val finalizar = ContentValues().apply {
                    put(MediaStore.MediaColumns.IS_PENDING, 0)
                }
                resolver.update(uri, finalizar, null, null)

                ResultadoGuardadoRespuestas(
                    exitoso = true,
                    rutaMostrada = "Documentos/CreadorFormularios/$nombreArchivo",
                    uri = uri
                )
            } catch (_: Exception) {
                resolver.delete(uri, null, null)
                ResultadoGuardadoRespuestas(false, "Documentos/CreadorFormularios/$nombreArchivo")
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
            archivo.writeText(contenidoPkmRespuestas, Charsets.UTF_8)

            ResultadoGuardadoRespuestas(
                exitoso = true,
                rutaMostrada = archivo.absolutePath,
                uri = Uri.fromFile(archivo)
            )
        }
    } catch (_: Exception) {
        ResultadoGuardadoRespuestas(false, "Documentos/CreadorFormularios/$nombreArchivo")
    }
}
