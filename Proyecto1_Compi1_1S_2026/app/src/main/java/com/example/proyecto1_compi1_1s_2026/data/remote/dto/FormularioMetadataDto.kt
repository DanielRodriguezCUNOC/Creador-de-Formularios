package com.example.proyecto1_compi1_1s_2026.data.remote.dto

data class FormularioMetadataDto(
    val id: Long,
    val nombreFormulario: String? = null,
    val autor: String? = null,
    val fechaCreacion: String? = null,
    val tamanioBytes: Int = 0
)
