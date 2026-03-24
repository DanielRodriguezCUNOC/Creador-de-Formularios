package com.example.proyecto1_compi1_1s_2026.backend.generate.pkm;

import java_cup.runtime.*;
import java.util.ArrayList;
import java.util.List;
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.ErrorInfo;
import com.example.proyecto1_compi1_1s_2026.backend.logic.forms.proceso.TipoError;

%%
%public
%class LexerPKM
%unicode
%cup
%line
%column
%ignorecase
%state STRING
%state METADATA
%eofval{
	return symbol(sym.EOF, "<EOF>");
%eofval}

%{
	// Lista de errores lexicos detectados.
	private List<ErrorInfo> errorList = new ArrayList<>();

	public List<ErrorInfo> getLexicalErrors() {
		return this.errorList;
	}

	// Crea simbolo sin valor asociado.
	private Symbol symbol(int type) {
		return new Symbol(type, yyline, yycolumn);
	}

	// Crea simbolo con lexema/valor.
	private Symbol symbol(int type, Object value) {
		return new Symbol(type, yyline, yycolumn, value);
	}

	// Registra error de caracter invalido.
	private void addLexicalError(String mensaje) {
		ErrorInfo error = new ErrorInfo(
			TipoError.LEXICO,
			mensaje,
			yyline,
			yycolumn
		);
		errorList.add(error);
	}
%}

// Expresiones base.
LETRA = [:jletter:]
DIGITO = [0-9]
// Incluye marcas combinadas para soportar tildes en forma Unicode descompuesta.
MARCA_COMBINADA = [\u0300-\u036F]
IDENT = {LETRA}({LETRA}|{DIGITO}|{MARCA_COMBINADA}|_|-)*
ENTERO = {DIGITO}+
NUMERO = -?{ENTERO}(\.{ENTERO})?

ESPACIO = [ \t\r\n\f]+

// Formatos de color admitidos.
COLOR_HEX_RE = #[0-9A-Fa-f]{3}([0-9A-Fa-f]{3})?
COMP_COLOR = ({NUMERO}|\?)
COLOR_RGB_RE = \({COMP_COLOR}[ ]*,[ ]*{COMP_COLOR}[ ]*,[ ]*{COMP_COLOR}\)
COLOR_HSL_RE = \<{COMP_COLOR}[ ]*,[ ]*{COMP_COLOR}[ ]*,[ ]*{COMP_COLOR}\>

// Claves compuestas con espacios.
K_TOTAL_SECCIONES = "total"[ ]+"de"[ ]+"secciones"
K_TOTAL_PREGUNTAS = "total"[ ]+"de"[ ]+"preguntas"
K_BACKGROUND_COLOR = "background"[ ]+"color"
K_FONT_FAMILY = "font"[ ]+"family"
K_TEXT_SIZE = "text"[ ]+"size"

%%


