package net.pygmales.interpreter;

import net.pygmales.lexer.Lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class BasicInterpreter implements Interpreter {
    public BasicInterpreter() {}

    @Override
    public void runFile(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        run(new String(bytes, Charset.defaultCharset()));
    }

    @Override
    public void runInteractive() throws IOException {
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

    private void run(String content) {
        new Lexer(content).scanTokens().forEach(System.out::println);
    }
}
