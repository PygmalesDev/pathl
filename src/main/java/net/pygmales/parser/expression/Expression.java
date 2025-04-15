package net.pygmales.parser.expression;

import java.util.List;
import net.pygmales.lexer.Token;

import static net.pygmales.lexer.TokenType.*;
import static net.pygmales.parser.expression.Container.*;

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
		public final Token operator;
		public final Expression right;

		public BinaryExpression(Expression left, Token operator, Expression right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		@Override
		public <R> R accept(ExpressionVisitor<R> visitor) {
			return visitor.visitBinary(this);
		}

		@Override
		public String toString() {
			return "Binary[" + left.toString() + ", "
					 + operator.toString() + ", "
					 + right.toString() + "]";
		}
	}

	static BinaryExpression binary(Expression left, Token operator, Expression right) {
		if (BINARY_OPERATOR_TYPE.contains(operator.type()))
			return new BinaryExpression(left, operator, right);
		throw new IllegalArgumentException("Provided token cannot be used in literal expression!");
	}

	class UnaryExpression implements Expression {
		public final Token operator;
		public final Expression expression;

		public UnaryExpression(Token operator, Expression expression) {
			this.operator = operator;
			this.expression = expression;
		}

		@Override
		public <R> R accept(ExpressionVisitor<R> visitor) {
			return visitor.visitUnary(this);
		}

		@Override
		public String toString() {
			return "Unary[" + operator.toString() + ", "
					 + expression.toString() + "]";
		}
	}

	static UnaryExpression unary(Token operator, Expression expression) {
		if (UNARY_OPERATOR_TYPE.contains(operator.type()))
			return new UnaryExpression(operator, expression);
		throw new IllegalArgumentException("Provided token cannot be used in literal expression!");
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
