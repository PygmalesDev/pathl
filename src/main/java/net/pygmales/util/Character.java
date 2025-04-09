package net.pygmales.util;

public class Character {
    public static boolean isLetter(char c) {
        return java.lang.Character.isLetter(c);
    }

    public static boolean isDigit(char c) {
        return java.lang.Character.isDigit(c);
    }

    public static boolean isWhitespace(char c) {
        return java.lang.Character.isWhitespace(c);
    }

    public static boolean isDot(char c) {
        return c == '.';
    }

    public static boolean isSlash(char c) {
        return c == '/';
    }

    public static boolean isQuotes(char c) {
        return c == '"';
    }

    public static boolean isUnderscore(char c) {
        return c == '_';
    }

    public static boolean isStar(char c) {
        return c == '*';
    }

    public static boolean isNewLine(char c) {
        return c == '\n';
    }

    public static boolean isEof(char c) {
        return c == '\0';
    }

    public static boolean isMultilineCommentEnd(char c, char next) {
        return isStar(c) && isSlash(next);
    }
}
