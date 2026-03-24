package com.example.proyecto1_compi1_1s_2026.ui.util

data class BloqueFormulario(val nombre: String, val plantilla: String)

val bloquesDisponibles = listOf(
    BloqueFormulario(
        "SECTION",
        """
        SECTION [
            width: 500,
            height: 300,
            pointX: 0,
            pointY: 0,
            orientation: VERTICAL,
            elements: {
                // Agrega aquí tus elementos
            },
            styles [
                "color": #000000,
                "background color": #FFFFFF,
                "font family": MONO,
                "text size": 18,
                "border": (2, LINE, #000000)
            ]
        ]
        """.trimIndent()
    ),

    BloqueFormulario(
        "TABLE",
        """
            TABLE [
                width: 500,
                height: 300,
                pointX: 0,
                pointY: 0,
                orientation: HORIZONTAL,
                elements: {
                [ $ primera linea
                {
                  $ elemento 1
                }
                ],
                [ $ segunda linea
                {
                  $ elemento 2
                }
                ],
                },
                styles [
                    "color": #000000,
                    "background color": #FFFFFF,
                    "font family": MONO,
                    "text size": 12,
                    "border": (2, LINE, #000000)
                ]
            ]

        """.trimIndent()
    ),

    BloqueFormulario(
        "TEXT",
        """
        TEXT [
            content: "Texto de ejemplo",
            styles [
                "color": #000000,
                "text size": 16
            ]
        ]
        """.trimIndent()
    ),

    BloqueFormulario(
        "OPEN_QUESTION",
        """
        OPEN_QUESTION [
            label: "Pregunta abierta",
            styles [
                "color": #000000,
                "text size": 16
            ]
        ]
        """.trimIndent()
    ),

    BloqueFormulario(
        "DROP_QUESTION",
        """
            DROP_QUESTION [
                width: 500,
                height: 300,
                label: "Pregunta Desplegable",
                options: {"Opción 1", "Opción 2", "Opción 3"},
                correct: 1,
                $ estilos opcionales
                styles [
                    "color": #000000,
                    "background color": #FFFFFF,
                    "font family": MONO,
                    "text size": 16
                ]
            ]

        """.trimIndent()
    ),

    BloqueFormulario(
        "SELECT_QUESTION",
        """
            SELECT_QUESTION [
                width: 500,
                height: 300,
                label: "Pregunta De Seleccion Unica",
                options: {"Opción 1", "Opción 2", "Opción 3"},
                correct: 1,
                $ estilos opcionales
                styles [
                    "color": #000000,
                    "background color": #FFFFFF,
                    "font family": MONO,
                    "text size": 16
                ]
            ]

        """.trimIndent()
    ),

    BloqueFormulario(
        "MULTIPLE_QUESTION",
        """
            MULTIPLE_QUESTION [
                width: 500, $ opcional
                height: 300, $ opcional
                label: "Pregunta De Seleccion Unica",
                options: {"Opción 1", "Opción 2", "Opción 3"},
                correct: {1, 2},
                $ estilos opcionales
                styles [
                    "color": #000000,
                    "background color": #FFFFFF,
                    "font family": MONO,
                    "text size": 16
                ]
            ]

        """.trimIndent()
    ),

    BloqueFormulario(
        "CONDICIONAL IF",
        """
            IF ( 10 > 10 ) {
            /*
            Definición o asignación de variables,
            Preguntas
            U otros bloques de código
            */
            } ELSE IF ( 10 < 50 ) {
            $ Esta sección es opcional
            } ELSE {
            $ Esta sección es opcional
            }

        """.trimIndent()
    ),

    BloqueFormulario(
        "CICLO IF",
        """
            WHILE ( 10 > 10 ) {

                $ instrucciones
            }
        """.trimIndent()
    ),

    BloqueFormulario(
        "CICLO DO WHILE",
        """
            DO {
                $ instrucciones
            }WHILE ( 10 < 10 || 10 < 10)
            """.trimIndent()
    ),
    BloqueFormulario(
        "CICLO FOR",
        """
            FOR ( i = 0; i < 10; i++ ) {
                $ instrucciones
            }
        """.trimIndent()
    ),
    BloqueFormulario(
        "CICLO FOR EACH",
        """
            FOR EACH ( i in 1 .. 5 ) {
                $ instrucciones
            }
        """.trimIndent()
    ),
    BloqueFormulario(
        "EMOJI SONRISA",
        """
            @[:)]
        """.trimIndent()
    ),

    BloqueFormulario(
        "EMOJI TITE",
        """
            @[:(]
        """.trimIndent()
    ),

    BloqueFormulario(
        "EMOJI MODO SERIO",
        """
            @[:|]
        """.trimIndent()
    ),

    BloqueFormulario(
        "EMOJI KOKORO",
        """
            @[<3]
        """.trimIndent()
    ),

    BloqueFormulario(
        "EMOJI ESTRELLA",
        """
            @[:star:]
        """.trimIndent()
    ),

    BloqueFormulario(
        "EMOJI GATITO",
        """
            @[:^^:]
        """.trimIndent()
    ),
)
