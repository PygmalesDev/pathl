package net.pygmales.error;

import net.pygmales.lexer.Token;

public class ErrorLogger {
    public static void omitError(Token token, String line) {
        System.out.printf("Error in %s: unexpected token %s%n", line, token.type());
    }
}
