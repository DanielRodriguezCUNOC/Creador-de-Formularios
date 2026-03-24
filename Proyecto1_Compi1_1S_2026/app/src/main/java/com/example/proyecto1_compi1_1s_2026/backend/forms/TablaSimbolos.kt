
package com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso
import android.util.Log

class TablaSimbolos(val anterior: TablaSimbolos?) {

    private val tabla = HashMap<String, Any>()
    private val tipos= HashMap<String, String>()



    fun almacenarVariable(id: String, valor: Any, tipo: String = "unknown"){
        Log.d("TablaSimbolos", "Declarando variable '$id' con tipo '$tipo' en entorno ${this.hashCode()}")
        tabla[id] = valor
        tipos[id] = tipo
    }

    fun obtenerVariable(id: String): Any{
        if(tabla.containsKey(id)){
            Log.d("TablaSimbolos", "Acceso variable '$id' en entorno ${this.hashCode()} (encontrada)")
            return tabla[id]!!
        }
        if(anterior != null){
            Log.d("TablaSimbolos", "Acceso variable '$id' en entorno ${this.hashCode()} (delegando a padre)")
            return anterior.obtenerVariable(id)
        }
        Log.d("TablaSimbolos", "Acceso variable '$id' en entorno ${this.hashCode()} (NO DECLARADA)")
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
    fun obtenerTipo(id: String): String? {
        if (tipos.containsKey(id)) {
            return tipos[id]
        }
        if (anterior != null) {
            return anterior.obtenerTipo(id)
        }
        return null
    }
}