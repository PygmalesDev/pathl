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

    public void unclosedString(Token token) {
        this.printError(token,
                "unclosed string literal",
                "string literal started here and was not closed with `\"`"
        );
    }

    public void unclosedComment(Token token) {
        this.printError(token,
                "unclosed multiline comment",
                "multiline comment started here and was not closed with `*/`"
        );
    }

    public void unexpectedToken(Token token) {
        this.printError(token,
                String.format("unexpected token `%s`", token.literal()),
                String.format("no rules defined for the token `%s`", token.literal())
        );
    }

    private void printError(Token token, String errorName, String errorDescription) {
        String line = this.sourceLines[token.line()-1];
        int tokenPos = token.column() - (line.length() - line.stripLeading().length()) - 1;

        this.printOpener();
        this.printLine();
        System.out.printf("%s %s\n", red("error:"), white(errorName+"!"));
        this.printErrorPosition(line, token.line(), token.column());
        System.out.println(red(String.format("┃\t\t%s▲ %s",
                " ".repeat(tokenPos), errorDescription)));
        this.printLine();
        System.out.println();

        this.errorCount++;
    }

    private void printErrorPosition(String line, int lineNum, int column) {
        System.out.print(red("┣━ "));
        System.out.printf("in file \"%s\" at %d:%d\n", this.filename, lineNum, column);
        System.out.println(red("┃"));
        System.out.printf("%s\t%s\n", red("┣━━━▶"), line.trim());
    }

    private void printLine() {
        System.out.print(red("┃ "));
    }

    private void printOpener() {
        if (this.errorCount == 0) System.out.println(red("┓"));
    }

    public void close() {
        if (this.errorCount > 0) System.out.println(red("┛\n"));
    }

    private static String white(Object o) {
        return String.format("\033[0;97m%s\033[0m", o.toString());
    }

    private static String red(Object o) {
        return String.format("\033[1;91m%s\033[0m", o.toString());
    }

}
