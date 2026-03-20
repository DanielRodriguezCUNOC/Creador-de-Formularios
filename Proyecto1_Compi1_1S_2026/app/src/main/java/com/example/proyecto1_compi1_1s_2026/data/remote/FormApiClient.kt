package com.example.proyecto1_compi1_1s_2026.data.remote

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Streaming
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ApiMessageResponseDto(
    val message: String? = null,
    val idFormulario: Long? = null
)

data class ApiErrorResponseDto(
    val error: String? = null,
    val message: String? = null,
    val detail: String? = null
)

data class FormularioMetadataDto(
    val id: Long,
    val nombreFormulario: String? = null,
    val autor: String? = null,
    val fechaCreacion: String? = null,
    val tamanioBytes: Int = 0
)

data class PaginatedFormulariosResponseDto(
    val page: Int = 1,
    val size: Int = 10,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val formularios: List<FormularioMetadataDto> = emptyList()
)

interface FormApiService {
    @Multipart
    @POST("formularios/guardar")
    suspend fun guardarFormulario(
        @Part("autor") autor: okhttp3.RequestBody,
        @Part("nombreFormulario") nombreFormulario: okhttp3.RequestBody,
        @Part formulario: MultipartBody.Part
    ): Response<ApiMessageResponseDto>

    @GET("formularios/descargar")
    suspend fun listarFormularios(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PaginatedFormulariosResponseDto>

    @Streaming
    @GET("formularios/descargar")
    suspend fun descargarFormularioPorId(
        @Query("id") id: Long
    ): Response<ResponseBody>
}

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
        autor: String = "AndroidApp"
    ): Boolean {
        if (codigoPkm.isBlank()) return false

        val service = create(baseUrl)
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val nombre = "formulario_$timestamp"

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
