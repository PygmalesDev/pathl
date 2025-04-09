package net.pygmales.lexer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum TokenType {
    // Single-character tokens
    LPAR("("), RPAR(")"), LBRAC("{"), RBRAC("}"), COMMA(","), DOT("."),
    MINUS("-"), PLUS("+"), SEMICOLON(";"), SLASH("/"), STAR("*"),

    // One or two character tokens
    NOT("!"), NOT_EQUAL("!="), EQUAL("="), EQUAL_EQUAL("=="),
    GREATER(">"), GREATER_EQUAL(">="), LESS("<"), LESS_EQUAL("<="),

    // Literals
    IDENTIFIER("identifier"), STRING("string"), NUMBER("number"),

    // Keywords
    AND("and"), CLASS("class"), ELSE("else"), FALSE("false"), DEF("def"),
    FOR("for"), IF("if"), NULL("null"), OR("or"), PRINT("print"), RETURN("return"),
    SUPER("super"), THIS("this"), TRUE("true"), LET("let"), WHILE("while"),

    EOF("eof"), UNKNOWN("unknown"), UMC_ERROR("umc"), US_ERROR("us");

    private final String str;

    TokenType(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return this.str;
    }

    public static Map<String, TokenType> getTokenTypeMap() {
        Map<String, TokenType> tokenTypesMap = new HashMap<>();
        Arrays.stream(TokenType.values()).toList().forEach(token -> tokenTypesMap.put(token.str, token));
        return tokenTypesMap;
    }
}
