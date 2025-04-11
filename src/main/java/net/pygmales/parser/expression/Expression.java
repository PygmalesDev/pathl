package net.pygmales.parser.expression;

import net.pygmales.lexer.Token;
import static net.pygmales.lexer.TokenType.*;

public interface Expression {
	<R> R accept(ExpressionVisitor<R> visitor);

	class LiteralExpression implements Expression {
		public final Object literal;

		public LiteralExpression(Object literal) {
			this.literal = literal;
		}

		@Override
		public <R> R accept(ExpressionVisitor<R> visitor) {
			return visitor.visitLiteral(this);
		}

		@Override
		public String toString() {
			return "Literal[" + literal.toString() + "]";
		}
	}

	static LiteralExpression literal(Object literal) {
		return new LiteralExpression(literal);
	}

	class BinaryExpression implements Expression {
		public final Expression left;
		public final Token binaryOperator;
		public final Expression right;

		public BinaryExpression(Expression left, Token binaryOperator, Expression right) {
			this.left = left;
			this.binaryOperator = binaryOperator;
			this.right = right;
		}

		@Override
		public <R> R accept(ExpressionVisitor<R> visitor) {
			return visitor.visitBinary(this);
		}

		@Override
		public String toString() {
			return "Binary[" + left.toString() + ", " + binaryOperator.toString() + ", " + right.toString() + "]";
		}
	}

	static BinaryExpression binary(Expression left, Token binaryOperator, Expression right) {
		if (BINARY_OPERATOR_TYPE.contains(binaryOperator.type()))
			return new BinaryExpression(left, binaryOperator, right);
		throw new IllegalArgumentException("Provided token of cannot be used in literal expression!");
	}

	class UnaryExpression implements Expression {
		public final Token unaryOperator;
		public final Expression expression;

		public UnaryExpression(Token unaryOperator, Expression expression) {
			this.unaryOperator = unaryOperator;
			this.expression = expression;
		}

		@Override
		public <R> R accept(ExpressionVisitor<R> visitor) {
			return visitor.visitUnary(this);
		}

		@Override
		public String toString() {
			return "Unary[" + unaryOperator.toString() + ", " + expression.toString() + "]";
		}
	}

	static UnaryExpression unary(Token unaryOperator, Expression expression) {
		if (UNARY_OPERATOR_TYPE.contains(unaryOperator.type()))
			return new UnaryExpression(unaryOperator, expression);
		throw new IllegalArgumentException("Provided token of cannot be used in literal expression!");
	}

	class GroupingExpression implements Expression {
		public final Expression expression;

		public GroupingExpression(Expression expression) {
			this.expression = expression;
		}

		@Override
		public <R> R accept(ExpressionVisitor<R> visitor) {
			return visitor.visitGrouping(this);
		}

		@Override
		public String toString() {
			return "Grouping[" + expression.toString() + "]";
		}
	}

	static GroupingExpression grouping(Expression expression) {
		return new GroupingExpression(expression);
	}

}
