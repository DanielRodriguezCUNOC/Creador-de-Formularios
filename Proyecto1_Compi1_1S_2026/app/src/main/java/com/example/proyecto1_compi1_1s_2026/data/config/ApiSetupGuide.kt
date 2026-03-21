package com.example.proyecto1_compi1_1s_2026.data.config

/**
 * GUÍA DE USO: Configuración de la API en tu app Android
 *
 * ============================================================
 * 1. DONDE CONFIGURAR LA URL BASE
 * ============================================================
 *
 * En tu Activity o Composable, obtén una instancia de ApiConfigStore:
 *
 *    val configStore = ApiConfigStore(context)
 *
 * LA PRIMERA VEZ (en una pantalla de configuración):
 *
 *    configStore.setBaseUrl("https://mindi-fascinative-reversedly.ngrok-free.dev/Form-API-1.0-SNAPSHOT")
 *
 * Cada vez que ngrok se reinicia, la URL cambia. Guarda la nueva URL.
 *
 * ============================================================
 * 2. COMO OBTENER LOS ENDPOINTS
 * ============================================================
 *
 *    val endpoints = configStore.getEndpoints() // Retorna ApiEndpoints? (null si no está configurado)
 *
 *    if (endpoints != null) {
 *        val urlGuardar = endpoints.guardarFormulario()
 *        val urlDescargar = endpoints.descargarFormularios(page = 1, size = 10)
 *        val urlContestar = endpoints.contestarFormulario("nombre_formulario")
 *    }
 * ============================================================
 * 4. ENDPOINTS DISPONIBLES
 * ============================================================
 *
 * POST /formularios/guardar
 *   - Guarda un formulario en DB y servidor
 *   - Parámetros: autor, nombreFormulario, formulario (archivo)
 *   - Retorna: {id: Long, nombreArchivoServidor: String}
 *   - Uso: endpoints.guardarFormulario()
 *
 * GET /formularios/descargar?page=1&size=10
 *   - Descarga lista paginada de formularios
 *   - Retorna: lista con metadatos (id, nombre, autor, fecha, tamaño)
 *   - Uso: endpoints.descargarFormularios(page = 1, size = 10)
 *
 * GET /formularios/descargar?id=42
 *   - Descarga un formulario específico en bytes
 *   - Retorna: array de bytes del PKM
 *   - Uso: endpoints.descargarFormularioPorId(42)
 *
 * GET /formularios/contestar/{nombreFormulario}
 *   - Obtiene un formulario para ser contestado
 *   - Parámetro: nombreFormulario (en URL)
 *   - Retorna: array de bytes del PKM
 *   - Uso: endpoints.contestarFormulario("registro_clientes")
 *
 * ============================================================
 * 5. FLUJO TÍPICO DE LA APP
 * ============================================================
 *
 * 1. Pantalla de Configuración (solo una vez):
 *    - Usuario ingresa URL de ngrok
 *    - configStore.setBaseUrl(url)
 *
 * 2. Pantalla de Crear Formulario:
 *    - Usuario crea formulario en XML/JSON
 *    - Se genera código PKM
 *    - Se sube con endpoints.guardarFormulario()
 *    - Se guarda también en almacenamiento local (Downloads)
 *
 * 3. Pantalla de Descargar Formularios:
 *    - Se obtiene lista con endpoints.descargarFormularios()
 *    - Se muestra lista de formularios
 *    - Usuario selecciona uno y descarga con endpoints.descargarFormularioPorId(id)
 *
 * 4. Pantalla de Contestar Formulario:
 *    - Usuario selecciona formulario
 *    - Se obtiene con endpoints.contestarFormulario(nombre)
 *    - Se abre en la app para contestar
 *    - Se guarda respuesta localmente y/o en servidor
 *
 * ============================================================
 */
