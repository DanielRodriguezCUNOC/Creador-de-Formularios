package com.example.proyecto1_compi1_1s_2026.data.remote.client

import com.example.proyecto1_compi1_1s_2026.data.remote.api.FormApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormApiClient {

    private val httpClient = OkHttpClient.Builder().build()

    fun create(baseUrl: String): FormApiService {
        val normalizada = normalizarBaseUrl(baseUrl)
        val retrofit = Retrofit.Builder()
            .baseUrl(normalizada)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(FormApiService::class.java)
    }

    fun normalizarBaseUrl(baseUrl: String): String {
        val trimmed = baseUrl.trim()
        require(trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            "La URL debe iniciar con http:// o https://"
        }
        return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
    }

    suspend fun subirPkm(
        baseUrl: String,
        codigoPkm: String,
        autor: String = "AndroidApp",
        nombreFormulario: String = ""
    ): Boolean {
        if (codigoPkm.isBlank()) return false

        val service = create(baseUrl)
        val nombre = if (nombreFormulario.isBlank()) {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            "formulario_$timestamp"
        } else {
            nombreFormulario
        }

        val autorBody = autor.toRequestBody("text/plain".toMediaType())
        val nombreBody = nombre.toRequestBody("text/plain".toMediaType())
        val archivoBody = codigoPkm.toByteArray(Charsets.UTF_8)
            .toRequestBody("text/plain".toMediaType())

        val archivoPart = MultipartBody.Part.createFormData(
            "formulario",
            "$nombre.pkm",
            archivoBody
        )

        return try {
            val response = service.guardarFormulario(autorBody, nombreBody, archivoPart)
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }
}
