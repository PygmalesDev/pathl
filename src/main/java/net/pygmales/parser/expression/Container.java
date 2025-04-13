package net.pygmales.parser.expression;

import net.pygmales.lexer.Token;

import static net.pygmales.parser.expression.Expression.*;

public interface Container {
	record OperatorTermContainer(Token operator, TermExpression term) implements Container { }
	record OperatorUnaryContainer(Token operator, UnaryExpression unary) implements Container { }
	record OperatorFactorContainer(Token operator, FactorExpression factor) implements Container { }
	record OperatorComparisonContainer(Token operator, ComparisonExpression comparison) implements Container { }
}
