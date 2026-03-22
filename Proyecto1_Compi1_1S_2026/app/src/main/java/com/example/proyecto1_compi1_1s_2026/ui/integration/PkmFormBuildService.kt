package com.example.proyecto1_compi1_1s_2026.ui.integration

import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.ElementoFormulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.Formulario
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaAbierta
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaDesplegable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaSeleccionMultiple
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaSeleccionUnica
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TipoError

data class ResultadoConstruccionPkm(
    val formulario: Formulario? = null,
    val erroresSemanticos: List<ErrorInfo> = emptyList()
) {
    val exitoso: Boolean
        get() = erroresSemanticos.isEmpty() && formulario != null
}

/**
 * reconstruye preguntas principales desde etiquetas PKM.
 */
class PkmFormBuildService {

    fun construir(codigoPkm: String): ResultadoConstruccionPkm {
        val errores = mutableListOf<ErrorInfo>()
        val elementos = mutableListOf<ElementoFormulario>()

        val codigoSinMetadata = quitarBloqueMetadata(codigoPkm)
        var indiceActual = 0

        while (indiceActual < codigoSinMetadata.length) {
            val coincidencia = REGEX_COMPONENTE.find(codigoSinMetadata, indiceActual) ?: break
            val tipo = coincidencia.groupValues[1].lowercase()
            val inicioArgumentos = coincidencia.range.last + 1
            val finTag = buscarFinTag(codigoSinMetadata, inicioArgumentos)

            if (finTag < 0) {
                errores.add(
                    ErrorInfo(
                        tipo = TipoError.SEMANTICO,
                        mensaje = "Etiqueta <$tipo> sin cierre de argumentos",
                        linea = 0,
                        columna = 0
                    )
                )
                break
            }

            val argumentoCrudo = codigoSinMetadata.substring(inicioArgumentos, finTag)
            val argumentoLimpio = limpiarCierreAutocontenido(argumentoCrudo)
            val elemento = crearElementoDesdeTag(tipo, argumentoLimpio, errores)
            if (elemento != null) {
                elementos.add(elemento)
            }

            indiceActual = finTag + 1
        }

        if (elementos.isEmpty() && errores.isEmpty()) {
            errores.add(
                ErrorInfo(
                    tipo = TipoError.SEMANTICO,
                    mensaje = "No se encontraron componentes PKM para contestar",
                    linea = 0,
                    columna = 0
                )
            )
        }

        if (errores.isNotEmpty()) {
            return ResultadoConstruccionPkm(erroresSemanticos = errores)
        }

        return ResultadoConstruccionPkm(formulario = Formulario(elementos = elementos))
    }

    private fun crearElementoDesdeTag(
        tipo: String,
        argumentosTag: String,
        errores: MutableList<ErrorInfo>
    ): ElementoFormulario? {
        val partes = separarTopLevel(argumentosTag)

        return when (tipo) {
            "open" -> crearOpen(partes, errores)
            "drop" -> crearDrop(partes, errores)
            "select" -> crearSelect(partes, errores)
            "multiple" -> crearMultiple(partes, errores)
            else -> null
        }
    }

    private fun crearOpen(partes: List<String>, errores: MutableList<ErrorInfo>): ElementoFormulario? {
        if (partes.size < 3) {
            agregarError(errores, "open requiere width,height,label")
            return null
        }

        val width = parseFloat(partes[0], errores, "width de open") ?: return null
        val height = parseFloat(partes[1], errores, "height de open") ?: return null
        val label = parseCadena(partes[2])

        return PreguntaAbierta(width = width, height = height, label = label)
    }

    private fun crearDrop(partes: List<String>, errores: MutableList<ErrorInfo>): ElementoFormulario? {
        if (partes.size < 5) {
            agregarError(errores, "drop requiere width,height,label,opciones,correcta")
            return null
        }

        val width = parseFloat(partes[0], errores, "width de drop") ?: return null
        val height = parseFloat(partes[1], errores, "height de drop") ?: return null
        val label = parseCadena(partes[2])
        val opciones = parseListaStrings(partes[3], errores, "opciones de drop")
        val correctaRaw = parseEntero(partes[4], errores, "correcta de drop") ?: -1
        val correcta = normalizarIndice(correctaRaw, opciones.size, "correcta de drop", errores)

        return PreguntaDesplegable(
            width = width,
            height = height,
            label = label,
            opciones = opciones,
            correcta = correcta
        )
    }

    private fun crearSelect(partes: List<String>, errores: MutableList<ErrorInfo>): ElementoFormulario? {
        if (partes.size < 5) {
            agregarError(errores, "select requiere width,height,label,opciones,correcta")
            return null
        }

        val width = parseFloat(partes[0], errores, "width de select") ?: return null
        val height = parseFloat(partes[1], errores, "height de select") ?: return null
        val label = parseCadena(partes[2])
        val opciones = parseListaStrings(partes[3], errores, "opciones de select")
        val correctaRaw = parseEntero(partes[4], errores, "correcta de select") ?: -1
        val correcta = normalizarIndice(correctaRaw, opciones.size, "correcta de select", errores)

        return PreguntaSeleccionUnica(
            width = width,
            height = height,
            label = label,
            opciones = opciones,
            correcta = correcta
        )
    }

