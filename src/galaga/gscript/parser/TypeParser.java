package galaga.gscript.parser;

import java.util.Map;

import galaga.gscript.ast.types.Type;
import galaga.gscript.ast.types.TypeFunction;
import galaga.gscript.lexer.Keyword;
import galaga.gscript.lexer.Operator;
import galaga.gscript.lexer.token.TokenType;

public final class TypeParser {
    public static Type parseType(ParserContext context) {
        boolean isConst = context.getValueIf(Keyword.CONST).isPresent();
        boolean isRef = context.getValueIf(Keyword.REF).isPresent();
        String typeName = context.getValueAndAdvance();

        boolean isArray = false;
        if (context.isAndAdvance(Operator.LEFT_BRACKET)) {
            if (!context.expect(Operator.RIGHT_BRACKET)) {
                context.advance();
            } else {
                isArray = true;
            }
        }

        return new Type(typeName, isConst, isRef, isArray);
    }

    public static TypeFunction parseTypeFunction(ParserContext context) {
        Type returnType = TypeParser.parseType(context);

        String functionName = context.getValue();
        if (!context.expect(TokenType.IDENTIFIER) && !context.expect(Operator.LEFT_PAREN)) {
            return null;
        }

        Map<Type, String> parameters = Map.of();
        while (!context.isEnd() && !context.is(Operator.RIGHT_PAREN)) {
            Type paramType = TypeParser.parseType(context);
            String paramName = context.getValue();
            if (!context.expect(TokenType.IDENTIFIER)) {
                context.advance();
            }
            parameters.put(paramType, paramName);
            if (!context.isAndAdvance(Operator.COMMA)) {
                break;
            }
        }
        if (!context.expect(Operator.RIGHT_PAREN)) {
            context.advance();
        }
        return new TypeFunction(returnType, functionName, parameters);
    }

}
