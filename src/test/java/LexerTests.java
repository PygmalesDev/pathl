import net.pygmales.lexer.Lexer;
import net.pygmales.lexer.Token;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.pygmales.lexer.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;

public class LexerTests {
    public final Lexer lexer = new Lexer();

    @Test
    @DisplayName("Empty input")
    public void testEmptyInput() {
        this.lexer.setSource("");
        assertEquals(List.of(Token.EOF), this.lexer.scanTokens());
    }

    @Test
    @DisplayName("Identifiers tokenizing")
    public void testTokenizeIdentifier() {
        this.lexer.setSource("oneInteger");
        assertEquals(List.of(new Token(IDENTIFIER, "oneInteger", null), Token.EOF), this.lexer.scanTokens());
    }

    @Test
    @DisplayName("Identifier with digits tokenizing")
    public void testIdentifierWithDigits() {
        this.lexer.setSource("integer32");
        assertEquals(List.of(new Token(IDENTIFIER, "integer32", null), Token.EOF), this.lexer.scanTokens());
    }

    @Test
    @DisplayName("Numbers tokenizing")
    public void testTokenizeNumber() {
        this.lexer.setSource("365");
        assertEquals(List.of(new Token(NUMBER, "365", null), Token.EOF), this.lexer.scanTokens());
    }

    @Test
    @DisplayName("Keywords tokenizing")
    public void testTokenizeKeywords() {
        this.lexer.setSource("if else while true false and or this let def null return print super");
        List<Token> tokens = this.lexer.scanTokens();
        assertEquals(List.of(
                IF, ELSE, WHILE, TRUE, FALSE,
                AND, OR, THIS, LET, DEF, NULL,
                RETURN, PRINT, SUPER, EOF),
                tokens.stream().map(Token::type).toList());
    }

    @Test
    @DisplayName("Symbols tokenizing")
    public void testTokenizeSymbols() {
        this.lexer.setSource("! != < > <= >= { } ( ) ; * + - = == / . ,");
        List<Token> tokens = this.lexer.scanTokens();
        assertEquals(List.of(
                NOT, NOT_EQUAL, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL,
                LBRAC, RBRAC, LPAR, RPAR, SEMICOLON, STAR, PLUS, MINUS,
                EQUAL, EQUAL_EQUAL, SLASH, DOT, COMMA, EOF),
                tokens.stream().map(Token::type).toList());
    }

    @Test
    @DisplayName("Strings tokenizing")
    public void testTokenizeStrings() {
        this.lexer.setSource("\"string\" \"string with empty spaces\"");
        List<Token> tokens = this.lexer.scanTokens();
        assertEquals(List.of(
                new Token(STRING, "string", null),
                new Token(STRING, "string with empty spaces", null),
                Token.EOF), tokens);
    }

    @Test
    @DisplayName("Long expression")
    public void testTokenizeExpression() {
        this.lexer.setSource(
        """
        class Breaker {
            break() {}
        }
        
        def hello_world() {
            let br = Breaker();
            let a = 35;
            let b = 84;
            let c = a + b;
            if (a >= 5) {
                br.break();
                return "everything okay";
            } else {
                return "something went wrong";
            }
        }
        """);
        List<Token> tokens = this.lexer.scanTokens();
        assertEquals(List.of(
                CLASS, IDENTIFIER, LBRAC,
                IDENTIFIER, LPAR, RPAR, LBRAC, RBRAC, RBRAC,
                DEF, IDENTIFIER, LPAR, RPAR, LBRAC,
                LET, IDENTIFIER, EQUAL, IDENTIFIER, LPAR, RPAR, SEMICOLON,
                LET, IDENTIFIER, EQUAL, NUMBER, SEMICOLON,
                LET, IDENTIFIER, EQUAL, NUMBER, SEMICOLON,
                LET, IDENTIFIER, EQUAL, IDENTIFIER, PLUS, IDENTIFIER, SEMICOLON,
                IF, LPAR, IDENTIFIER, GREATER_EQUAL, NUMBER, RPAR, LBRAC,
                IDENTIFIER, DOT, IDENTIFIER, LPAR, RPAR, SEMICOLON,
                RETURN, STRING, SEMICOLON, RBRAC, ELSE, LBRAC,
                RETURN, STRING, SEMICOLON, RBRAC, RBRAC, EOF),
                tokens.stream().map(Token::type).toList());
    }
}
