package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente

/**
 * Representa un atributo de un componente UI dentro del AST.
 *
 * En lugar de usar un Map<String, Object>, se usa una lista de NodoAtributo
 * para evitar el overhead del hashing y simplificar el recorrido del árbol.
 * Con un máximo de ~7 atributos por componente, la búsqueda lineal O(n)
 * es más eficiente que el O(1) amortizado del HashMap.
 *
 * @param nombre  Nombre del atributo (p.ej. "width", "height", "label", "styles")
 * @param valor   Valor del atributo: puede ser un NodoExpresion, una List, u otro valor
 */
data class NodoAtributo(
    val nombre: String,
    val valor: Any
) {
    /**
     * Busca el primer NodoAtributo con el nombre dado dentro de una lista.
     * Método de utilidad para evitar crear un Map temporal en el intérprete.
     */
    companion object {
        fun buscar(atributos: List<NodoAtributo>, nombre: String): NodoAtributo? {
            return atributos.firstOrNull { it.nombre == nombre }
        }

        fun valor(atributos: List<NodoAtributo>, nombre: String): Any? {
            return buscar(atributos, nombre)?.valor
        }

        fun contiene(atributos: List<NodoAtributo>, nombre: String): Boolean {
            return atributos.any { it.nombre == nombre }
        }
    }
}
