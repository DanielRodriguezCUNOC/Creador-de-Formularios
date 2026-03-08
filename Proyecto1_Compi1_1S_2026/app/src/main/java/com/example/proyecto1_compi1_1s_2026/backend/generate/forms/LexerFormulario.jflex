import java_cup.runtime.*;
import java.util.ArrayList;
import java.util.List;

%%
%public
%class LexerFormulario
%unicode
%cup
%line
%column

%{
   //---------------------------------------------
   //    Codigo para el manejo de errores
   //---------------------------------------------

    private List<String> errorList = new ArrayList<>();
    
    public List<String> getLexicalErrors(){
        return this.errorList;
    }

     //-----------------------------------------------
     //         Codigo para el parserFormulario
     //-----------------------------------------------
        private Symbol symbol(int type){
            return new Symbol(type, yyline+1, yycolumn+1);
        }

        private Symbol symbol(int type, Object value){
            return new Symbol(type, yyline+1, yycolumn+1, value);
        }

         private void error(String message){
         errorList.add("Error en la linea: " + (yyline+1) + ", columna: " + (yycolumn+1) + " : " + message);
    }
%}

// --- Expresiones Regulares Básicas ---
LETRA = [:jletter:]
DIGITO = [0-9]
IDENTIFICADOR = {LETRA}({LETRA}|{DIGITO}|_)*
ENTERO = {DIGITO}+
DECIMAL = {DIGITO}+\.{DIGITO}+
NUMERO = {ENTERO}|{DECIMAL}

COMENTARIO_MULTILINEA = \/\*([^*]|\*[^\/])*\*\/
COMENTARIO_LINEA = \$.*
                                     
// Textos y Cadenas
STRING_LITERAL = \"([^\\\"]|\\.)*\"

// Colores hexadecimales (#RRGGBB o #RGB)
COLOR_HEX = #[0-9A-Fa-f]{6}|#[0-9A-Fa-f]{3}

// Espacios en blanco
ESPACIO = [ \t\r\n\f]+

%%
<YYINITIAL> {

    // --- COMENTARIOS (ignorar) ---

        {COMENTARIO_MULTILINEA}     { /* Ignorar */ }
        {COMENTARIO_LINEA}          { /* Ignorar */ }

    // --- PALABRAS RESERVADAS ---

    "SECTION"		{return symbol(sym.SECTION);}
    "TABLE"			{return symbol(sym.TABLE);}
    "TEXT"			{return symbol(sym.TEXT);}
    "OPEN_QUESTION"		{return symbol(sym.OPEN_QUESTION);}
    "DROP_QUESTION"		{return symbol(sym.DROP_QUESTION);}
    "SELECT_QUESTION"	{return symbol(sym.SELECT_QUESTION);}
    "MULTIPLE_QUESTION"	{return symbol(sym.MULTIPLE_QUESTION);}
    
    // --- ATRIBUTOS DE FORMULARIO ---
    "width"             { return symbol(sym.WIDTH); }
      "height"            { return symbol(sym.HEIGHT); }
      "pointX"            { return symbol(sym.POINT_X); }
      "pointY"            { return symbol(sym.POINT_Y); }
      "orientation"       { return symbol(sym.ORIENTATION); }
      "elements"          { return symbol(sym.ELEMENTS); }
      "styles"            { return symbol(sym.STYLES); }
      "color"             { return symbol(sym.COLOR_ATTR); }
      "background color"  { return symbol(sym.BACKGROUND_COLOR); }
      "font family"       { return symbol(sym.FONT_FAMILY); }
      "text size"         { return symbol(sym.TEXT_SIZE); }
      "border"            { return symbol(sym.BORDER); }
      "content"           { return symbol(sym.CONTENT); }
      "label"             { return symbol(sym.LABEL); }
      "options"           { return symbol(sym.OPTIONS); }
      "correct"           { return symbol(sym.CORRECT); }

    // --- TIPOS DE DATOS Y VALORES RESERVADOS

    "number"            { return symbol(sym.TYPE_NUMBER); }
      "string"            { return symbol(sym.TYPE_STRING); }
      "special"           { return symbol(sym.TYPE_SPECIAL); }
      "VERTICAL"          { return symbol(sym.VERTICAL); }
      "HORIZONTAL"        { return symbol(sym.HORIZONTAL); }
      "MONO"		    { return symbol(sym.MONO); }	

    // --- COLORES POR DEFECTO ---
      "RED" | "BLUE" | "GREEN" | "PURPLE" | "SKY" | "YELLOW" | "BLACK" | "WHITE" { return symbol(sym.COLOR_NAME, yytext()); }


    // --- CONTROL ---
      "IF"                { return symbol(sym.IF); }
      "ELSE"              { return symbol(sym.ELSE); }
      "WHILE"             { return symbol(sym.WHILE); }
      "DO"                { return symbol(sym.DO); }
      "FOR"               { return symbol(sym.FOR); }
      "in"                { return symbol(sym.IN); }

// --- Funciones Especiales ---
  "who_is_that_pokemon" { return symbol(sym.FUNC_POKEMON); }
  "draw"              { return symbol(sym.DRAW); }


// --- Símbolos Estructurales ---
  "["                 { return symbol(sym.CORCHETE_IZQ); }
  "]"                 { return symbol(sym.CORCHETE_DER); }
  "{"                 { return symbol(sym.LLAVE_IZQ); }
  "}"                 { return symbol(sym.LLAVE_DER); }
  "("                 { return symbol(sym.PAREN_IZQ); }
  ")"                 { return symbol(sym.PAREN_DER); }
  ","                 { return symbol(sym.COMA); }
  ":"                 { return symbol(sym.DOS_PUNTOS); }
  "="                 { return symbol(sym.ASIGNACION); }
  "?"                 { return symbol(sym.COMODIN); }
  ".."                { return symbol(sym.RANGO); }

// --- Operadores Aritméticos ---
  "+"                 { return symbol(sym.MAS); }
  "-"                 { return symbol(sym.MENOS); }
  "*"                 { return symbol(sym.POR); }
  "/"                 { return symbol(sym.DIV); }
  "^"                 { return symbol(sym.POTENCIA); }
  "%"                 { return symbol(sym.MODULO); }

// --- Operadores Relacionales ---
  ">"                 { return symbol(sym.MAYOR); }
  ">="                { return symbol(sym.MAYOR_IGUAL); }
  "<"                 { return symbol(sym.MENOR); }
  "<="                { return symbol(sym.MENOR_IGUAL); }
  "=="                { return symbol(sym.IGUALDAD); }
  "!!"                { return symbol(sym.DIFERENTE); }

// --- Operadores Lógicos ---
  "||"                { return symbol(sym.OR); }
  "&&"                { return symbol(sym.AND); }
  "!"                 { return symbol(sym.NOT); }

// --- Formatos de Color ---
  {COLOR_HEX}         { return symbol(sym.COLOR_HEX, yytext()); }

// --- LITERALES ---
    {STRING_LITERAL}    { return symbol(sym.STRING_LITERAL, yytext()); }
    {NUMERO}            { return symbol(sym.NUMBER_LITERAL, yytext()); }
    {IDENTIFICADOR}     { return symbol(sym.ID, yytext()); }

// --- ESPACIOS EN BLANCO (ignorar) ---
    {ESPACIO}           { /* Ignorar espacios */ }


// --- Manejo de Errores Léxicos ---
[^]  { 
    String error = "Error Léxico: Caracter no reconocido '" + yytext() + 
                   "' en línea " + (yyline+1) + ", columna " + (yycolumn+1);
    errorList.add(error);
    System.err.println(error);
    }
}