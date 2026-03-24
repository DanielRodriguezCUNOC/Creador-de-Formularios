package com.example.proyecto1_compi1_1s_2026.backend.generate.forms;

import java.util.HashMap;
import java.util.Map;

// Diccionario centralizado de etiquetas para tokens del parser.
public final class TokenLabelProvider {

    private static final Map<String, String> LABELS = crearMapa();

    private TokenLabelProvider() {
    }

    public static String traducir(String token) {
        if (token == null) {
            return "desconocido";
        }
        String valor = LABELS.get(token);
        if (valor != null) {
            return valor;
        }
        return token;
    }

    private static Map<String, String> crearMapa() {
        Map<String, String> mapa = new HashMap<>();

        // Componentes UI
        mapa.put("SECTION", "SECTION");
        mapa.put("TABLE", "TABLE");
        mapa.put("TEXT", "TEXT");
        mapa.put("OPEN_QUESTION", "OPEN_QUESTION");
        mapa.put("DROP_QUESTION", "DROP_QUESTION");
        mapa.put("SELECT_QUESTION", "SELECT_QUESTION");
        mapa.put("MULTIPLE_QUESTION", "MULTIPLE_QUESTION");

        // Atributos
        mapa.put("WIDTH", "width");
        mapa.put("HEIGHT", "height");
        mapa.put("POINT_X", "pointX");
        mapa.put("POINT_Y", "pointY");
        mapa.put("ORIENTATION", "orientation");
        mapa.put("ELEMENTS", "elements");
        mapa.put("STYLES", "styles");
        mapa.put("COLOR_ATTR", "\"color\"");
        mapa.put("BACKGROUND_COLOR", "\"background color\"");
        mapa.put("FONT_FAMILY", "\"font family\"");
        mapa.put("TEXT_SIZE", "\"text size\"");
        mapa.put("BORDER", "\"border\"");
        mapa.put("CONTENT", "content");
        mapa.put("LABEL", "label");
        mapa.put("OPTIONS", "options");
        mapa.put("CORRECT", "correct");

        // Tipos
        mapa.put("TYPE_NUMBER", "number");
        mapa.put("TYPE_STRING", "string");
        mapa.put("TYPE_SPECIAL", "special");

        // Enumeraciones
        mapa.put("VERTICAL", "VERTICAL");
        mapa.put("HORIZONTAL", "HORIZONTAL");
        mapa.put("MONO", "MONO");
        mapa.put("SANS_SERIF", "SANS_SERIF");
        mapa.put("CURSIVE", "CURSIVE");
        mapa.put("LINE", "LINE");
        mapa.put("DOTTED", "DOTTED");
        mapa.put("DOUBLE", "DOUBLE");

        // API y control
        mapa.put("NUMBER", "NUMBER");
        mapa.put("IF", "IF");
        mapa.put("ELSE", "ELSE");
        mapa.put("WHILE", "WHILE");
        mapa.put("DO", "DO");
        mapa.put("FOR", "FOR");
        mapa.put("IN", "in");
        mapa.put("FUNC_POKEMON", "who_is_that_pokemon");
        mapa.put("DRAW", "draw");

        // Símbolos estructurales
        mapa.put("CORCHETE_IZQ", "'['");
        mapa.put("CORCHETE_DER", "']'");
        mapa.put("LLAVE_IZQ", "'{'");
        mapa.put("LLAVE_DER", "'}'");
        mapa.put("PAREN_IZQ", "'('");
        mapa.put("PAREN_DER", "')'");
        mapa.put("COMA", "','");
        mapa.put("DOS_PUNTOS", "':'");
        mapa.put("PUNTO_COMA", "';'");
        mapa.put("ASIGNACION", "'='");
        mapa.put("PUNTO", "'.'");
        mapa.put("COMODIN", "'?'");

        // Operadores
        mapa.put("MAS", "'+'");
        mapa.put("MENOS", "'-'");
        mapa.put("POR", "'*'");
        mapa.put("DIV", "'/'");
        mapa.put("POTENCIA", "'^'");
        mapa.put("MODULO", "'%'");
        mapa.put("MAYOR", "'>'");
        mapa.put("MENOR", "'<'");
        mapa.put("MAYOR_IGUAL", "'>='");
        mapa.put("MENOR_IGUAL", "'<='");
        mapa.put("IGUALDAD", "'=='");
        mapa.put("DIFERENTE", "'!!'");
        mapa.put("OR", "'||'");
        mapa.put("AND", "'&&'");
        mapa.put("NOT", "'~'");

        // Literales
        mapa.put("IDENTIFICADOR", "identificador");
        mapa.put("NUMBER_LITERAL", "número");
        mapa.put("INICIO_CADENA", "'\"' (inicio de cadena)");
        mapa.put("FIN_CADENA", "'\"' (fin de cadena)");
        mapa.put("CONTENIDO_CADENA", "contenido de cadena");
        mapa.put("EMOJI_SMILE", "emoji @[:)]");
        mapa.put("EMOJI_SAD", "emoji @[:(]");
        mapa.put("EMOJI_SERIOUS", "emoji @[:|]");
        mapa.put("EMOJI_HEART", "emoji @[<3]");

        // Colores y fin de entrada
        mapa.put("COLOR_NAME", "color (RED, BLUE, GREEN...)");
        mapa.put("COLOR_HEX", "color hexadecimal (#RRGGBB)");
        mapa.put("COLOR_RGB", "color RGB (r,g,b)");
        mapa.put("COLOR_HSL", "color HSL <h,s,l>");
        mapa.put("EOF", "fin de archivo");

        return mapa;
    }
}
