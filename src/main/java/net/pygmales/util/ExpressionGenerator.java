package net.pygmales.util;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExpressionGenerator {
    private static final Path EXP_PATH = Path.of("./src/main/java/net/pygmales/parser/expression/Expression.java");
    private static final Path VISITOR_PATH = Path.of("./src/main/java/net/pygmales/parser/expression/ExpressionVisitor.java");

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL schemeUrl = ExpressionGenerator.class.getResource("/scheme.exp");
        if (Objects.isNull(schemeUrl)) System.exit(42);

        Files.createDirectories(EXP_PATH.getParent());
        Files.writeString(EXP_PATH, "");
        Files.writeString(VISITOR_PATH, "");

        BufferedReader reader = Files.newBufferedReader(Path.of(schemeUrl.toURI()));
        FileWriter exp_writer = new FileWriter(EXP_PATH.toFile());
        FileWriter visitor_writer = new FileWriter(VISITOR_PATH.toFile());

        addExpressionHeaders(exp_writer);
        addVisitorHeaders(visitor_writer);

        addExpressionInterfaceFields(exp_writer);
        visitor_writer.write("public interface ExpressionVisitor<R> {\n");

        for (String line : reader.lines().toList()) {
            SchemeParser parser = new SchemeParser(line);
            addVisitMethod(visitor_writer, parser);
            addExpressionClass(exp_writer, parser);
            addExpressionFactory(exp_writer, parser);
        }
        reader.close();

        exp_writer.write("}\n");
        visitor_writer.write("}\n");

        visitor_writer.close();
        exp_writer.close();
    }

    private static void addExpressionInterfaceFields(FileWriter writer) throws IOException {
        writer.write("public interface Expression {\n");
        writer.write("\t<R> R accept(ExpressionVisitor<R> visitor);\n\n");
    }

    private static void addExpressionHeaders(Writer writer) throws IOException {
        writer.write("package net.pygmales.parser.expression;\n\n");
        writer.write("import net.pygmales.lexer.Token;\n");
        writer.write("import static net.pygmales.lexer.TokenType.*;\n\n");
    }

    private static void addVisitorHeaders(Writer writer) throws IOException {
        writer.write("package net.pygmales.parser.expression;\n\n");
        writer.write("import static net.pygmales.parser.expression.Expression.*;\n\n");
    }

    private static void addVisitMethod(Writer writer, SchemeParser parser) throws IOException {
        writer.write(String.format("\tR visit%s(%sExpression %s);\n",
                parser.className, parser.className, parser.className.toLowerCase()));
    }

    private static void addExpressionClass(Writer writer, SchemeParser parser) throws IOException {
        // Class declaration
        writer.write(String.format("\tclass %sExpression implements Expression {\n", parser.className));

        // Class fields
        for (String type : parser.fieldTypes) writer.write(String.format("\t\tprivate final %s;\n", type));
        writer.write("\n");

        // Class constructor
        writer.write(String.format("\t\tpublic %sExpression(%s) {\n", parser.className, String.join(", ", parser.fieldTypes)));
        for (String fieldName : parser.fieldNames) writer.write(String.format("\t\t\tthis.%s = %s;\n", fieldName, fieldName));
        writer.write("\t\t}\n\n");

        // accept() implementation
        writer.write("\t\t@Override\n\t\tpublic <R> R accept(ExpressionVisitor<R> visitor) {\n");
        writer.write(String.format("\t\t\treturn visitor.visit%s(this);\n", parser.className));
        writer.write("\t\t}\n\n");

        // toString() method
        writer.write("\t\t@Override\n\t\tpublic String toString() {\n");
        writer.write(String.format("\t\t\treturn %s;\n\t\t}\n", Arrays.stream(parser.fieldNames)
                .map(name -> String.format("%s.toString()", name))
                .collect(Collectors.joining(" + \" \" + "))));
        writer.write("\t}\n\n");
    }

    private static void addExpressionFactory(Writer writer, SchemeParser parser) throws IOException {
        // Factory method declaration
        writer.write(String.format("\tstatic %sExpression %s(%s) {\n",
                parser.className, parser.className.toLowerCase(), String.join(", ", parser.fieldTypes)));

        // Check for the correct Token type
        boolean typeCheck = false;
        for (String[] splitField : parser.fieldElements) {
            if (splitField.length < 3) continue;
            writer.write(String.format("\t\tif (%s_TYPE.contains(%s.type()))\n", splitField[2], splitField[1]));
            typeCheck = true;
        }

        // Return statement
        if (typeCheck) writer.write("\t");
        writer.write(String.format("\t\treturn new %sExpression(%s);\n",
                parser.className, String.join(", ", parser.fieldNames)));

        // Exception for an incorrect Token type
        if (typeCheck)
            writer.write("\t\tthrow new IllegalArgumentException(\"Provided token of cannot be used in literal expression!\");\n");

        writer.write("\t}\n\n");
    }

    private static class SchemeParser {
        private final String className;
        private final String[][] fieldElements;
        private final String[] fieldTypes;
        private final String[] fieldNames;

        private SchemeParser(String expLine) {
            String[] contents = expLine.split(":");
            this.className = contents[0].trim();

            this.fieldElements = Arrays.stream(contents[1].split(","))
                    .map(String::trim)
                    .map(field -> field.split(" "))
                    .toArray(String[][]::new);

            this.fieldTypes = Arrays.stream(fieldElements)
                    .map(words -> String.format("%s %s", words[0], words[1]))
                    .toArray(String[]::new);

            this.fieldNames = Arrays.stream(fieldElements)
                    .map(words -> words[1])
                    .toArray(String[]::new);
        }
    }
}
