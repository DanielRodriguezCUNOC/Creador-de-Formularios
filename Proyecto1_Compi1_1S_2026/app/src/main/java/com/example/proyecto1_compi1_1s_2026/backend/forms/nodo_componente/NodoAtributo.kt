package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.nodo_componente


data class NodoAtributo(
    val nombre: String,
    val valor: Any
) {

     // Busca el primer NodoAtributo con el nombre dado dentro de una lista.
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
