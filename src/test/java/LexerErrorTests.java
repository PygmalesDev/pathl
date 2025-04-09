import net.pygmales.lexer.Lexer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Lexer errors")
public class LexerErrorTests {
    @Test
    @DisplayName("Unexpected token")
    public void testUnexpectedToken() {
        Lexer lexer = new Lexer("{}{}<<%<<{}{}``");
        lexer.scanTokens();
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

    }
}