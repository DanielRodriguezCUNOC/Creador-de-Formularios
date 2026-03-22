#!/bin/bash

# Script de generacion para LexerPKM y ParserPKM.

JFLEX_PATH="/home/luluwalilith/Documentos/1S 2026 CUNOC/COMPI 1/Recursos/jflex-full-1.9.1.jar"
CUP_PATH="/home/luluwalilith/Documentos/1S 2026 CUNOC/COMPI 1/Recursos/java-cup-11b.jar"

# Genera LexerPKM.java desde LexerPKM.jflex.
echo "Compilando lexer PKM..."
java -jar "$JFLEX_PATH" LexerPKM.jflex

# Genera ParserPKM.java y sym.java desde ParserPKM.cup.
echo "Compilando parser PKM..."
java -jar "$CUP_PATH" -parser ParserPKM ParserPKM.cup

echo "Generacion finalizada."
