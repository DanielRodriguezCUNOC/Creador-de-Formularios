package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

import androidx.compose.ui.graphics.Color
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_expresion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_instruccion.*
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_principal.Visitor
import kotlin.math.pow

// ── Import aliases para resolver conflicto de nombres entre AST y modelos ──
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaAbierta          as ModeloPreguntaAbierta
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaDesplegable      as ModeloPreguntaDesplegable
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaSeleccionUnica   as ModeloPreguntaSeleccionUnica
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.models.PreguntaSeleccionMultiple as ModeloPreguntaSeleccionMultiple

/**
 * Intérprete del AST. Implementa [Visitor]<Any?> para poder evaluar tanto
 * expresiones (devuelven su valor) como instrucciones (ejecutan efectos laterales).
 *
 * Los componentes UI añaden el [ElementoFormulario] construido a la lista interna
 * y también lo devuelven, lo que permite componerlos dentro de secciones anidadas.
 */
class Interprete(private var entornoActual: TablaSimbolos) : Visitor<Any?> {

    private val elementos = mutableListOf<ElementoFormulario>()
    private val errores   = mutableListOf<ErrorInfo>()

    // ─── Punto de entrada ────────────────────────────────────────────────────

    fun interpretar(instrucciones: List<NodoInstruccion>): ResultadoInterpretacion {
        elementos.clear()
        errores.clear()
        for (instruccion in instrucciones) {
            instruccion.accept(this)
        }
        return ResultadoInterpretacion(
            formulario = Formulario(elementos.toList()),
            errores    = errores.toList()
        )
    }

    // ─── Expresiones ─────────────────────────────────────────────────────────

    override fun visit(node: NodoLiteral): Any? = node.valor

    override fun visit(node: NodoAccesoVariable): Any? {
        return try {
            entornoActual.obtenerVariable(node.id)
        } catch (e: Exception) {
            errores.add(ErrorInfo(TipoError.SEMANTICO, "Variable '${node.id}' no declarada", node.linea, node.columna))
            null
        }
    }

    override fun visit(node: NodoLlamadaApi): Any? {
        // La PokéAPI se resolverá con coroutines en una fase posterior
        if (node.tipo == "pokemon") {
            val inicio = toDouble(node.rangoInicio.accept(this))?.toInt() ?: 0
            val fin    = toDouble(node.rangoFin.accept(this))?.toInt()   ?: 0
            return "pokemon_range:${inicio}:${fin}"
        }
        return null
    }

    override fun visit(node: NodoOperacionUnaria): Any? {
        val v = node.expresion.accept(this)
        return when (node.operador) {
            "-"  -> when (v) {
                is Double -> -v
                is Int    -> -v
                is Number -> -v.toDouble()
                else      -> null
            }
            "~"  -> when (v) {
                is Boolean -> !v
                else       -> null
            }
            else -> v
        }
    }

    override fun visit(node: NodoOperacionBinaria): Any? {
        val izq = node.izq.accept(this)
        val der = node.der.accept(this)
        val izqN = toDouble(izq)
        val derN = toDouble(der)
        return when (node.operador) {
            "+"  -> if (izq is String || der is String) "${izq ?: ""}${der ?: ""}"
                    else izqN?.plus(derN ?: 0.0)
            "-"  -> izqN?.minus(derN ?: 0.0)
            "*"  -> izqN?.times(derN ?: 0.0)
            "/"  -> if (derN != null && derN != 0.0) izqN?.div(derN)
                    else {
                        errores.add(ErrorInfo(TipoError.SEMANTICO, "División por cero", node.linea, node.columna))
                        null
                    }
            "%"  -> izqN?.rem(derN ?: 1.0)
            "^"  -> if (izqN != null && derN != null) izqN.pow(derN) else null
            ">"  -> izqN != null && derN != null && izqN > derN
            ">=" -> izqN != null && derN != null && izqN >= derN
            "<"  -> izqN != null && derN != null && izqN < derN
            "<=" -> izqN != null && derN != null && izqN <= derN
            "==" -> izq == der
            "!!" -> izq != der
            "&&" -> toBool(izq) && toBool(der)
            "||" -> toBool(izq) || toBool(der)
            else -> null
        }
    }

    // ─── Instrucciones de variables ──────────────────────────────────────────

