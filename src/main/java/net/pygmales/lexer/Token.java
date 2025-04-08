package net.pygmales.lexer;

public record Token(
        TokenType type,
        String lexeme,
        Object literal
) {
    public static final Token EOF = new Token(TokenType.EOF, "", null);

    @Override
    public String toString() {
        return String.format("Token(%s, %s, %s)", type, lexeme, literal);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token other)
            return this.type.equals(other.type) && this.lexeme.equals(other.lexeme);
        return false;
    }
}
