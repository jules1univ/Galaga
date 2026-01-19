package galaga.gscript.parser.subparsers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.types.Type;
import galaga.gscript.ast.types.TypeEnumData;
import galaga.gscript.ast.types.TypeFunction;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.token.TokenType;
import galaga.gscript.parser.ParserContext;
import galaga.gscript.parser.ParserException;

public final class TypeParser {

    public static boolean isType(ParserContext context) {
        return context.is(Keyword.CONST) || context.is(Keyword.REF)
                || (context.is(TokenType.IDENTIFIER) && context.nextIs(TokenType.IDENTIFIER));
    }

    public static Type parseType(ParserContext context) throws ParserException {
        boolean isConst = context.getValueIf(Keyword.CONST).isPresent();
        boolean isRef = context.getValueIf(Keyword.REF).isPresent();
        String typeName = context.getValueExpect(TokenType.IDENTIFIER);

        boolean isArray = false;
        if (context.isAndAdvance(Operator.LEFT_BRACKET)) {
            context.expect(Operator.RIGHT_BRACKET);
            isArray = true;
        }

        return new Type(typeName, isConst, isRef, isArray);
    }

    public static TypeFunction parseTypeFunction(ParserContext context) throws ParserException {
        Type returnType = TypeParser.parseType(context);
        String name = context.getValue();

        context.expect(TokenType.IDENTIFIER);
        context.expect(Operator.LEFT_PAREN);

        Map<Type, String> parameters = new HashMap<>();
        while (!context.isEnd() && !context.is(Operator.RIGHT_PAREN)) {
            Type paramType = TypeParser.parseType(context);
            String paramName = context.getValueExpect(TokenType.IDENTIFIER);
            parameters.put(paramType, paramName);
            if (!context.isAndAdvance(Operator.COMMA)) {
                break;
            }
        }

        context.expect(Operator.RIGHT_PAREN);

        Optional<Type> extendType = Optional.empty();
        if (context.isAndAdvance(Keyword.EXTENDS)) {
            extendType = Optional.of(parseType(context));
        }

        return new TypeFunction(returnType, name, parameters, extendType);
    }

    public static TypeEnumData parseTypeEnumData(ParserContext context) throws ParserException {
        Map<Type, String> data = new HashMap<>();
        if (context.isAndAdvance(Operator.LEFT_BRACE)) {
            while (!context.isEnd() && !context.is(Operator.RIGHT_BRACE)) {
                Type dataType = TypeParser.parseType(context);
                String dataName = context.getValueExpect(TokenType.IDENTIFIER);
                data.put(dataType, dataName);
                if (!context.isAndAdvance(Operator.SEMICOLON)) {
                    break;
                }
            }

            context.expect(Operator.RIGHT_BRACE);
        }

        Optional<ExpressionBase> value = Optional.empty();
        if (context.isAndAdvance(Operator.ASSIGN) && ExpressionParser.isLiteralExpression(context)) {
            value = Optional.of(ExpressionParser.parseLiteralExpression(context));
        }

        return new TypeEnumData(data, value);
    }
}