    override fun visit(node: NodoDeclaracion): Any? {
        val valor = if (node.valorInicio != null) {
            node.valorInicio.accept(this)
        } else {
            when (node.tipo) {
                "number" -> 0.0
                "string" -> ""
                else     -> null
            }
        }
        entornoActual.almacenarVariable(node.id, valor ?: "", node.tipo)
        return null
    }

    override fun visit(node: NodoDeclaracionSpecial): Any? {
        // Interpretar el componente de forma aislada para no contaminar el flujo principal
        val elementosPrevios = elementos.toMutableList()
        elementos.clear()
        val componente = node.pregunta.accept(this)
        elementos.clear()
        elementos.addAll(elementosPrevios)
        // Guardar el ElementoFormulario construido para que .draw() lo añada al flujo cuando sea llamado
        entornoActual.almacenarVariable(node.id, componente ?: "", "special")
        return null
    }

    override fun visit(node: NodoAsignacion): Any? {
        val valor = node.nuevoValor.accept(this)
        try {
            entornoActual.reasignarVariable(node.id, valor ?: "")
        } catch (e: Exception) {
            errores.add(ErrorInfo(TipoError.SEMANTICO, e.message ?: "Error de asignación", node.linea, node.columna))
        }
        return null
    }

    // ─── Instrucciones de control de flujo ──────────────────────────────────

    override fun visit(node: NodoSentenciaIf): Any? {
        val condicion = toBool(node.condicion.accept(this))
        val nuevoEntorno = TablaSimbolos(entornoActual)
        val temp = entornoActual
        entornoActual = nuevoEntorno
        if (condicion) {
            node.instruccionesIf.forEach { it.accept(this) }
        } else {
            node.instruccionesElse?.forEach { it.accept(this) }
        }
        entornoActual = temp
        return null
    }

    override fun visit(node: NodoCicloWhile): Any? {
        var iter = 0
        while (toBool(node.condicion.accept(this))) {
            if (++iter > MAX_ITER) {
                errores.add(ErrorInfo(TipoError.SEMANTICO, "WHILE: posible bucle infinito (>$MAX_ITER iteraciones)", node.linea, node.columna))
                break
            }
            val nuevoEntorno = TablaSimbolos(entornoActual)
            val temp = entornoActual
            entornoActual = nuevoEntorno
            node.instruccionesWhile.forEach { it.accept(this) }
            entornoActual = temp
        }
        return null
    }

    override fun visit(node: NodoCicloDoWhile): Any? {
        var iter = 0
        do {
            if (++iter > MAX_ITER) {
                errores.add(ErrorInfo(TipoError.SEMANTICO, "DO-WHILE: posible bucle infinito (>$MAX_ITER iteraciones)", node.linea, node.columna))
                break
            }
            val nuevoEntorno = TablaSimbolos(entornoActual)
            val temp = entornoActual
            entornoActual = nuevoEntorno
            node.instrucciones.forEach { it.accept(this) }
            entornoActual = temp
        } while (toBool(node.condicion.accept(this)))
        return null
    }

    override fun visit(node: NodoCicloFor): Any? {
        val inicio = toDouble(node.rangoInicio.accept(this))?.toInt() ?: 0
        val fin    = toDouble(node.rangoFin.accept(this))?.toInt()   ?: 0
        for (i in inicio..fin) {
            val nuevoEntorno = TablaSimbolos(entornoActual)
            nuevoEntorno.almacenarVariable(node.idVariable, i.toDouble(), "number")
            val temp = entornoActual
            entornoActual = nuevoEntorno
            node.instruccionesFor.forEach { it.accept(this) }
            entornoActual = temp
        }
        return null
    }

    /** Recupera la variable special y añade el [ElementoFormulario] almacenado al formulario. */
    override fun visit(node: NodoDraw): Any? {
        return try {
            val elemento = entornoActual.obtenerVariable(node.idVariableEspecial)
            if (elemento is ElementoFormulario) {
                // TODO: aplicar comodines de node.parametros cuando se implemente
                elementos.add(elemento)
            }
            null
        } catch (e: Exception) {
            errores.add(ErrorInfo(TipoError.SEMANTICO, "Variable '${node.idVariableEspecial}' no declarada", node.linea, node.columna))
            null
        }
    }

    // ─── Componentes UI ──────────────────────────────────────────────────────

