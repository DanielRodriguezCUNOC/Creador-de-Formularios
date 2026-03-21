package com.example.proyecto1_compi1_1s_2026.data.config

/**
 * Centraliza todas las rutas de endpoints de la API Form-API.
 * Todos los endpoints se construyen a partir de una URL base configurable.
 *
 * Uso:
 * val baseUrl = "https://mindi-fascinative-reversedly.ngrok-free.dev/Form-API-1.0-SNAPSHOT"
 * val endpoints = ApiEndpoints(baseUrl)
 * val guardarUrl = endpoints.guardarFormulario()
 */
class ApiEndpoints(private val baseUrl: String) {

    private val canonicalBaseUrl: String = normalizarBaseUrl(baseUrl)

    init {
        require(baseUrl.isNotBlank()) { "La URL base no puede estar vacía" }
    }

    private fun ensureSlash(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }

    private fun normalizarBaseUrl(url: String): String {
        val trimmed = url.trim().trimEnd('/')
        val lower = trimmed.lowercase()

        return when {
            lower.contains("/api/v1") -> trimmed
            lower.contains("/form-api-1.0-snapshot") -> "$trimmed/api/v1"
            else -> "$trimmed/Form-API-1.0-SNAPSHOT/api/v1"
        }
    }

    // ============ ENDPOINTS DE FORMULARIOS ============

    /**
     * Endpoint para guardar un formulario en la DB y en el servidor.
     * Método: POST
     * Parámetros: autor (String), nombreFormulario (String), formulario (File/InputStream)
     * Retorna: ApiMessageResponse con idFormulario y nombreArchivoServidor
     */
    fun guardarFormulario(): String {
        return "${ensureSlash(canonicalBaseUrl)}formularios/guardar"
    }

    /**
     * Endpoint para descargar formularios con paginación.
     * Método: GET
     * Parámetros: page (Int, default=1), size (Int, default=10)
     * Retorna: PaginatedFormulariosResponse
     */
    fun descargarFormularios(page: Int = 1, size: Int = 10): String {
        return "${ensureSlash(canonicalBaseUrl)}formularios/descargar?page=$page&size=$size"
    }

    /**
     * Endpoint para descargar un formulario específico por ID.
     * Método: GET
     * Parámetros: id (Long)
     * Retorna: Bytes del archivo PKM
     */
    fun descargarFormularioPorId(id: Long): String {
        return "${ensureSlash(canonicalBaseUrl)}formularios/descargar?id=$id"
    }

    /**
     * Endpoint para obtener un formulario para contestar (por nombre).
     * Método: GET
     * Parámetros: nombreFormulario (PathParam)
     * Retorna: Bytes del archivo PKM
     */
    fun contestarFormulario(nombreFormulario: String): String {
        return "${ensureSlash(canonicalBaseUrl)}formularios/contestar/${nombreFormulario.trim()}"
    }

    // ============ MÉTODOS AUXILIARES PARA VALIDACIÓN ============

    /**
     * Valida que la URL base sea correcta.
     * Retorna true si es válida, false en caso contrario.
     */
    fun isValidBaseUrl(): Boolean {
        val trimmed = canonicalBaseUrl.trim()
        return trimmed.startsWith("http://") || trimmed.startsWith("https://")
    }

    /**
     * Retorna la URL base configurada.
     */
    fun getBaseUrl(): String = canonicalBaseUrl
}
