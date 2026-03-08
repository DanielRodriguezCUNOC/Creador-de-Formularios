package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

class TablaSimbolos(val anterior: TablaSimbolos?) {

    private val tabla = HashMap<String, Any>()

    fun almacenarVariable(id: String, valor: Any){
        tabla[id] = valor
    }

    fun obtenerVariable(id: String): Any{

        //* Si la variable existe en la tabla actual
        if(tabla.containsKey(id)){
            return tabla[id]!!
        }
        //* Si la variable no existe en la tabla actual, se busca en la anterior
        if(anterior != null){
            return anterior.obtenerVariable(id)
        }
        throw Exception("Error Semantico: Variable $id no declarada")
    }

    fun reasignarVariable(id: String, valor: Any) {
        if (tabla.containsKey(id)) {
            tabla[id] = valor
            return
        }
        if (anterior != null) {
            anterior.reasignarVariable(id, valor)
            return
        }
        throw Exception("Error Semantico: Variable $id no declarada")
    }

}