    override fun visit(node: ComponenteSeccion): Any? {
        val attrs       = node.atributos
        val width       = toFloat(evalAttr(attrs, "width"))
        val height      = toFloat(evalAttr(attrs, "height"))
        val pointX      = toFloat(evalAttr(attrs, "pointX")) ?: 0f
        val pointY      = toFloat(evalAttr(attrs, "pointY")) ?: 0f
        val orientacion = when ((evalAttr(attrs, "orientation") as? String)?.uppercase()) {
            "HORIZONTAL" -> Orientacion.HORIZONTAL
            else         -> Orientacion.VERTICAL
        }
        val estilos = parsearEstilos(attrs)

        // Recolectar los elementos internos de forma aislada
        val elementosPrevios = elementos.toMutableList()
        elementos.clear()
        node.elementosInternos.forEach { it.accept(this) }
        val internos = elementos.toList()
        elementos.clear()
        elementos.addAll(elementosPrevios)

        val seccion = SeccionFormulario(
            width       = width,
            height      = height,
            pointX      = pointX,
            pointY      = pointY,
            orientacion = orientacion,
            elementos   = internos,
            estilos     = estilos
        )
        elementos.add(seccion)
        return seccion
    }

    override fun visit(node: ComponenteTexto): Any? {
        val attrs    = node.atributos
        val contenido = evalAttr(attrs, "content")?.toString() ?: ""
        val estilos  = parsearEstilos(attrs)
        val texto    = TextoFormulario(contenido = contenido, estilos = estilos)
        elementos.add(texto)
        return texto
    }

    override fun visit(node: PreguntaAbierta): Any? {
        val attrs    = node.atributos
        val label    = evalAttr(attrs, "label")?.toString() ?: ""
        val estilos  = parsearEstilos(attrs)
        val pregunta = ModeloPreguntaAbierta(label = label, estilos = estilos)
        elementos.add(pregunta)
        return pregunta
    }

    override fun visit(node: PreguntaDesplegable): Any? {
        val attrs    = node.atributos
        val label    = evalAttr(attrs, "label")?.toString() ?: ""
        val opciones = evalOpciones(attrs, "options")
        val correcta = toInt(evalAttr(attrs, "correct"))
        val estilos  = parsearEstilos(attrs)
        val pregunta = ModeloPreguntaDesplegable(
            label    = label,
            opciones = opciones,
            correcta = correcta,
            estilos  = estilos
        )
        elementos.add(pregunta)
        return pregunta
    }

    override fun visit(node: PreguntaSeleccionUnica): Any? {
        val attrs    = node.atributos
        val label    = evalAttr(attrs, "label")?.toString() ?: ""
        val opciones = evalOpciones(attrs, "options")
        val correcta = toInt(evalAttr(attrs, "correct"))
        val estilos  = parsearEstilos(attrs)
        val pregunta = ModeloPreguntaSeleccionUnica(
            label    = label,
            opciones = opciones,
            correcta = correcta,
            estilos  = estilos
        )
        elementos.add(pregunta)
        return pregunta
    }

    override fun visit(node: PreguntaSeleccionadaMultiple): Any? {
        val attrs    = node.atributos
        val label    = evalAttr(attrs, "label")?.toString() ?: ""
        val opciones = evalOpciones(attrs, "options")
        @Suppress("UNCHECKED_CAST")
        val correctas = (NodoAtributo.valor(attrs, "correct") as? List<NodoExpresion>)
            ?.map { toInt(it.accept(this)) ?: 0 } ?: emptyList()
        val estilos  = parsearEstilos(attrs)
        val pregunta = ModeloPreguntaSeleccionMultiple(
            label     = label,
            opciones  = opciones,
            correctas = correctas,
            estilos   = estilos
        )
        elementos.add(pregunta)
        return pregunta
    }

    override fun visit(node: ComponenteTabla): Any? {
        val attrs  = node.atributos
        val width  = toFloat(evalAttr(attrs, "width"))
        val height = toFloat(evalAttr(attrs, "height"))
        val pointX = toFloat(evalAttr(attrs, "pointX")) ?: 0f
        val pointY = toFloat(evalAttr(attrs, "pointY")) ?: 0f
        val estilos = parsearEstilos(attrs)
        val filas  = node.filas.map { fila ->
            fila.map { celdaExpr ->
                TextoFormulario(contenido = celdaExpr.accept(this)?.toString() ?: "") as ElementoFormulario
            }
        }
        val tabla = TablaFormulario(
            width   = width,
            height  = height,
            pointX  = pointX,
            pointY  = pointY,
            filas   = filas,
            estilos = estilos
        )
        elementos.add(tabla)
        return tabla
    }

    // ─── Helpers de evaluación ───────────────────────────────────────────────

