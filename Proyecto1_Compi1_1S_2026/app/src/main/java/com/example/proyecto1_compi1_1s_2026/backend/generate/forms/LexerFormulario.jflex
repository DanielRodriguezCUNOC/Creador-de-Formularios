package com.example.proyecto1_compi1_1s_2026.backend.generate.forms;

import java_cup.runtime.*;
import java.util.ArrayList;
import java.util.List;
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo;
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TipoError;

%%
%public
%class LexerFormulario
%unicode
%cup
%line
%column
%state STRING
%eofval{
    return symbol(sym.EOF, "<EOF>");
%eofval}

%{
   //---------------------------------------------
   //    Codigo para el manejo de errores
   //---------------------------------------------

    private List<ErrorInfo> errorList = new ArrayList<>();
    
    public List<ErrorInfo> getLexicalErrors(){
        return this.errorList;
    }

     //-----------------------------------------------
     //         Codigo para el parser
     //-----------------------------------------------
        private Symbol symbol(int type){
            return new Symbol(type, yyline+1, yycolumn+1);
        }

        private Symbol symbol(int type, Object value){
            return new Symbol(type, yyline+1, yycolumn+1, value);
        }

        
        private void addLexicalError(String mensaje) {
            ErrorInfo error = new ErrorInfo(
                TipoError.LEXICO,
                mensaje,
                yyline + 1,
                yycolumn + 1
            );
            errorList.add(error);
        }
%}

// --- Expresiones Regulares Básicas ---
LETRA = [:jletter:]
DIGITO = [0-9]
IDENTIFICADOR = {LETRA}({LETRA}|{DIGITO}|_)*
ENTERO = {DIGITO}+
DECIMAL = {DIGITO}+\.{DIGITO}+
NUMERO = {ENTERO}|{DECIMAL}

// Comentarios
COMENTARIO_MULTILINEA = \/\*([^*]|\*[^\/])*\*\/
COMENTARIO_LINEA = \$[^\n\r]*

// Colores hexadecimales (#RRGGBB o #RGB)
COLOR_HEX = #[0-9A-Fa-f]{6}|#[0-9A-Fa-f]{3}
COMP_COLOR = ([0-9]{1,3}|\?)
COLOR_RGB = \({COMP_COLOR}[ ]*,[ ]*{COMP_COLOR}[ ]*,[ ]*{COMP_COLOR}\)
COLOR_HSL = \<{COMP_COLOR}[ ]*,[ ]*{COMP_COLOR}[ ]*,[ ]*{COMP_COLOR}\>

// Espacios en blanco
ESPACIO = [ \t\r\n\f]+

// --- Estados léxicos ---
%state STRING

