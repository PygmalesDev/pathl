package net.pygmales.parser.expression;

import net.pygmales.lexer.Token;
import static net.pygmales.lexer.TokenType.*;

public interface Expression {
	class LiteralExpression implements Expression {
		private final Token literal;

		public LiteralExpression(Token literal) {
			this.literal = literal;
		}

		@Override
		public String toString() {
			return literal.toString();
		}
	}

	static LiteralExpression literal(Token literal) {
		if (LITERAL_TYPE.contains(literal.type()))
			return new LiteralExpression(literal);
		throw new IllegalArgumentException("Provided token of cannot be used in literal expression!");
	}

	class BinaryExpression implements Expression {
		private final Expression left;
		private final Token binaryOperator;
		private final Expression right;

		public BinaryExpression(Expression left, Token binaryOperator, Expression right) {
			this.left = left;
			this.binaryOperator = binaryOperator;
			this.right = right;
		}

		@Override
		public String toString() {
			return left.toString() + " " + binaryOperator.toString() + " " + right.toString();
		}
	}

	static BinaryExpression binary(Expression left, Token binaryOperator, Expression right) {
		if (BINARY_OPERATOR_TYPE.contains(binaryOperator.type()))
			return new BinaryExpression(left, binaryOperator, right);
		throw new IllegalArgumentException("Provided token of cannot be used in literal expression!");
	}

	class UnaryExpression implements Expression {
		private final Token unaryOperator;
		private final Expression expression;

		public UnaryExpression(Token unaryOperator, Expression expression) {
			this.unaryOperator = unaryOperator;
			this.expression = expression;
		}

		@Override
		public String toString() {
			return unaryOperator.toString() + " " + expression.toString();
		}
	}

	static UnaryExpression unary(Token unaryOperator, Expression expression) {
		if (UNARY_OPERATOR_TYPE.contains(unaryOperator.type()))
			return new UnaryExpression(unaryOperator, expression);
		throw new IllegalArgumentException("Provided token of cannot be used in literal expression!");
	}

	class GroupingExpression implements Expression {
		private final Expression expression;

		public GroupingExpression(Expression expression) {
			this.expression = expression;
		}

		@Override
		public String toString() {
			return expression.toString();
		}
	}

	static GroupingExpression grouping(Expression expression) {
		return new GroupingExpression(expression);
	}

}
