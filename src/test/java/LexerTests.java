import net.pygmales.lexer.Lexer;
import net.pygmales.lexer.Token;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.pygmales.lexer.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Lexer tests")
public class LexerTests {

    @Test
    @DisplayName("Empty input")
    public void testEmptyInput() {
        Lexer lexer = new Lexer("");
        assertEquals(List.of(Token.EOF), lexer.scanTokens());
    }

    @Test
    @DisplayName("White spaces")
    public void testWhiteSpaces() {
        Lexer lexer = new Lexer("       \t\n         \n");
        assertEquals(List.of(Token.EOF), lexer.scanTokens());
    }

    @Test
    @DisplayName("Ignore inline comments")
    public void testIgnoreInlineComments() {
        Lexer lexer = new Lexer("let a = 3; // this is a comment");
        List<Token> tokens = lexer.scanTokens();
        assertEquals(List.of(LET, IDENTIFIER, EQUAL, NUMBER, SEMICOLON, EOF),
                tokens.stream().map(Token::type).toList());
    }

    @Test
    @DisplayName("Ignore multiline comments")
    public void testIgnoreMultilineComments() {
        Lexer lexer = new Lexer(
                """
                /* simple class to test if the lexer would
                skip the multiline comments */
                class Person() {
                    let a;
                }
                """);
        List<Token> tokens = lexer.scanTokens();
        assertEquals(List.of(CLASS, IDENTIFIER, LPAR, RPAR, LBRAC, LET, IDENTIFIER, SEMICOLON, RBRAC, EOF),
                tokens.stream().map(Token::type).toList());
    }

    @Test
    @DisplayName("Single letter identifier")
    public void testTokenizeSingleLetter() {
        Lexer lexer = new Lexer("a");
        assertEquals(List.of(new Token(IDENTIFIER, "a", null, 1, 1), Token.EOF), lexer.scanTokens());
    }

    @Test
    @DisplayName("Identifiers tokenizing")
    public void testTokenizeIdentifier() {
        Lexer lexer = new Lexer("oneInteger");
        assertEquals(List.of(new Token(IDENTIFIER, "oneInteger",  null, 1, 1), Token.EOF), lexer.scanTokens());
    }

    @Test
    @DisplayName("Identifier with digits tokenizing")
    public void testIdentifierWithDigits() {
        Lexer lexer = new Lexer("integer32");
        assertEquals(List.of(new Token(IDENTIFIER, "integer32", null, 1, 1), Token.EOF), lexer.scanTokens());
    }

    @Test
    @DisplayName("Numbers tokenizing")
    public void testTokenizeNumber() {
        Lexer lexer = new Lexer("365");
        assertEquals(List.of(new Token(NUMBER, "365", null, 1, 1), Token.EOF), lexer.scanTokens());
    }

    @Test
    @DisplayName("Keywords tokenizing")
    public void testTokenizeKeywords() {
        Lexer lexer = new Lexer("if else while true false and or this let def null return print super");
        List<Token> tokens = lexer.scanTokens();
        assertEquals(List.of(
                IF, ELSE, WHILE, TRUE, FALSE,
                AND, OR, THIS, LET, DEF, NULL,
                RETURN, PRINT, SUPER, EOF),
                tokens.stream().map(Token::type).toList());
    }

    @Test
    @DisplayName("Symbols tokenizing")
    public void testTokenizeSymbols() {
        Lexer lexer = new Lexer("! != < > <= >= { } ( ) ; * + - = == / . ,");
        List<Token> tokens = lexer.scanTokens();
        assertEquals(List.of(
                NOT, NOT_EQUAL, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL,
                LBRAC, RBRAC, LPAR, RPAR, SEMICOLON, STAR, PLUS, MINUS,
                EQUAL, EQUAL_EQUAL, SLASH, DOT, COMMA, EOF),
                tokens.stream().map(Token::type).toList());
    }

    @Test
    @DisplayName("Concatenated symbols")
    public void testConcatenatedSymbols() {
        Lexer lexer = new Lexer("(){}");
        assertEquals(List.of(LPAR, RPAR, LBRAC, RBRAC, EOF),
                lexer.scanTokens().stream().map(Token::type).toList());
    }

    @Test
    @DisplayName("Strings tokenizing")
    public void testTokenizeStrings() {
        Lexer lexer = new Lexer("\"string\" \"string with empty spaces\"");
        List<Token> tokens = lexer.scanTokens();
        assertEquals(List.of(
                new Token(STRING, "string", null, 1, 2),
                new Token(STRING, "string with empty spaces", null, 1, 9),
                Token.EOF), tokens);
    }

    @Test
    @DisplayName("Long expression")
    public void testTokenizeExpression() {
        Lexer lexer = new Lexer(
        """
        /* breaks something
           is very evil and smells bad */
        class Breaker {
            break() {}
        }
        
        // checks something and returns a string
        def hello_world() {
            let br = Breaker();
            let a = 35; // variable a
            let b = 84; // variable b
            let c = a + b; // variable c
            if (a >= 5) {
                br.break();
                return "everything okay";
            } else {
                // possibly something went wrong here
                return "something went wrong";
            }
        }
        """);
        List<Token> tokens = lexer.scanTokens();
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