package com.example.proyecto1_compi1_1s_2026.data.remote.dto

data class PaginatedFormulariosResponseDto(
    val page: Int = 1,
    val size: Int = 10,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val formularios: List<FormularioMetadataDto> = emptyList()
)
