package net.pygmales.parser;

import net.pygmales.lexer.Token;
import net.pygmales.lexer.TokenType;

import net.pygmales.parser.expression.Expression;
import net.pygmales.util.ErrorLogger;

import java.util.List;

import static net.pygmales.lexer.TokenType.*;
import static net.pygmales.parser.expression.Expression.*;

public class Parser {
    private final ErrorLogger logger;
    private final List<Token> tokens;
    private int position = 0;

    public Parser(ErrorLogger logger, List<Token> tokens) {
        this.logger = logger;
        this.tokens = tokens;
    }

    public Expression parse() {
        try {
            return expression();
        } catch (ParseError error) {
            logger.close();
            return Expression.literal(-1);
        }
    }

    private Expression expression() {
        return equality();
    }

    private Expression equality() {
        Expression exp = comparison();

        while (match(EQUALITY_OPERATOR_TYPE)) {
            Token operator = getCurrent();
            proceed();
            Expression right = comparison();
            exp = binary(exp, operator, right);
        }

        return exp;
    }

    private Expression comparison() {
        Expression exp = term();

        while (match(COMPARISON_OPERATOR_TYPE)) {
            Token operator = getCurrent();
            proceed();
            Expression right = term();
            exp = binary(exp, operator, right);
        }

        return exp;
    }

    private Expression term() {
        Expression exp = factor();

        while (match(TERM_OPERATOR_TYPE)) {
            Token operator = getCurrent();
            proceed();
            Expression right = factor();
            exp = binary(exp, operator, right);
        }

        return exp;
    }

    private Expression factor() {
        Expression exp = unary();

        while (match(FACTOR_OPERATOR_TYPE)) {
            Token operator = getCurrent();
            proceed();
            Expression right = unary();
            exp = binary(exp, operator, right);
        }

        return exp;
    }

    private Expression unary() {
        if (match(UNARY_OPERATOR_TYPE)) {
            Token operator = getCurrent();
            proceed();
            Expression right = unary();

            return Expression.unary(operator, right);
        }

        return primary();
    }

    private Expression primary() {
        if (match(PRIMARY_OPERATOR_TYPE)) {
            Expression exp = literal(getCurrent().literal());
            proceed();
            return exp;
        }

        if (match(LPAR)) {
            proceed();
            Expression exp = expression();
            if (consumeOrThrow(RPAR,
                    "unclosed expression",
                    "expression was not properly closed with `)`"))
                proceed();
            return grouping(exp);
        }

        throw error(getNext(), "not an expression", "expression expected");
    }

    private boolean consumeOrThrow(TokenType type, String errorName, String errorDescription) {
        if (match(type)) return true;

        throw error(getCurrent(), errorName, errorDescription);
    }

    private ParseError error(Token token, String errorName, String errorDescription) {
        logger.error(token, errorName, errorDescription);
        return new ParseError();
    }

    private boolean match (TokenType type) {
        return !isLastToken() && type.equals(getCurrent().type());
    }

    private boolean match(List<TokenType> types) {
        return !isLastToken () && types.contains(getCurrent().type());
    }

    private Token getNext() {
        return tokens.get(position+1);
    }

    private Token getCurrent() {
        return tokens.get(position);
    }

    private void proceed() {
        position++;
    }

    private boolean isLastToken() {
        return position == tokens.size()-1;
    }

    private static class ParseError extends RuntimeException {}
}
