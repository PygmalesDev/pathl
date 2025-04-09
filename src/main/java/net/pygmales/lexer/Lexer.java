package net.pygmales.lexer;

import net.pygmales.util.ErrorLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.pygmales.lexer.TokenType.*;

public class Lexer {
    private final List<Token> tokens = new ArrayList<>();
    private final Map<String, TokenType> tokenMap = TokenType.getTokenTypeMap();
    private final ErrorLogger logger;
    private final String source;

    private char currentChar;
    private int pos;
    private int column = 1;
    private int line = 1;

    public Lexer(String source) {
        this("nil.ptl", source);
    }

    public Lexer(String fileName, String source) {
        this.source = source;
        this.logger = new ErrorLogger(fileName, source);
        if (!source.isEmpty()) this.currentChar = source.charAt(0);
    }

    public List<Token> scanTokens() {
        while (this.pos < this.source.length()) {
            this.scanNextToken();
            this.isFaultyToken();
        }
        this.tokens.add(Token.EOF);
        this.logger.close();
        return this.tokens;
    }

    private void isFaultyToken() {
        if (this.tokens.isEmpty()) return;

        Token token = this.tokens.getLast();
        if (token.type().equals(UNKNOWN)) this.logger.unexpectedToken(token);
        if (token.type().equals(UMC)) this.logger.unclosedComment(token);
    }
    
    private void scanNextToken() {
        this.skipWhitespaces();

        if      (this.isLetter())    this.addKeywordToken();
        else if (this.isDigit())     this.addNumericalToken();
        else if (this.isQuotes())    this.addStringToken();
        else if (this.isSlash())     this.addSlashTokenOrSkipComment();
        else                         this.addSymbolicToken();
    }

    private void addSlashTokenOrSkipComment() {
        next();
        if (this.isSlash()) this.getLexeme(() -> !this.isEof() && !this.isNewLine());
        else if (this.isStar()) {
            next();
            String commentStartLine = String.valueOf(this.line);
            for (; !this.isMultilineCommentEnd(); next()) {
                if (this.isEof()) {
                    this.addToken(UMC, commentStartLine);
                    return;
                }
            }
            this.getLexeme(() -> !this.isEof() && !this.isNewLine());
        } else this.addToken(SLASH, "/");
    }

    private void addStringToken() {
        this.next();
        this.addToken(STRING, this.getLexeme(() -> !isQuotes()));
        this.next();
    }

    private void addSymbolicToken() {
        if (this.isEof()) return;

        char letter = this.currentChar;
        this.next();
        char nextChar = (this.isEof()) ? '\0' : this.currentChar;
        boolean isDoubleToken = nextChar == '=';
        String lexeme = isDoubleToken ? String.format("%c%c", letter, nextChar) : String.valueOf(letter);
        if (isDoubleToken) this.next();
        this.addToken(this.tokenMap.getOrDefault(lexeme, UNKNOWN), lexeme);
    }

    private void addKeywordToken() {
        String lexeme = this.getLexeme(() -> isLetter() || isDigit() || isUnderscore());
        this.addToken(this.tokenMap.getOrDefault(lexeme, IDENTIFIER), lexeme);
    }

    private void addNumericalToken() {
        this.addToken(NUMBER, this.getLexeme(this::isDigit));
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

    private boolean isSlash() {
        return this.currentChar == '/';
    }

    private boolean isQuotes() {
        return this.currentChar == '"';
    }

    private boolean isUnderscore() {
        return this.currentChar == '_';
    }

    private boolean isStar() {
        return this.currentChar == '*';
    }

    private boolean isNewLine() {
        return this.currentChar == '\n';
    }

    private boolean isEof() {
        return this.currentChar == '\0';
    }

    private boolean isMultilineCommentEnd() {
        return this.isStar() && this.source.charAt(this.pos+1) == '/';
    }

    private void skipWhitespaces() {
        while (this.isWhitespace()) this.next();
    }

    private void addToken(TokenType type, String lexeme) {
        this.tokens.add(new Token(type, lexeme, null, this.line, this.column));
    }

    private String getLexeme(Supplier<Boolean> predicate) {
        int start = this.pos;
        while (predicate.get()) this.next();

        return this.source.substring(start, this.pos);
    }

    private void next() {
        if (this.isNewLine()) {
            this.line++;
            this.column = 1;
        }

        this.column++;
        if (++this.pos >= this.source.length()) this.currentChar = '\0';
        else this.currentChar = this.source.charAt(this.pos);
    }
}