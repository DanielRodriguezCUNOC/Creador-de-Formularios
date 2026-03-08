package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso

class TablaSimbolos(val anterior: TablaSimbolos?) {

    private val tabla = HashMap<String, Any>()
    private val tipos= HashMap<String, String>()


    fun almacenarVariable(id: String, valor: Any, tipo: String = "unknown"){
        tabla[id] = valor
        tipos[id] = tipo
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

    fun obtenerTipo(id: String):String?{
        if (tipos.containsKey(id)){
            return tipos[id]
        }
        if (anterior != null){
            return anterior.obtenerTipo(id)
        }
        return null
    }
}