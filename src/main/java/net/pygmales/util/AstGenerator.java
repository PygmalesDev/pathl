package net.pygmales.util;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AstGenerator {
    private static final Path EXPRESSION_PATH = Path.of("./src/main/generated/net/pygmales/parser/expression/Expression.java");
    private static final Path CONTAINER_PATH = Path.of("./src/main/generated/net/pygmales/parser/expression/Container.java");
    private static final Path VISITOR_PATH = Path.of("./src/main/generated/net/pygmales/parser/expression/ExpressionVisitor.java");

    private static final String OPERATOR = "Operator";
    private static final String LITERAL = "Literal";
    private static final String EXPRESSION = "Expression";

    private static final Map<String, List<String>> CONTAINERS = new HashMap<>();

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL schemeUrl = AstGenerator.class.getResource("/scheme.exp");
        if (Objects.isNull(schemeUrl)) System.exit(42);

        Files.createDirectories(EXPRESSION_PATH.getParent());
        Files.writeString(EXPRESSION_PATH, "");
        Files.writeString(VISITOR_PATH, "");
        Files.writeString(CONTAINER_PATH, "");

        BufferedReader reader = Files.newBufferedReader(Path.of(schemeUrl.toURI()));
        FileWriter exp_writer = new FileWriter(EXPRESSION_PATH.toFile());
        FileWriter cont_writer = new FileWriter(CONTAINER_PATH.toFile());
        FileWriter visitor_writer = new FileWriter(VISITOR_PATH.toFile());

        addExpressionHeaders(exp_writer);
        addContainerHeaders(cont_writer);
        addVisitorHeaders(visitor_writer);

        addExpressionInterfaceFields(exp_writer);

        cont_writer.write("public interface Container {\n");
        visitor_writer.write("public interface ExpressionVisitor<R> {\n");

        for (String line : reader.lines().toList()) {
            if (line.isEmpty()) continue;

            SchemeParser parser = new SchemeParser(line);
            addVisitMethod(visitor_writer, parser);
            addExpressionClass(exp_writer, parser);
            addExpressionFactory(exp_writer, parser);
        }
        reader.close();

        addContainers(cont_writer);

        exp_writer.write("}\n");
        cont_writer.write("}\n");
        visitor_writer.write("}\n");

        visitor_writer.close();
        cont_writer.close();
        exp_writer.close();
    }

    private static void addExpressionInterfaceFields(FileWriter writer) throws IOException {
        writer.write("public interface Expression {\n");
        writer.write("\t<R> R accept(ExpressionVisitor<R> visitor);\n\n");
    }

    private static void addExpressionHeaders(Writer writer) throws IOException {
        writer.write("package net.pygmales.parser.expression;\n\n");
        writer.write("import java.util.List;\n");
        writer.write("import net.pygmales.lexer.Token;\n\n");
        writer.write("import static net.pygmales.lexer.TokenType.*;\n");
        writer.write("import static net.pygmales.parser.expression.Container.*;\n\n");
    }

    private static void addContainerHeaders(Writer writer) throws IOException {
        writer.write("package net.pygmales.parser.expression;\n\n");
        writer.write("import net.pygmales.lexer.Token;\n\n");
        writer.write("import static net.pygmales.parser.expression.Expression.*;\n\n");
    }

    private static void addVisitorHeaders(Writer writer) throws IOException {
        writer.write("package net.pygmales.parser.expression;\n\n");
        writer.write("import static net.pygmales.parser.expression.Expression.*;\n\n");
    }

    private static void addVisitMethod(Writer writer, SchemeParser parser) throws IOException {
        writer.write(String.format("\tR visit%s(%sExpression %s);\n",
                parser.className, parser.className, parser.className.toLowerCase()));
    }

    private static void addContainers(Writer writer) throws IOException {
        for (Entry<String, List<String>> entry : CONTAINERS.entrySet()) {
            String containerName = entry.getKey();
            List<String> containerFields = entry.getValue();

            writer.write(String.format("\trecord %sContainer(%s) implements Container { }\n",
                    containerName, String.join(", ", containerFields.stream()
                            .map(AstGenerator::getFieldDefinition).toList())));
        }
    }

    private static void addExpressionClass(Writer writer, SchemeParser parser) throws IOException {
        // Class definition
        writer.write(String.format("\tclass %s%s implements %s {\n", parser.className, EXPRESSION, EXPRESSION));

        // Class fields
        for (String field : parser.fields) writer.write(getClassField(field));
        writer.write("\n");

        // Class constructor
        writer.write(String.format("\t\tpublic %s%s(%s) {\n", parser.className, EXPRESSION, String.join(", ",
                parser.fields.stream().map(AstGenerator::getFieldDefinition).toList())));
        for (String fieldName : parser.fields.stream().map(AstGenerator::getFieldAssignment).toList())
            writer.write(fieldName);
        writer.write("\t\t}\n\n");

        // accept() implementation
        writer.write("\t\t@Override\n\t\tpublic <R> R accept(ExpressionVisitor<R> visitor) {\n");
        writer.write(String.format("\t\t\treturn visitor.visit%s(this);\n", parser.className));
        writer.write("\t\t}\n\n");

        // toString() method
        writer.write("\t\t@Override\n\t\tpublic String toString() {\n");
        writer.write(String.format("\t\t\treturn \"%s[\" + %s + \"]\";\n\t\t}\n",
                parser.className, parser.fields.stream().map(AstGenerator::getFieldName)
                .map(name -> String.format("%s.toString()", name))
                .collect(Collectors.joining(" + \", \"\n\t\t\t\t\t + "))));

        writer.write("\t}\n\n");
    }

    private static void addExpressionFactory(Writer writer, SchemeParser parser) throws IOException {
        // Factory method declaration
        writer.write(String.format("\tstatic %sExpression %s(%s) {\n", parser.className, parser.className.toLowerCase(),
                String.join(", ", parser.fields.stream().map(AstGenerator::getFieldDefinition).toList())));

        // Check for the correct Token type
        boolean typeCheck = parser.fields.stream().anyMatch(field -> field.equals(OPERATOR));

        if (typeCheck)
            writer.write(String.format("\t\tif (%s_OPERATOR_TYPE.contains(operator.type()))\n", parser.className.toUpperCase()));

        // Return statement
        if (typeCheck) writer.write("\t");
        writer.write(String.format("\t\treturn new %sExpression(%s);\n",
                parser.className, String.join(", ", parser.fields.stream().map(AstGenerator::getFieldName).toList())));

        // Exception for an incorrect Token type
        if (typeCheck)
            writer.write("\t\tthrow new IllegalArgumentException(\"Provided token cannot be used in literal expression!\");\n");

        writer.write("\t}\n\n");
    }

    private static String getClassField(String field) {
        return String.format("\t\tpublic final %s;\n", getFieldDefinition(field));
    }

    private static String getFieldName(String field) {
        if (CONTAINERS.containsKey(field)) field = field + "Containers";
        return toSnakeCase(field);
    }

    private static String getFieldAssignment(String field) {
        field = getFieldName(field);
        return String.format("\t\t\tthis.%s = %s;\n", field, field);
    }

    private static String getFieldDefinition(String field) {
        if (CONTAINERS.containsKey(field)) return String.format("List<%sContainer> %s", field, getFieldName(field));
        return switch (field) {
            case EXPRESSION -> "Expression expression";
            case OPERATOR -> "Token operator";
            case LITERAL -> "Object literal";
            default -> String.format("%sExpression %s", field, field.toLowerCase());
        };
    }

    private static String toSnakeCase(String field) {
        return field.substring(0, 1).toLowerCase() + field.substring(1);
    }

    private static class SchemeParser {
        private final String className;
        private final List<String> fields;

        private SchemeParser(String expLine) {
            List<String> contents = Arrays.stream(expLine.split("->"))
                    .map(String::trim).toList();
            className = getFormattedClassName(contents.getFirst());

            String fieldNames = contents.getLast();
            fields = fieldNames.contains("*") ?
                    splitWithContainers(fieldNames) :
                    splitNormally(fieldNames);
        }

        private List<String> splitWithContainers(String fieldNames) {
            List<String> fields = new ArrayList<>();
            Map<String, List<String>> containerFieldsMap = new HashMap<>();
            Pattern pattern = Pattern.compile("\\(.*\\)\\*");
            Matcher matcher = pattern.matcher(fieldNames);

            int lastIndex = 0;
            while (matcher.find()) {
                fields.addAll(splitNormally(fieldNames.substring(lastIndex, matcher.start())));
                String container = fieldNames.substring(matcher.start()+1, matcher.end()-2);
                List<String> containerFields = getContainerFields(container);
                String containerName = getContainerClassName(containerFields);

                fields.add(containerName);
                containerFieldsMap.put(containerName, containerFields);
                lastIndex = matcher.end();
            }

            CONTAINERS.putAll(containerFieldsMap);
            return fields;
        }

        private static List<String> splitNormally(String fieldNames) {
            return Arrays.stream(fieldNames.split(","))
                    .map(String::trim)
                    .filter(string -> !string.isEmpty())
                    .map(SchemeParser::getFormattedClassName)
                    .toList();
        }

        private static String getFormattedClassName(String field) {
            return field.substring(0, 1).toUpperCase() + field.substring(1);
        }

        private static String getContainerClassName(List<String> containerFields) {
            return containerFields.stream()
                    .map(String::trim)
                    .map(SchemeParser::getFormattedClassName)
                    .collect(Collectors.joining());
        }

        private static List<String> getContainerFields(String container) {
          return Arrays.stream(container.split(", "))
                  .map(String::trim)
                  .map(SchemeParser::getFormattedClassName).toList();
        }
    }
}