    /** Evalúa un atributo por nombre; si es NodoExpresión lo interpreta, si no lo devuelve tal cual. */
    private fun evalAttr(attrs: List<NodoAtributo>, nombre: String): Any? {
        val valor = NodoAtributo.valor(attrs, nombre) ?: return null
        return if (valor is NodoExpresion) valor.accept(this) else valor
    }

    /** Evalúa la lista de opciones de un atributo tipo `options`. */
    @Suppress("UNCHECKED_CAST")
    private fun evalOpciones(attrs: List<NodoAtributo>, nombre: String): List<String> {
        val valor = NodoAtributo.valor(attrs, nombre) ?: return emptyList()
        return when (valor) {
            is List<*>       -> (valor as List<NodoExpresion>).map { it.accept(this)?.toString() ?: "" }
            is NodoExpresion -> listOf(valor.accept(this)?.toString() ?: "")
            else             -> emptyList()
        }
    }

    /** Extrae el bloque `styles: { ... }` y construye un [EstiloElemento]. */
    @Suppress("UNCHECKED_CAST")
    private fun parsearEstilos(attrs: List<NodoAtributo>): EstiloElemento {
        val stylesVal   = NodoAtributo.valor(attrs, "styles") ?: return EstiloElemento()
        val stylesAttrs = stylesVal as? List<NodoAtributo>    ?: return EstiloElemento()

        val color      = parsearColor(NodoAtributo.valor(stylesAttrs, "color"))
        val background = parsearColor(NodoAtributo.valor(stylesAttrs, "background"))
        val fontFamily = evalAttr(stylesAttrs, "fontFamily")?.toString()?.uppercase() ?: "SANS_SERIF"
        val textSize   = toFloat(evalAttr(stylesAttrs, "textSize")) ?: 14f

        val borderAttrs = NodoAtributo.valor(stylesAttrs, "border") as? List<NodoAtributo>
        val border = borderAttrs?.let {
            BorderEstilo(
                grosor = toFloat(evalAttr(it, "grosor")) ?: 1f,
                tipo   = evalAttr(it, "tipo")?.toString()?.uppercase() ?: "LINE",
                color  = parsearColor(NodoAtributo.valor(it, "color"))
            )
        }
        return EstiloElemento(
            color           = color,
            backgroundColor = background,
            fontFamily      = fontFamily,
            textSize        = textSize,
            border          = border
        )
    }

    /**
     * Convierte un valor de atributo a un [Color] de Compose.
     * Acepta hex con # (`#RRGGBB`, `#AARRGGBB`) y nombres de color en inglés.
     */
    private fun parsearColor(valor: Any?): Color {
        val str = when (valor) {
            is NodoExpresion -> valor.accept(this)?.toString()
            else             -> valor?.toString()
        } ?: return Color.Black

        return try {
            val hex = str.trim().trimStart('#')
            when (hex.length) {
                6    -> Color((0xFF000000L or hex.toLong(16)).toInt())
                8    -> Color(hex.toLong(16).toInt())
                else -> Color.Black
            }
        } catch (_: Exception) {
            when (str.trim().lowercase()) {
                "red"          -> Color.Red
                "blue"         -> Color.Blue
                "green"        -> Color.Green
                "white"        -> Color.White
                "black"        -> Color.Black
                "yellow"       -> Color.Yellow
                "cyan"         -> Color.Cyan
                "magenta"      -> Color.Magenta
                "gray", "grey" -> Color.Gray
                "transparent"  -> Color.Transparent
                else           -> Color.Black
            }
        }
    }

    // ─── Conversores de tipo ─────────────────────────────────────────────────

    private fun toDouble(v: Any?): Double? = when (v) {
        is Double -> v
        is Int    -> v.toDouble()
        is Float  -> v.toDouble()
        is Long   -> v.toDouble()
        is Number -> v.toDouble()
        is String -> v.toDoubleOrNull()
        else      -> null
    }

    private fun toFloat(v: Any?): Float? = toDouble(v)?.toFloat()

    private fun toInt(v: Any?): Int? = toDouble(v)?.toInt()

    private fun toBool(v: Any?): Boolean = when (v) {
        is Boolean -> v
        is Number  -> v.toDouble() != 0.0
        is String  -> v.isNotEmpty()
        else       -> false
    }

    companion object {
        /** Límite de iteraciones para detectar bucles infinitos. */
        private const val MAX_ITER = 1_000
    }
}