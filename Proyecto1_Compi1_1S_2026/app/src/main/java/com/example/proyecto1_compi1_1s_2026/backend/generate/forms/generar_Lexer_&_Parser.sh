#!/bin/bash

# This script generate a java class using jflex

JFLEX_PATH="/home/luluwalilith/Documentos/1S 2026 CUNOC/COMPI 1/Recursos/jflex-full-1.9.1.jar"

CUP_PATH="/home/luluwalilith/Documentos/1S 2026 CUNOC/COMPI 1/Recursos/java-cup-11b.jar"


# Compile the jflex specification file
echo "Compilando el archivo flex"
java -jar "$JFLEX_PATH" LexerFormulario.jflex
java -jar "$CUP_PATH" -parser Parser ParserFormulario.cup