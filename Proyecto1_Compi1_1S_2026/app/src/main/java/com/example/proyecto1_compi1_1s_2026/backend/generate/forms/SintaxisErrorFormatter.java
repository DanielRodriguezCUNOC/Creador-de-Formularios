package com.example.proyecto1_compi1_1s_2026.backend.generate.forms;

import java.util.List;

// Formateador centralizado para mensajes de error sintáctico.
public final class SintaxisErrorFormatter {

    private SintaxisErrorFormatter() {
    }

    public static String construirMensaje(List<String> esperados, String encontrado) {
        // Si no hay tokens esperados, mostrar mensaje genérico.
        if (esperados == null || esperados.isEmpty()) {
            return "Token inesperado '" + encontrado + "'";
        }

        // Detectar caso especial de atributos styles entre comillas.
        boolean todosStyles = true;
        for (String esperado : esperados) {
            if (esperado == null || !(esperado.startsWith("\"") && esperado.endsWith("\""))) {
                todosStyles = false;
                break;
            }
        }

        if (todosStyles) {
            return "Se esperaba un atributo de styles entre comillas: "
                + String.join(", ", esperados)
                + ". Recuerda que los atributos dentro de styles [ ] deben escribirse entre comillas."
                + " Pero se encontró: '" + encontrado + "'";
        }

        return "Se esperaba: " + String.join(", ", esperados)
            + ". Pero se encontró: '" + encontrado + "'";
    }
}
