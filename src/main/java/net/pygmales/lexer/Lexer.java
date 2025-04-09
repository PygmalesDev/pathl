package net.pygmales.lexer;

import net.pygmales.util.ErrorLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.pygmales.lexer.TokenType.*;
import static net.pygmales.util.Character.*;

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
        switch (token.type()) {
            case UNKNOWN -> this.logger.unexpectedToken(token);
            case UMC_ERROR -> this.logger.unclosedComment(token);
            case US_ERROR -> this.logger.unclosedString(token);
        }
    }
    
    private void scanNextToken() {
        this.skipWhitespaces();

        if      (isLetter(this.getChar())) this.addKeywordToken();
        else if (isDigit(this.getChar()))  this.addNumericalToken();
        else if (isQuotes(this.getChar())) this.addStringToken();
        else if (isSlash(this.getChar()))  this.addSlashTokenOrSkipComment();
        else                                 this.addSymbolicToken();
    }

    private void addSlashTokenOrSkipComment() {
        int commentStart = this.column;

        proceed();
        if (isSlash(this.getChar())) this.getLexeme(() -> !isEof(this.getChar()) && !isNewLine(this.getChar()));
        else if (isStar(this.getChar())) {
            proceed();
            int commentStartLine = this.line;
            for (; !isMultilineCommentEnd(this.getChar(), this.peek()); proceed()) {
                if (isEof(this.getChar())) {
                    this.addToken(new Token(UMC_ERROR, "", null, commentStartLine, commentStart));
                    return;
                }
            }
            this.getLexeme(() -> !isEof(this.getChar()) && !isNewLine(this.getChar()));
        } else this.addToken(SLASH, "/");
    }

    private void addStringToken() {
        int stringStart = this.column;
        this.proceed();
        int stringStartLine = this.line;
        for (; !isQuotes(this.getChar()); proceed()) {
            if (isEof(this.getChar()) || isNewLine(this.getChar())) {
                this.addToken(new Token(US_ERROR, "", null, stringStartLine, stringStart));
                return;
            }
        }

        this.addToken(STRING, this.source.substring(stringStart, this.pos));
        this.proceed();
    }

    private void addSymbolicToken() {
        if (isEof(this.getChar())) return;

        int tokenStart = this.column;
        char letter = this.getChar();
        this.proceed();
        char nextChar = (isEof(this.getChar())) ? '\0' : this.getChar();
        boolean isDoubleToken = nextChar == '=';
        String lexeme = isDoubleToken ? String.format("%c%c", letter, nextChar) : String.valueOf(letter);
        if (isDoubleToken) this.proceed();
        this.addToken(new Token(this.tokenMap.getOrDefault(lexeme, UNKNOWN), lexeme, null, this.line, tokenStart));
    }

    private void addKeywordToken() {
        int keywordStart = this.column;
        String lexeme = this.getLexeme(() -> isLetter(this.getChar()) || isDigit(this.getChar()) || isUnderscore(this.getChar()));
        this.addToken(new Token(this.tokenMap.getOrDefault(lexeme, IDENTIFIER), lexeme, null, this.line, keywordStart));
    }

    private void addNumericalToken() {
        String lexeme = this.getLexeme(() -> isDigit(this.getChar()));
        if (isDot(this.getChar())) {
            if (isDigit(this.peek())) {
                proceed();
                lexeme += "." + this.getLexeme(() -> isDigit(this.getChar()));
            }
        }
        this.addToken(NUMBER, lexeme);
    }

    private void skipWhitespaces() {
        while (isWhitespace(this.getChar())) this.proceed();
    }

    private void addToken(Token token) {
        this.tokens.add(token);
    }

    private void addToken(TokenType type, String lexeme) {
        this.tokens.add(new Token(type, lexeme, null, this.line, this.column));
    }

    private String getLexeme(Supplier<Boolean> predicate) {
        int start = this.pos;
        while (predicate.get()) this.proceed();

        return this.source.substring(start, this.pos);
    }

    private void proceed() {
        if (isNewLine(this.getChar())) {
            this.line++;
            this.column = 0;
        }

        this.column++;
        if (++this.pos >= this.source.length()) this.currentChar = '\0';
        else this.currentChar = this.source.charAt(this.pos);
    }

    private char getChar() {
        return this.currentChar;
    }

    private char peek() {
        if (this.pos+1 < this.source.length()) return this.source.charAt(this.pos+1);
        return '\0';
    }
}