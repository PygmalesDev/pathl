package net.pygmales.util;

import net.pygmales.lexer.Token;

public class ErrorLogger {
    private final String[] sourceLines;
    private final String filename;

    public ErrorLogger(String filename, String source) {
        this.filename = filename;
        this.sourceLines = source.split("\n");
    }

    public void unexpectedToken(Token token, int lineNum) {
        String line = this.sourceLines[lineNum];
        String lexeme = token.lexeme();
        int lexemePos = line.indexOf(lexeme);
        int trimmedLexemePos = line.trim().indexOf(lexeme);

        System.out.printf("%s %s\n", red("error:"), white(String.format("unexpected token `%s`!", lexeme)));
        System.out.printf("%s in file \"%s\" at %d:%d\n", red("┏━"), this.filename, lineNum, lexemePos);
        System.out.println(red("┃"));
        System.out.printf("%s\t%s\n", red("┣━━━▶"), line.trim());
        System.out.println(red(String.format("┃\t\t%s▲ no rules defined for the token `%s`\n", " ".repeat(trimmedLexemePos), lexeme)));
    }

    private static String white(Object o) {
        return String.format("\033[0;97m%s\033[0m", o.toString());
    }

    private static String red(Object o) {
        return String.format("\033[1;91m%s\033[0m", o.toString());
    }
}
