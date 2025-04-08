package net.pygmales.interpreter;

import java.io.IOException;

public interface Interpreter {
    void runFile(String filePath) throws IOException;
    void runInteractive() throws IOException;
}
