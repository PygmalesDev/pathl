package net.pygmales.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.pygmales.lexer.TokenType.*;

public class Lexer {
    private final List<Token> tokens = new ArrayList<>();
    private final Map<String, TokenType> tokenMap = TokenType.getTokenTypeMap();

    private String source;
    private char currentChar;
    private int sourceSize;
    private int pos;

    public Lexer() {}

    public Lexer(String source) {
        this.source = source;
        this.sourceSize = source.length()-1;
        if (!source.isEmpty()) this.currentChar = source.charAt(0);
    }

    public List<Token> scanTokens() {
        while (this.pos < this.sourceSize)
            this.scanNextToken();

        this.tokens.add(Token.EOF);
        return this.tokens;
    }
    
    private void scanNextToken() {
        this.skipWhitespaces();

        if      (this.isLetter()) this.addKeywordToken();
        else if (this.isDigit())  this.addNumericalToken();
        else if (this.isQuotes()) this.addStringToken();
        else                      this.addSymbolicToken();
    }

    private void addStringToken() {
        this.next();
        this.tokens.add(new Token(STRING, this.getLexeme(() -> !isQuotes()), null));
        this.next();
    }

    private void addSymbolicToken() {
        char letter = this.currentChar;
        this.next();
        char next = (letter != '\n') ? this.currentChar : ' ';
        boolean isDoubleToken = next == '=';
        String lexeme = isDoubleToken ? String.format("%c%c", letter, next) : String.valueOf(letter);
        if (isDoubleToken) this.next();
        this.tokens.add(new Token(this.tokenMap.getOrDefault(lexeme, EOF), lexeme, null));
    }

    private void addKeywordToken() {
        String lexeme = this.getLexeme(() -> isLetter() || isDigit() || isUnderscore());
        this.tokens.add(new Token(this.tokenMap.getOrDefault(lexeme, IDENTIFIER), lexeme, null));
    }

    private void addNumericalToken() {
        this.tokens.add(new Token(NUMBER, this.getLexeme(this::isDigit), null));
    }

    private boolean isLetter() {
        return Character.isLetter(this.currentChar);
    }

    private boolean isDigit() {
        return Character.isDigit(this.currentChar);
    }

    private boolean isWhitespace() {
        return Character.isWhitespace(this.currentChar);
    }

    private boolean isQuotes() {
        return this.currentChar == '"';
    }

    private boolean isUnderscore() {
        return this.currentChar == '_';
    }

    private void skipWhitespaces() {
        while (this.isWhitespace()) this.next();
    }

    private String getLexeme(Supplier<Boolean> predicate) {
        int start = this.pos;
        while (predicate.get()) this.next();

        return this.source.substring(start, this.pos);
    }

    public void setSource(String source) {
        this.source = source;
        this.sourceSize = source.length()-1;
        if (!source.isEmpty()) this.currentChar = source.charAt(0);
        this.pos = 0;
    }

    private void next() {
        if (++this.pos == this.source.length()) this.currentChar = '\0';
        else this.currentChar = this.source.charAt(this.pos);
    }
}