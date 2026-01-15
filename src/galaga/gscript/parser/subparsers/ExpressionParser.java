package galaga.gscript.parser.subparsers;

import java.util.ArrayList;
import java.util.List;

import galaga.gscript.ast.expression.BinaryExpression;
import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.expression.ExpressionError;
import galaga.gscript.ast.expression.FunctionCallExpression;
import galaga.gscript.ast.expression.UnaryExpression;
import galaga.gscript.ast.expression.literals.LiteralBoolExpression;
import galaga.gscript.ast.expression.literals.LiteralFloatExpression;
import galaga.gscript.ast.expression.literals.LiteralStringExpression;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.rules.OperatorPriority;
import galaga.gscript.lexer.token.TokenType;
import galaga.gscript.parser.ParserContext;

public final class ExpressionParser {
    public static ExpressionBase parseExpression(ParserContext context) {
        return parseBinaryExpression(context, OperatorPriority.MIN_PRIORITY);
    }

    public static boolean isFunctionCallExpression(ParserContext context) {
        return context.is(TokenType.IDENTIFIER) && context.nextIs(Operator.LEFT_PAREN);
    }

    public static ExpressionBase parseFunctionCallExpression(ParserContext context) {
        if (!context.expect(TokenType.IDENTIFIER) || !context.expect(Operator.LEFT_PAREN)) {
            return (ExpressionBase) context.getLastError();
        }

        String name = context.getValueAndAdvance();
        List<ExpressionBase> args = new ArrayList<>();
        while (!context.isEnd() && !context.is(Operator.RIGHT_PAREN)) {
            ExpressionBase arg = parseExpression(context);
            args.add(arg);
            if (!context.isAndAdvance(Operator.COMMA)) {
                break;
            }
        }
        if (!context.expect(Operator.RIGHT_PAREN)) {
            return (ExpressionBase) context.getLastError();
        }
        return new FunctionCallExpression(name, args);
    }

    public static ExpressionBase parseBinaryExpression(ParserContext context, int priority) {
        if (priority > OperatorPriority.MAX_PRIORITY) {
            return parseUnaryExpression(context);
        }

        ExpressionBase left = parseBinaryExpression(context, priority + 1);
        while (!context.isEnd()) {
            Operator operator = context.getOperatorAndAdvance();
            if (operator == null || OperatorPriority.getPriority(operator) != priority) {
                break;
            }

            ExpressionBase right;
            if (operator == Operator.ASSIGN) {
                right = parseBinaryExpression(context, priority);
            } else {
                right = parseBinaryExpression(context, priority + 1);
            }
            left = new BinaryExpression(left, operator, right);
        }

        return left;
    }

    public static boolean isUnaryExpression(ParserContext context) {
        return context.is(Operator.MINUS) || context.is(Operator.PLUS) || context.is(Operator.NOT) ||
                isLiteralExpression(context);
    }

    public static ExpressionBase parseUnaryExpression(ParserContext context) {
        if (context.is(Operator.MINUS) || context.is(Operator.PLUS) || context.is(Operator.NOT)) {
            Operator operator = context.getOperatorAndAdvance();
            if (operator == null) {
                return new ExpressionError("Expected unary operator.");
            }
            return new UnaryExpression(operator, parseUnaryExpression(context));
        }

        if (isFunctionCallExpression(context)) {
            return parseFunctionCallExpression(context);
        }

        if (context.isAndAdvance(Operator.LEFT_PAREN)) {
            ExpressionBase expr = parseExpression(context);
            if (!context.expect(Operator.RIGHT_PAREN)) {
                return (ExpressionBase) context.getLastError();
            }
            return expr;
        }

        return parseLiteralExpression(context);
    }

    public static boolean isLiteralExpression(ParserContext context) {
        return context.is(TokenType.STRING) || context.is(TokenType.NUMBER) ||
                context.is(Keyword.TRUE) || context.is(Keyword.FALSE);
    }

    public static ExpressionBase parseLiteralExpression(ParserContext context) {
        if (context.is(TokenType.STRING)) {
            String value = context.getValueAndAdvance();
            return new LiteralStringExpression(value);
        }

        if (context.is(TokenType.NUMBER)) {
            String value = context.getValueAndAdvance();
            if (context.isAndAdvance(Operator.DOT)) {
                value += "." + context.getValueAndAdvance();
                if (context.is(TokenType.NUMBER)) {
                    value += context.getValueAndAdvance();
                }
            }

            try {
                float floatValue = Float.parseFloat(value);
                return new LiteralFloatExpression(floatValue);
            } catch (NumberFormatException e) {
                return new ExpressionError("Invalid integer literal: " + value);
            }
        }

        if (context.is(Keyword.TRUE) || context.is(Keyword.FALSE)) {
            boolean boolValue = context.is(Keyword.TRUE);
            context.advance();
            return new LiteralBoolExpression(boolValue);
        }

        return new ExpressionError("Expected literal expression.");
    }
}
