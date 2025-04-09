import net.pygmales.lexer.Lexer;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Lexer errors")
public class LexerErrorTests {
    private final ByteArrayOutputStream stream = new ByteArrayOutputStream();

    @BeforeEach
    public void setOutput() {
        System.setOut(new PrintStream(stream));
    }

    @Test
    @DisplayName("Unexpected token")
    public void testUnexpectedToken() {
        Lexer lexer = new Lexer("{}{}<<%<<{}{}``");
        lexer.scanTokens();
        assertEquals(4, stream.toString().split("error").length);
    }

    @Test
    @DisplayName("Unexpected token in multiline text")
    public void testMultilineText() {
        new Lexer("""
                def hello_world() {
                    if (a < b) {
                        return `ggk`;
                    } s[]
                }
                """).scanTokens();

        assertTrue(stream.toString().contains("error"));
    }

    @Test
    @DisplayName("Unclosed multiline comment")
    public void testUnclosedMultilineComment() {
        new Lexer("""
                def hello_world() {
                    if (a < b) { /* multiline comment begins here
                        return `ggk`;
                    } s[]
                }
                """).scanTokens();

        assertTrue(stream.toString().contains("error:"));
    }
}