package net.pygmales.interpreter;

import net.pygmales.lexer.Lexer;
import net.pygmales.lexer.Token;
import net.pygmales.parser.Parser;
import net.pygmales.parser.SyntaxTreePrinter;
import net.pygmales.util.ErrorLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class BasicInterpreter implements Interpreter {
    private ErrorLogger logger;
    public BasicInterpreter() {}

    @Override
    public void runFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        logger = new ErrorLogger(path.toFile().getName());
        byte[] bytes = Files.readAllBytes(path);
        run(new String(bytes, Charset.defaultCharset()));
    }

    @Override
    public void runInteractive() throws IOException {
        logger = new ErrorLogger("interactive");
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true) {
            System.out.print("> ");
            String line = reader.readLine();
            if (Objects.isNull(line) || line.equals("exit")) break;
            run(line);
        }
        System.out.println("Leaving path interactive...");
    }

    private void run(String source) {
        logger.setSource(source);
        List<Token> tokens = new Lexer(logger, source).scanTokens();
        System.out.println(new SyntaxTreePrinter().print(new Parser(logger, tokens).parse()));
    }
}
