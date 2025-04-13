package net.pygmales.parser.expression;

import java.util.List;
import net.pygmales.lexer.Token;

import static net.pygmales.lexer.TokenType.*;
import static net.pygmales.parser.expression.Container.*;

public interface Expression {
	<R> R accept(ExpressionVisitor<R> visitor);

	class RootExpression implements Expression {
		public final EqualityExpression equality;

		public RootExpression(EqualityExpression equality) {
			this.equality = equality;
		}

		@Override
		public <R> R accept(ExpressionVisitor<R> visitor) {
			return visitor.visitRoot(this);
		}

		@Override
		public String toString() {
			return "Root[" + equality.toString() + "]";
		}
	}

	static RootExpression root(EqualityExpression equality) {
		return new RootExpression(equality);
	}

	class EqualityExpression implements Expression {
		public final ComparisonExpression comparison;
		public final List<OperatorComparisonContainer> operatorComparisonContainers;

		public EqualityExpression(ComparisonExpression comparison, List<OperatorComparisonContainer> operatorComparisonContainers) {
			this.comparison = comparison;
			this.operatorComparisonContainers = operatorComparisonContainers;
		}

		@Override
		public <R> R accept(ExpressionVisitor<R> visitor) {
			return visitor.visitEquality(this);
		}

		@Override
		public String toString() {
			return "Equality[" + comparison.toString() + ", "
					 + operatorComparisonContainers.toString() + "]";
		}
	}

	static EqualityExpression equality(ComparisonExpression comparison, List<OperatorComparisonContainer> operatorComparisonContainers) {
		return new EqualityExpression(comparison, operatorComparisonContainers);
	}

	class ComparisonExpression implements Expression {
		public final TermExpression term;
		public final List<OperatorTermContainer> operatorTermContainers;

		public ComparisonExpression(TermExpression term, List<OperatorTermContainer> operatorTermContainers) {
			this.term = term;
			this.operatorTermContainers = operatorTermContainers;
		}

		@Override
		public <R> R accept(ExpressionVisitor<R> visitor) {
			return visitor.visitComparison(this);
		}

		@Override
		public String toString() {
			return "Comparison[" + term.toString() + ", "
					 + operatorTermContainers.toString() + "]";
		}
	}

	static ComparisonExpression comparison(TermExpression term, List<OperatorTermContainer> operatorTermContainers) {
		return new ComparisonExpression(term, operatorTermContainers);
	}

	class TermExpression implements Expression {
		public final FactorExpression factor;
		public final List<OperatorFactorContainer> operatorFactorContainers;

		public TermExpression(FactorExpression factor, List<OperatorFactorContainer> operatorFactorContainers) {
			this.factor = factor;
			this.operatorFactorContainers = operatorFactorContainers;
		}

		@Override
		public <R> R accept(ExpressionVisitor<R> visitor) {
			return visitor.visitTerm(this);
		}

		@Override
		public String toString() {
			return "Term[" + factor.toString() + ", "
					 + operatorFactorContainers.toString() + "]";
		}
	}

	static TermExpression term(FactorExpression factor, List<OperatorFactorContainer> operatorFactorContainers) {
		return new TermExpression(factor, operatorFactorContainers);
	}

	class FactorExpression implements Expression {
		public final FactorExpression factor;
		public final List<OperatorUnaryContainer> operatorUnaryContainers;

		public FactorExpression(FactorExpression factor, List<OperatorUnaryContainer> operatorUnaryContainers) {
			this.factor = factor;
			this.operatorUnaryContainers = operatorUnaryContainers;
		}

		@Override
		public <R> R accept(ExpressionVisitor<R> visitor) {
			return visitor.visitFactor(this);
		}

		@Override
		public String toString() {
			return "Factor[" + factor.toString() + ", "
					 + operatorUnaryContainers.toString() + "]";
		}
	}

	static FactorExpression factor(FactorExpression factor, List<OperatorUnaryContainer> operatorUnaryContainers) {
		return new FactorExpression(factor, operatorUnaryContainers);
	}

	class UnaryExpression implements Expression {
		public final Token operator;
		public final UnaryExpression unary;
		public final PrimaryExpression primary;

		public UnaryExpression(Token operator, UnaryExpression unary, PrimaryExpression primary) {
			this.operator = operator;
			this.unary = unary;
			this.primary = primary;
		}

		@Override
		public <R> R accept(ExpressionVisitor<R> visitor) {
			return visitor.visitUnary(this);
		}

		@Override
		public String toString() {
			return "Unary[" + operator.toString() + ", "
					 + unary.toString() + ", "
					 + primary.toString() + "]";
		}
	}

	static UnaryExpression unary(Token operator, UnaryExpression unary, PrimaryExpression primary) {
		if (UNARY_OPERATOR_TYPE.contains(operator.type()))
			return new UnaryExpression(operator, unary, primary);
		throw new IllegalArgumentException("Provided token cannot be used in literal expression!");
	}

	class PrimaryExpression implements Expression {
		public final Object literal;
		public final Expression expression;

		public PrimaryExpression(Object literal, Expression expression) {
			this.literal = literal;
			this.expression = expression;
		}

		@Override
		public <R> R accept(ExpressionVisitor<R> visitor) {
			return visitor.visitPrimary(this);
		}

		@Override
		public String toString() {
			return "Primary[" + literal.toString() + ", "
					 + expression.toString() + "]";
		}
	}

	static PrimaryExpression primary(Object literal, Expression expression) {
		return new PrimaryExpression(literal, expression);
	}

}
