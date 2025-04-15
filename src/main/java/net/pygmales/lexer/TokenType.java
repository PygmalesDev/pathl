package net.pygmales.lexer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    public static final List<TokenType> BINARY_OPERATOR_TYPE = List.of(
            EQUAL_EQUAL, NOT_EQUAL, LESS, LESS_EQUAL,
            GREATER, GREATER_EQUAL, PLUS, MINUS, STAR, SLASH);
    public static final List<TokenType> UNARY_OPERATOR_TYPE = List.of(MINUS, NOT);
    public static final List<TokenType> EQUALITY_OPERATOR_TYPE = List.of(EQUAL_EQUAL, NOT_EQUAL);
    public static final List<TokenType> COMPARISON_OPERATOR_TYPE = List.of(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);
    public static final List<TokenType> TERM_OPERATOR_TYPE = List.of(PLUS, MINUS);
    public static final List<TokenType> FACTOR_OPERATOR_TYPE = List.of(SLASH, STAR);
    public static final List<TokenType> PRIMARY_OPERATOR_TYPE = List.of(FALSE, TRUE, NULL, NUMBER, STRING);

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
