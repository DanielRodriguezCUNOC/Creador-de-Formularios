package com.example.proyecto1_compi1_1s_2026.data.remote.api

import com.example.proyecto1_compi1_1s_2026.data.remote.dto.ApiMessageResponseDto
import com.example.proyecto1_compi1_1s_2026.data.remote.dto.PaginatedFormulariosResponseDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Streaming

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
