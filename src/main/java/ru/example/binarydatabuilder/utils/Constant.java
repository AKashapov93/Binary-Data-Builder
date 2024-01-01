package ru.example.binarydatabuilder.utils;

public class Constant {

    public static final String RED_COLOR_COMMAND =
            """
                    3E
                    56
                    D3
                    00
                    """;
    public static final String GREEN_COLOR_COMMAND =
            """
                    3E
                    26
                    D3
                    00
                    """;

    public static final String BLUE_COLOR_COMMAND =
            """
                    3E
                    46
                    D3
                    00
                    """;

    public static final String COMMAND_TEMPLATE =
            """
                    3E
                    %s
                    D3
                    00
                            """;

    public static final String MNEMONIC_AND_OPERAND_RED =
            """
                    MVI A,56

                    OUT 00

                            """;

    public static final String MNEMONIC_AND_OPERAND_GREEN =
            """
                    MVI A,26

                    OUT 00

                            """;

    public static final String MNEMONIC_AND_OPERAND_BLUE =
            """
                    MVI A,46

                    OUT 00

                            """;

    public static final String MNEMONIC_AND_OPERAND_TEMPLATE =
            """
                    MVI A,%s

                    OUT 00

                            """;

    public static final String COMMENT_RED =
            """
                    56 => A, A=56
                    {красный цвет символа в Акк}
                    A->PORT 00,PORT 00=56
                    {цвет символа в порт монитора}
                                                     """;

    public static final String COMMENT_GREEN =
            """
                    26 => A, A=26
                    {зеленый цвет символа в Акк}
                    A->PORT 00,PORT 00=26
                    {цвет символа в порт монитора}
                                                     """;

    public static final String COMMENT_BLUE =
            """
                    46 => A, A=46
                    {синий цвет символа в Акк}
                    A->PORT 00,PORT 00=46
                    {цвет символа в порт монитора}
                                                     """;

    public static final String COMMENT_SPACE =
            """
                    20 => A, A= 20
                    {номер символа в Акк, пробел ->A}
                    A->PORT 00,PORT 00= 20
                    {номер символа в порт монитора

                                                      """;

    public static final String COMMENT_TEMPLATE =
            """
                    %s => A, A= %s
                    {номер символа в Акк, %s ->A}
                    A->PORT 00,PORT 00= %s
                    {номер символа в порт монитора}

                                                      """;

    private Constant() {
    }
}