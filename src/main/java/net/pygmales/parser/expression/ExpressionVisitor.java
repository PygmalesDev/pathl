package net.pygmales.parser.expression;

import static net.pygmales.parser.expression.Expression.*;

public interface ExpressionVisitor<R> {
	R visitRoot(RootExpression root);
	R visitEquality(EqualityExpression equality);
	R visitComparison(ComparisonExpression comparison);
	R visitTerm(TermExpression term);
	R visitFactor(FactorExpression factor);
	R visitUnary(UnaryExpression unary);
	R visitPrimary(PrimaryExpression primary);
}
