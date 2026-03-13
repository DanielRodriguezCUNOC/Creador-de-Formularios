package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import android.util.Log
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Servicio simple para consumir PokéAPI sin librerías externas.
 *
 * Diseño imperativo:
 * - Cache en memoria por id para no repetir consultas
 * - HTTP con HttpURLConnection
 * - Ejecución en hilo aparte para evitar NetworkOnMainThreadException
 */
object PokemonApiService {

    private const val TAG = "PokemonApiService"

    private const val BASE_URL = "https://pokeapi.co/api/v2/pokemon/"
    private const val CONNECT_TIMEOUT_MS = 5_000
    private const val READ_TIMEOUT_MS = 5_000

    // Cache básico: id -> nombre
    private val cacheNombres = HashMap<Int, String>()

    /**
     * Obtiene nombres de pokémon en el rango [inicio, fin].
     * Si una consulta falla, agrega un fallback "pokemon-{id}".
     */
    fun obtenerNombresEnRango(inicio: Int, fin: Int): List<String> {
        val resultado = mutableListOf<String>()

        Log.d(TAG, "Solicitando rango PokéAPI: $inicio..$fin")

        if (inicio > fin) {
            Log.w(TAG, "Rango inválido: inicio > fin ($inicio > $fin)")
            return resultado
        }

        // Ejecutar el trabajo de red en un hilo aparte.
        val hilo = Thread {
            var id = inicio
            while (id <= fin) {
                val nombre = obtenerNombrePorId(id)
                if (nombre != null && nombre.isNotBlank()) {
                    resultado.add(nombre)
                } else {
                    // Fallback amigable cuando falla una petición puntual.
                    resultado.add("pokemon-$id")
                    Log.w(TAG, "No se pudo obtener id=$id. Usando fallback pokemon-$id")
                }
                id++
            }
        }

        hilo.start()
        try {
            hilo.join()
        } catch (_: InterruptedException) {
            // Si interrumpen, devolvemos lo que se haya logrado obtener.
            Log.w(TAG, "Hilo de consulta interrumpido")
        }

        Log.d(TAG, "Resultado PokéAPI ($inicio..$fin): $resultado")

        return resultado
    }

    /**
     * Obtiene nombre de un pokémon por id.
     * Retorna null si falla cualquier paso.
     */
    private fun obtenerNombrePorId(id: Int): String? {
        // 1) Revisar cache
        if (cacheNombres.containsKey(id)) {
            Log.d(TAG, "Cache hit id=$id -> ${cacheNombres[id]}")
            return cacheNombres[id]
        }

        var connection: HttpURLConnection? = null

        return try {
            // 2) Abrir conexión HTTP
            val url = URL(BASE_URL + id)
            connection = (url.openConnection() as HttpURLConnection)
            connection.requestMethod = "GET"
            connection.connectTimeout = CONNECT_TIMEOUT_MS
            connection.readTimeout = READ_TIMEOUT_MS
            connection.doInput = true

            // 3) Validar respuesta
            val status = connection.responseCode
            if (status != HttpURLConnection.HTTP_OK) {
                Log.w(TAG, "HTTP no OK para id=$id. status=$status")
                return null
            }

            // 4) Leer cuerpo completo
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            if (body.isBlank()) {
                return null
            }

            // 5) Parsear JSON y extraer campo "name"
            val json = JSONObject(body)
            val nombre = json.optString("name", "").trim()
            if (nombre.isBlank()) {
                Log.w(TAG, "Respuesta sin nombre para id=$id")
                return null
            }

            // 6) Guardar en cache y retornar
            cacheNombres[id] = nombre
            Log.d(TAG, "Pokémon obtenido id=$id -> $nombre")
            nombre
        } catch (e: Exception) {
            Log.e(TAG, "Error consultando id=$id", e)
            null
        } finally {
            connection?.disconnect()
        }
    }
}