    private fun crearMultiple(partes: List<String>, errores: MutableList<ErrorInfo>): ElementoFormulario? {
        if (partes.size < 5) {
            agregarError(errores, "multiple requiere width,height,label,opciones,correctas")
            return null
        }

        val width = parseFloat(partes[0], errores, "width de multiple") ?: return null
        val height = parseFloat(partes[1], errores, "height de multiple") ?: return null
        val label = parseCadena(partes[2])
        val opciones = parseListaStrings(partes[3], errores, "opciones de multiple")
        val correctasRaw = parseListaEnteros(partes[4], errores)

        val correctasValidas = mutableListOf<Int>()
        for (indice in correctasRaw) {
            val indiceNormalizado = normalizarIndice(indice, opciones.size, "indice de correctas en multiple", errores)
            if (indiceNormalizado != null) {
                correctasValidas.add(indiceNormalizado)
            }
        }

        return PreguntaSeleccionMultiple(
            width = width,
            height = height,
            label = label,
            opciones = opciones,
            correctas = correctasValidas.distinct()
        )
    }

    private fun parseFloat(valor: String, errores: MutableList<ErrorInfo>, campo: String): Float? {
        val numero = valor.trim().toFloatOrNull()
        if (numero == null) {
            agregarError(errores, "Valor invalido en $campo: ${valor.trim()}")
            return null
        }
        return numero
    }

    private fun parseEntero(valor: String, errores: MutableList<ErrorInfo>, campo: String): Int? {
        val numero = valor.trim().toIntOrNull()
        if (numero == null) {
            agregarError(errores, "Valor invalido en $campo: ${valor.trim()}")
            return null
        }
        return numero
    }

    private fun parseCadena(valor: String): String {
        val texto = valor.trim()
        if (texto.startsWith('"') && texto.endsWith('"') && texto.length >= 2) {
            return texto.substring(1, texto.length - 1)
        }
        return texto
    }

    private fun parseListaStrings(
        valor: String,
        errores: MutableList<ErrorInfo>,
        campo: String
    ): List<String> {
        val contenido = extraerContenidoLlaves(valor)
        if (contenido == null) {
            agregarError(errores, "Formato invalido en $campo")
            return emptyList()
        }

        if (contenido.isBlank()) return emptyList()

        val partes = separarTopLevel(contenido)
        val lista = mutableListOf<String>()
        for (parte in partes) {
            lista.add(parseCadena(parte))
        }
        return lista
    }

    private fun parseListaEnteros(valor: String, errores: MutableList<ErrorInfo>): List<Int> {
        val contenido = extraerContenidoLlaves(valor)
        if (contenido == null) {
            agregarError(errores, "Formato invalido en lista de enteros")
            return emptyList()
        }

        if (contenido.isBlank()) return emptyList()

        val partes = separarTopLevel(contenido)
        val lista = mutableListOf<Int>()
        for (parte in partes) {
            val numero = parte.trim().toIntOrNull()
            if (numero == null) {
                agregarError(errores, "Valor entero invalido en lista: ${parte.trim()}")
            } else {
                lista.add(numero)
            }
        }
        return lista
    }

    private fun normalizarIndice(
        indice: Int,
        totalOpciones: Int,
        campo: String,
        errores: MutableList<ErrorInfo>
    ): Int? {
        if (indice < 0) return null

        if (indice >= totalOpciones) {
            agregarError(errores, "Indice fuera de rango en $campo: $indice")
            return null
        }

        return indice
    }

    private fun buscarFinTag(codigo: String, inicio: Int): Int {
        var i = inicio
        var depthLlaves = 0
        var enCadena = false

        while (i < codigo.length) {
            val c = codigo[i]
            val prev = if (i > 0) codigo[i - 1] else '\u0000'

            if (c == '"' && prev != '\\') {
                enCadena = !enCadena
            } else if (!enCadena) {
                if (c == '{') depthLlaves++
                if (c == '}') depthLlaves--
                if (c == '>' && depthLlaves <= 0) {
                    return i
                }
            }
            i++
        }

        return -1
    }

    private fun separarTopLevel(texto: String): List<String> {
        val partes = mutableListOf<String>()
        val actual = StringBuilder()
        var depthLlaves = 0
        var enCadena = false

        for (i in texto.indices) {
            val c = texto[i]
            val prev = if (i > 0) texto[i - 1] else '\u0000'

            if (c == '"' && prev != '\\') {
                enCadena = !enCadena
                actual.append(c)
                continue
            }

            if (!enCadena) {
                if (c == '{') depthLlaves++
                if (c == '}') depthLlaves--

                if (c == ',' && depthLlaves == 0) {
                    partes.add(actual.toString().trim())
                    actual.clear()
                    continue
                }
            }

            actual.append(c)
        }

        if (actual.isNotBlank()) {
            partes.add(actual.toString().trim())
        }

        return partes
    }

    private fun limpiarCierreAutocontenido(texto: String): String {
        var limpio = texto.trim()
        if (limpio.endsWith("/")) {
            limpio = limpio.substring(0, limpio.length - 1).trim()
        }
        return limpio
    }

    private fun extraerContenidoLlaves(texto: String): String? {
        val limpio = texto.trim()
        if (!limpio.startsWith("{") || !limpio.endsWith("}")) {
            return null
        }
        return limpio.substring(1, limpio.length - 1)
    }

    private fun quitarBloqueMetadata(codigo: String): String {
        val primer = codigo.indexOf("###")
        if (primer < 0) return codigo

        val segundo = codigo.indexOf("###", primer + 3)
        if (segundo < 0) return codigo

        return codigo.substring(segundo + 3)
    }

    private fun agregarError(errores: MutableList<ErrorInfo>, mensaje: String) {
        errores.add(
            ErrorInfo(
                tipo = TipoError.SEMANTICO,
                mensaje = mensaje,
                linea = 0,
                columna = 0
            )
        )
    }

    companion object {
        private val REGEX_COMPONENTE = Regex("<\\s*(open|drop|select|multiple)\\s*=", RegexOption.IGNORE_CASE)
    }
}