%%
<YYINITIAL> {

    [\u200B\u200C\u200D\uFEFF]        { /* Ignorar caracteres invisibles */ }

    /* Ignorar caracteres de control no útiles */

    [\p{C}&&[^\n\r\t]]      { /* Ignorar */ }

    // --- COMENTARIOS (ignorar) ---

    {COMENTARIO_MULTILINEA}         { /* Ignorar */ }
    {COMENTARIO_LINEA}              { /* Ignorar */ }

    // --- PALABRAS RESERVADAS ---

    "SECTION"           { return symbol(sym.SECTION); }
    "TABLE"             { return symbol(sym.TABLE); }
    "TEXT"              { return symbol(sym.TEXT); }
    "OPEN_QUESTION"     { return symbol(sym.OPEN_QUESTION); }
    "DROP_QUESTION"     { return symbol(sym.DROP_QUESTION); }
    "SELECT_QUESTION"   { return symbol(sym.SELECT_QUESTION); }
    "MULTIPLE_QUESTION" { return symbol(sym.MULTIPLE_QUESTION); }

    // --- ATRIBUTOS DE FORMULARIO ---

    "width"             { return symbol(sym.WIDTH); }
    "height"            { return symbol(sym.HEIGHT); }
    "pointX"            { return symbol(sym.POINT_X); }
    "pointY"            { return symbol(sym.POINT_Y); }
    "orientation"       { return symbol(sym.ORIENTATION); }
    "elements"          { return symbol(sym.ELEMENTS); }
    "styles"            { return symbol(sym.STYLES); }
    "\"color\""             { return symbol(sym.COLOR_ATTR); }
    "\"background color\""  { return symbol(sym.BACKGROUND_COLOR); }
    "\"font family\""       { return symbol(sym.FONT_FAMILY); }
    "\"text size\""         { return symbol(sym.TEXT_SIZE); }
    "\"border\""            { return symbol(sym.BORDER); }
    "content"           { return symbol(sym.CONTENT); }
    "label"             { return symbol(sym.LABEL); }
    "options"           { return symbol(sym.OPTIONS); }
    "correct"           { return symbol(sym.CORRECT); }

    // --- TIPOS DE DATOS Y VALORES RESERVADOS ---

    "number"            { return symbol(sym.TYPE_NUMBER); }
    "string"            { return symbol(sym.TYPE_STRING); }
    "special"           { return symbol(sym.TYPE_SPECIAL); }
    "VERTICAL"          { return symbol(sym.VERTICAL); }
    "HORIZONTAL"        { return symbol(sym.HORIZONTAL); }
    "MONO"              { return symbol(sym.MONO); }
    "SANS_SERIF"        { return symbol(sym.SANS_SERIF); }
    "CURSIVE"           { return symbol(sym.CURSIVE); }
    "LINE"              { return symbol(sym.LINE); }
    "DOTTED"            { return symbol(sym.DOTTED); }
    "DOUBLE"            { return symbol(sym.DOUBLE); }

    // --- PALABRA RESERVADA UTILIZADA PARA LA POKEAPI ---
    "NUMBER"            { return symbol(sym.NUMBER); }
    // --- COLORES POR DEFECTO ---

    "RED" | "BLUE" | "GREEN" | "PURPLE" | "SKY" | "YELLOW" | "BLACK" | "WHITE"
                        { return symbol(sym.COLOR_NAME, yytext()); }

    // --- CONTROL ---

    "IF"                { return symbol(sym.IF); }
    "ELSE"              { return symbol(sym.ELSE); }
    "WHILE"             { return symbol(sym.WHILE); }
    "DO"                { return symbol(sym.DO); }
    "FOR"               { return symbol(sym.FOR); }
    "in"                { return symbol(sym.IN); }

    // --- FUNCIONES ESPECIALES ---

    "who_is_that_pokemon" { return symbol(sym.FUNC_POKEMON); }
    "draw"              { return symbol(sym.DRAW); }

    // --- SÍMBOLOS ESTRUCTURALES ---

    "["                 { return symbol(sym.CORCHETE_IZQ); }
    "]"                 { return symbol(sym.CORCHETE_DER); }
    "{"                 { return symbol(sym.LLAVE_IZQ); }
    "}"                 { return symbol(sym.LLAVE_DER); }
    "("                 { return symbol(sym.PAREN_IZQ); }
    ")"                 { return symbol(sym.PAREN_DER); }
    ","                 { return symbol(sym.COMA); }
    ":"                 { return symbol(sym.DOS_PUNTOS); }
    ";"                 { return symbol(sym.PUNTO_COMA); }
    "="                 { return symbol(sym.ASIGNACION); }
    "?"                 { return symbol(sym.COMODIN); }
    "."                { return symbol(sym.PUNTO); }

    // --- OPERADORES ARITMÉTICOS ---

    "+"                 { return symbol(sym.MAS); }
    "-"                 { return symbol(sym.MENOS); }
    "*"                 { return symbol(sym.POR); }
    "/"                 { return symbol(sym.DIV); }
    "^"                 { return symbol(sym.POTENCIA); }
    "%"                 { return symbol(sym.MODULO); }

    // --- OPERADORES RELACIONALES ---

    ">="                { return symbol(sym.MAYOR_IGUAL); }
    "<="                { return symbol(sym.MENOR_IGUAL); }
    ">"                 { return symbol(sym.MAYOR); }
    "<"                 { return symbol(sym.MENOR); }
    "=="                { return symbol(sym.IGUALDAD); }
    "!!"                { return symbol(sym.DIFERENTE); }

    // --- OPERADORES LÓGICOS ---

    "||"                { return symbol(sym.OR); }
    "&&"                { return symbol(sym.AND); }
    "~"                 { return symbol(sym.NOT); }

    // --- FORMATOS DE COLOR ---

    //{COLOR_RGB}         { return symbol(sym.COLOR_RGB, yytext()); }
    //{COLOR_HSL}         { return symbol(sym.COLOR_HSL, yytext()); }
    {COLOR_HEX}         { return symbol(sym.COLOR_HEX, yytext()); }

    // --- LITERALES NUMÉRICOS E IDENTIFICADORES ---

    {NUMERO}            { return symbol(sym.NUMBER_LITERAL, yytext()); }
    {IDENTIFICADOR}     { return symbol(sym.IDENTIFICADOR, yytext()); }

    // --- INICIO DE CADENA DE TEXTO ---

    \"                  { yybegin(STRING);
                          return symbol(sym.INICIO_CADENA, yytext()); }

    // --- ESPACIOS EN BLANCO (ignorar) ---

    {ESPACIO}           { /* Ignorar */ }

    // --- MANEJO DE ERRORES LÉXICOS ---

    [^]  {
        String mensaje = "Carácter no reconocido '" + yytext() + "'";
        addLexicalError(mensaje);
    }
}

/*--------------------------------------------------------------------*/
/*   ESTADO DE CADENAS DE TEXTO — incluye reconocimiento de emojis    */
/*--------------------------------------------------------------------*/
<STRING> {

    // --- FIN DE LA CADENA ---

    \"                  { yybegin(YYINITIAL);
                          return symbol(sym.FIN_CADENA, yytext()); }

    // ----- EMOJIS DINÁMICOS -----
    // @[:)] / @[:smile:]  — uno o más ')' para la boca
    @\[:\)+\]           { return symbol(sym.EMOJI_SMILE,   yytext()); }

    // @[:(] / @[:sad:]   — uno o más '(' para la boca
    @\[:\(+\]           { return symbol(sym.EMOJI_SAD,     yytext()); }

    // @[:|] / @[:serious:] — uno o más '|' para la boca
    @\[:\|+\]           { return symbol(sym.EMOJI_SERIOUS, yytext()); }

    // @[<3] / @[:heart:]  — una o más repeticiones de '<' seguidas de uno o más '3'
    @\[<+3+\]           { return symbol(sym.EMOJI_HEART,   yytext()); }

    // --- SECUENCIAS DE ESCAPE DENTRO DE CADENA ---

    \\\"                { return symbol(sym.CONTENIDO_CADENA, yytext()); }
    \\\\                { return symbol(sym.CONTENIDO_CADENA, yytext()); }
    \\.                 { return symbol(sym.CONTENIDO_CADENA, yytext()); }

    // --- CONTENIDO NORMAL (cualquier carácter excepto " y \) ---

    [^\"\\\n]+          { return symbol(sym.CONTENIDO_CADENA, yytext()); }

    // --- CADENA SIN CERRAR (salto de línea inesperado) ---

    \n                  {
        yybegin(YYINITIAL);
        String mensaje = "Cadena de texto sin cerrar en línea " + (yyline + 1);
        addLexicalError(mensaje);
        //* Evita que el parser se bloquee
        return symbol(sym.FIN_CADENA, "");
    }
}