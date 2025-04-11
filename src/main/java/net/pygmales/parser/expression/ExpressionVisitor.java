package net.pygmales.parser.expression;

import static net.pygmales.parser.expression.Expression.*;

public interface ExpressionVisitor<R> {
	R visitLiteral(LiteralExpression literal);
	R visitBinary(BinaryExpression binary);
	R visitUnary(UnaryExpression unary);
	R visitGrouping(GroupingExpression grouping);
}
