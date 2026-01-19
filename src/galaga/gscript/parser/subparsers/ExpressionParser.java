package galaga.gscript.parser.subparsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import galaga.gscript.ast.expression.BinaryExpression;
import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.expression.FunctionCallExpression;
import galaga.gscript.ast.expression.StructExpression;
import galaga.gscript.ast.expression.UnaryExpression;
import galaga.gscript.ast.expression.VariableExpression;
import galaga.gscript.ast.expression.literals.LiteralBoolExpression;
import galaga.gscript.ast.expression.literals.LiteralFloatExpression;
import galaga.gscript.ast.expression.literals.LiteralStringExpression;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.rules.OperatorPriority;
import galaga.gscript.lexer.token.TokenType;
import galaga.gscript.parser.ParserContext;
import galaga.gscript.parser.ParserException;

public final class ExpressionParser {
    public static ExpressionBase parseExpression(ParserContext context) throws ParserException {
        return parseBinaryExpression(context, OperatorPriority.MIN_PRIORITY);
    }

    public static boolean isFunctionCallExpression(ParserContext context) {
        return context.is(TokenType.IDENTIFIER) && context.nextIs(Operator.LEFT_PAREN);
    }

    public static ExpressionBase parseFunctionCallExpression(ParserContext context) throws ParserException {
        String name = context.getValueExpect(TokenType.IDENTIFIER);
        context.expect(Operator.LEFT_PAREN);

        List<ExpressionBase> args = new ArrayList<>();
        while (!context.isEnd() && !context.is(Operator.RIGHT_PAREN)) {
            ExpressionBase arg = parseExpression(context);
            args.add(arg);
            if (!context.isAndAdvance(Operator.COMMA)) {
                break;
            }
        }
        context.expect(Operator.RIGHT_PAREN);
        return new FunctionCallExpression(name, args);
    }

    public static ExpressionBase parseStructExpression(ParserContext context) throws ParserException {
        context.expect(Operator.LEFT_BRACE);

        Map<String, ExpressionBase> fields = new HashMap<>();

        while (!context.isEnd() && !context.is(Operator.RIGHT_BRACE)) {
            context.expect(TokenType.IDENTIFIER);
            String fieldName = context.getValueAndAdvance();
            context.expect(Operator.ASSIGN);
            ExpressionBase fieldValue = parseExpression(context);
            fields.put(fieldName, fieldValue);

            if (!context.isAndAdvance(Operator.COMMA)) {
                break;
            }
        }

        context.expect(Operator.RIGHT_BRACE);
        return new StructExpression(fields);
    }

    public static ExpressionBase parseBinaryExpression(ParserContext context, int priority) throws ParserException {
        if (priority > OperatorPriority.MAX_PRIORITY) {
            return parseUnaryExpression(context);
        }

        ExpressionBase left = parseBinaryExpression(context, priority + 1);
        while (!context.isEnd()) {
            Operator operator = context.getOperator();
            if (operator == null || OperatorPriority.getPriority(operator) != priority) {
                break;
            }
            context.advance();

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

    public static ExpressionBase parseUnaryExpression(ParserContext context) throws ParserException {
        if (context.is(Operator.MINUS) || context.is(Operator.PLUS) || context.is(Operator.NOT)) {
            Operator operator = context.getOperator();
            context.advance();
            return new UnaryExpression(operator, parseUnaryExpression(context));
        }

        if (isFunctionCallExpression(context)) {
            return parseFunctionCallExpression(context);
        }

        if (context.is(Operator.LEFT_BRACE)) {
            return parseStructExpression(context);
        }

        if (context.isAndAdvance(Operator.LEFT_PAREN)) {
            ExpressionBase expr = parseExpression(context);
            context.expect(Operator.RIGHT_PAREN);
            return expr;
        }

        return parseLiteralExpression(context);
    }

    public static boolean isLiteralExpression(ParserContext context) {
        return context.is(TokenType.STRING) || context.is(TokenType.NUMBER) ||
                context.is(Keyword.TRUE) || context.is(Keyword.FALSE);
    }

    public static ExpressionBase parseLiteralExpression(ParserContext context) throws ParserException {
        if (context.is(TokenType.STRING)) {
            String value = context.getValueAndAdvance();
            return new LiteralStringExpression(value);
        }

        if (context.is(TokenType.NUMBER)) {
            String value = context.getValueAndAdvance();
            if (context.isAndAdvance(Operator.DOT) && context.is(TokenType.NUMBER)) {
                value += "." + context.getValueAndAdvance();
            }

            try {
                float floatValue = Float.parseFloat(value);
                return new LiteralFloatExpression(floatValue);
            } catch (NumberFormatException e) {

            }
        }

        if (context.is(Keyword.TRUE) || context.is(Keyword.FALSE)) {
            boolean boolValue = context.is(Keyword.TRUE);
            context.advance();
            return new LiteralBoolExpression(boolValue);
        }

        if (context.is(TokenType.IDENTIFIER)) {
            return parseVariableExpression(context);
        }

        throw new ParserException(context, "Expected literal expression.");
    }

    public static ExpressionBase parseVariableExpression(ParserContext context) throws ParserException {
        String name = context.getValueExpect(TokenType.IDENTIFIER);
        if (context.isAndAdvance(Operator.DOT)) {
            return new VariableExpression(name, Optional.of(parseVariableExpression(context)));
        }
        return new VariableExpression(name, Optional.empty());
    }

}
