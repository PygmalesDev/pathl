package net.pygmales.parser;

import net.pygmales.parser.expression.Expression;
import net.pygmales.parser.expression.Expression.*;
import net.pygmales.parser.expression.ExpressionVisitor;
import net.pygmales.util.ErrorLogger;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class SyntaxTreePrinter implements ExpressionVisitor<String> {
    private int depth = -1;
    private final Set<Integer> binaryAtLevel = new HashSet<>();
    private boolean isBinary = false;

    public String print(Expression root) {
        return root.accept(this);
    }

    @Override
    public String visitLiteral(LiteralExpression literal) {
        return ErrorLogger.red(literal.literal.toString()) + "\n";
    }

    @Override
    public String visitBinary(BinaryExpression binary) {
        this.binaryAtLevel.add(++this.depth);
        this.isBinary = true;
        return this.nest(binary.operator.literal().toString(), binary.left, binary.right);
    }

    @Override
    public String visitUnary(UnaryExpression unary) {
        this.depth++;
        return this.nest(unary.operator.literal().toString(), unary.expression);
    }

    @Override
    public String visitGrouping(Expression.GroupingExpression grouping) {
        this.depth++;
        return this.nest("()", grouping.expression);
    }

    private String nest(String literal, Expression... expressions) {
        StringBuilder builder = new StringBuilder();

        builder.append(ErrorLogger.yellow(literal));
        builder.append("\n");

        int binaryTrailLevel = -1;
        if (this.isBinary) binaryTrailLevel = this.depth;

        this.intend(builder);
        builder.append("┃\n");
        for (Expression exp : expressions) {
            this.intend(builder);
            if (this.isBinary) builder.append("┣");
            else builder.append("┗");
            builder.append("━▶ ");
            this.isBinary = false;

            if (exp.equals(expressions[expressions.length-1])) {
                if (binaryTrailLevel >= 0) this.binaryAtLevel.remove(binaryTrailLevel);
            }

            builder.append(exp.accept(this));
        }

        this.depth--;
        return builder.toString();
    }

    private void intend(StringBuilder builder) {
        IntStream.range(0, this.depth).forEach(i -> {
            if (this.binaryAtLevel.contains(i)) builder.append("┃\t");
            else builder.append("\t");
        });
    }
}
