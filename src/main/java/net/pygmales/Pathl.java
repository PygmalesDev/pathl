package net.pygmales;

import net.pygmales.interpreter.BasicInterpreter;

import java.io.IOException;

public class Pathl {
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: pathl [script]");
            System.exit(1);
        }

        var interpreter = new BasicInterpreter();

        if (args.length == 1) interpreter.runFile(args[0]);
        else interpreter.runInteractive();
    }
}