<YYINITIAL> {
	
	[\u200B\u200C\u200D\uFEFF] { /* Ignora invisibles */ }
	
	{ESPACIO} { /* Ignora espacios */ }

	// Si detecta inicio de metadatos, cambia a estado METADATA
	"###" { yybegin(METADATA); return symbol(sym.METADATA_DELIM); }
	
	"Author" { return symbol(sym.AUTHOR); }
	"Fecha" { return symbol(sym.FECHA); }
	"Hora" { return symbol(sym.HORA); }
	"Description" { return symbol(sym.DESCRIPTION); }
	{K_TOTAL_SECCIONES} { return symbol(sym.TOTAL_SECCIONES); }
	{K_TOTAL_PREGUNTAS} { return symbol(sym.TOTAL_PREGUNTAS); }
	"Abiertas" { return symbol(sym.ABIERTAS); }
	"Desplegables" { return symbol(sym.DESPLEGABLES); }
	"Selección" { return symbol(sym.SELECCION); }
	"Múltiples" { return symbol(sym.MULTIPLES); }
	"Total de Componentes" { return symbol(sym.TOTAL_COMPONENTES); }
	"Textos"               { return symbol(sym.TEXTOS); }
	"Tablas"               { return symbol(sym.TABLAS); }
	"Con estilos"          { return symbol(sym.CON_ESTILOS); }
	"Draws ejecutados"     { return symbol(sym.DRAWS_EJECUTADOS); }
	"section" { return symbol(sym.SECTION); }
	"style" { return symbol(sym.STYLE); }
	"content" { return symbol(sym.CONTENT); }
	"table" { return symbol(sym.TABLE); }
	"line" { return symbol(sym.LINE); }
	"element" { return symbol(sym.ELEMENT); }
	"open" { return symbol(sym.OPEN); }
	"drop" { return symbol(sym.DROP); }
	"select" { return symbol(sym.SELECT); }
	"multiple" { return symbol(sym.MULTIPLE); }
	"color" { return symbol(sym.COLOR_KEY); }
	{K_BACKGROUND_COLOR} { return symbol(sym.BACKGROUND_COLOR_KEY); }
	{K_FONT_FAMILY} { return symbol(sym.FONT_FAMILY_KEY); }
	{K_TEXT_SIZE} { return symbol(sym.TEXT_SIZE_KEY); }
	"border" { return symbol(sym.BORDER_KEY); }
	"VERTICAL" { return symbol(sym.VERTICAL); }
	"HORIZONTAL" { return symbol(sym.HORIZONTAL); }
	"MONO" { return symbol(sym.MONO); }
	"SANS_SERIF" { return symbol(sym.SANS_SERIF); }
	"CURSIVE" { return symbol(sym.CURSIVE); }
	"LINE" | "DOTTED" | "DOUBLE" { return symbol(sym.BORDER_STYLE, yytext()); }
	"true" | "false" { return symbol(sym.BOOLEAN, yytext()); }
	{COLOR_HEX_RE} { return symbol(sym.COLOR_HEX, yytext()); }
	{COLOR_RGB_RE} { return symbol(sym.COLOR_RGB, yytext()); }
	{COLOR_HSL_RE} { return symbol(sym.COLOR_HSL, yytext()); }
	"</" { return symbol(sym.LT_SLASH); }
	"/>" { return symbol(sym.SLASH_GT); }
	"/" { return symbol(sym.SLASH); }
	"<" { return symbol(sym.LT); }
	">" { return symbol(sym.GT); }
	"=" { return symbol(sym.EQUALS); }
	"," { return symbol(sym.COMMA); }
	":" { return symbol(sym.COLON); }
	"{" { return symbol(sym.LBRACE); }
	"}" { return symbol(sym.RBRACE); }
	{NUMERO} { return symbol(sym.NUMBER, yytext()); }
	{IDENT} { return symbol(sym.IDENTIFIER, yytext()); }
	\" { yybegin(STRING); return symbol(sym.STRING_START, yytext()); }
	[^] {
		addLexicalError("Caracter no reconocido '" + yytext() + "'");
	}
}

<METADATA> {
	"###" { yybegin(YYINITIAL); return symbol(sym.METADATA_DELIM); }
	[^]+  { /* Ignorar todo el contenido de metadatos */ }
}

<STRING> {
	// Fin de cadena.
	\" { yybegin(YYINITIAL); return symbol(sym.STRING_END, yytext()); }

	// Detecta emojis unicode como pares sustitutos UTF-16.
	[\uD83C-\uDBFF][\uDC00-\uDFFF] {
		addLexicalError("Emoji unicode no permitido; use notacion @[...]");
	}

	// Emojis validos (formas simbolicas y nombradas).
	@\[:\)+\] { return symbol(sym.EMOJI_SPEC, yytext()); }
	@\[:smile:\] { return symbol(sym.EMOJI_SPEC, yytext()); }
	@\[:\(+\] { return symbol(sym.EMOJI_SPEC, yytext()); }
	@\[:sad:\] { return symbol(sym.EMOJI_SPEC, yytext()); }
	@\[:\|+\] { return symbol(sym.EMOJI_SPEC, yytext()); }
	@\[:serious:\] { return symbol(sym.EMOJI_SPEC, yytext()); }
	@\[:<+3+\] { return symbol(sym.EMOJI_SPEC, yytext()); }
	@\[:heart:\] { return symbol(sym.EMOJI_SPEC, yytext()); }
	@\[:cat:\] { return symbol(sym.EMOJI_SPEC, yytext()); }
	@\[:\^\^:\] { return symbol(sym.EMOJI_SPEC, yytext()); }
	@\[:star:\] { return symbol(sym.EMOJI_SPEC, yytext()); }
	@\[:star[:\-][0-9]+:\] { return symbol(sym.EMOJI_SPEC, yytext()); }

	// Cualquier especificacion @[...] no reconocida se reporta como error.
	@\[[^\]\n]+\] {
		addLexicalError("Formato de emoji no valido: '" + yytext() + "'");
	}

	// Escapes basicos dentro de texto.
	\\\" { return symbol(sym.STRING_TEXT, yytext()); }
	\\\\ { return symbol(sym.STRING_TEXT, yytext()); }
	\\. { return symbol(sym.STRING_TEXT, yytext()); }

	[^\"\\\n]+ { return symbol(sym.STRING_TEXT, yytext()); }

	\n {
		yybegin(YYINITIAL);
		addLexicalError("Cadena sin cerrar en linea " + (yyline));
		return symbol(sym.STRING_END, "");
	}
}
