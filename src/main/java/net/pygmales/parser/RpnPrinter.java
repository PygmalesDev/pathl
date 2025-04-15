package net.pygmales.parser;

import net.pygmales.parser.expression.Expression;
import net.pygmales.parser.expression.ExpressionVisitor;

import java.util.Stack;

import static net.pygmales.parser.expression.Expression.*;

public class RpnPrinter implements ExpressionVisitor<String> {
    Stack<String> stack = new Stack<>();

    public String print(Expression expression) {
        expression.accept(this);
        return String.join(" ", stack);
    }

    @Override
    public String visitLiteral(LiteralExpression literal) {
        stack.push(literal.literal.toString());
        return null;
    }

    @Override
    public String visitBinary(BinaryExpression binary) {
        binary.right.accept(this);
        binary.left.accept(this);
        stack.push(binary.operator.literal().toString());
        return null;
    }

    @Override
    public String visitUnary(UnaryExpression unary) {
        stack.push(unary.operator.literal().toString());
        unary.expression.accept(this);
        return null;
    }

    @Override
    public String visitGrouping(GroupingExpression grouping) {
        stack.push("[");
        grouping.expression.accept(this);
        stack.push("]");
        return null;
    }
}
