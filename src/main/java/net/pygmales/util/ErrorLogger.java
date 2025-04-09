package net.pygmales.util;

import net.pygmales.lexer.Token;

public class ErrorLogger {
    private final String[] sourceLines;
    private final String filename;
    private int errorCount;

    public ErrorLogger(String filename, String source) {
        this.filename = filename;
        this.sourceLines = source.split("\n");
    }

    public void unclosedComment(Token token) {
        int lineNum = Integer.parseInt(token.lexeme());
        String line = this.sourceLines[lineNum-1];
        int lexemePos = line.indexOf("/*");
        int trimmedLexemePos = line.trim().indexOf("/*");

        if (this.errorCount == 0) System.out.println(red("┓"));
        System.out.print(red("┃ "));
        System.out.printf("%s %s\n", red("error:"), white("unclosed multiline comment!"));
        System.out.print(red("┣━ "));
        System.out.printf("in file \"%s\" at %d:%d\n", this.filename, lineNum, lexemePos);
        System.out.println(red("┃"));

        System.out.printf("%s\t%s\n", red("┣━━━▶"), line.trim());
        System.out.println(red(String.format("┃\t\t%s▲ multiline comment started here and was not closed with `*/`",
                " ".repeat(trimmedLexemePos))));
        System.out.println(red("┃"));
        this.errorCount++;
    }

    public void unexpectedToken(Token token) {
        int lineNum = token.line();
        int lexemePos = token.column();
        String line = this.sourceLines[lineNum-1];
        String lexeme = token.lexeme();

        if (this.errorCount == 0) System.out.println(red("┓"));
        System.out.print(red("┃ "));
        System.out.printf("%s %s\n", red("error:"),
                white(String.format("unexpected token `%s`!", lexeme)));
        System.out.print(red("┣━ "));
        System.out.printf("in file \"%s\" at %d:%d\n", this.filename, lineNum, lexemePos);
        System.out.println(red("┃"));
        System.out.printf("%s\t%s\n", red("┣━━━▶"), line.trim());
        System.out.println(red(String.format("┃\t\t%s▲ no rules defined for the token `%s`", " ".repeat(lexemePos-2), lexeme)));
        System.out.println(red("┃"));
        this.errorCount++;
    }

    public void close() {
        if (this.errorCount > 0)
            System.out.println(red("┛\n"));
    }

    private static String white(Object o) {
        return String.format("\033[0;97m%s\033[0m", o.toString());
    }

    private static String red(Object o) {
        return String.format("\033[1;91m%s\033[0m", o.toString());
    }
}
