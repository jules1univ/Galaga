package galaga.gscript.parser.subparsers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import galaga.gscript.ast.types.Type;
import galaga.gscript.ast.types.TypeFunction;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.token.TokenType;
import galaga.gscript.parser.ParserContext;

public final class TypeParser {

    public static Optional<Type> parseType(ParserContext context) {
        boolean isConst = context.getValueIf(Keyword.CONST).isPresent();
        boolean isRef = context.getValueIf(Keyword.REF).isPresent();
        Optional<String> typeName = context.getValueExpect(TokenType.IDENTIFIER);
        if (typeName.isEmpty()) {
            context.advance();
            return Optional.empty();
        }

        boolean isArray = false;
        if (context.isAndAdvance(Operator.LEFT_BRACKET)) {
            if (!context.expect(Operator.RIGHT_BRACKET)) {
                context.advance();
                return Optional.empty();
            } else {
                isArray = true;
            }
        }

        return Optional.of(new Type(typeName.get(), isConst, isRef, isArray));
    }

    public static Optional<TypeFunction> parseTypeFunction(ParserContext context) {
        Optional<Type> returnType = TypeParser.parseType(context);
        if (returnType.isEmpty()) {
            return Optional.empty();
        }

        String functionName = context.getValue();
        if (!context.expect(TokenType.IDENTIFIER) || !context.expect(Operator.LEFT_PAREN)) {
            return Optional.empty();
        }

        Map<Type, String> parameters = new HashMap<>();
        while (!context.isEnd() && !context.is(Operator.RIGHT_PAREN)) {
            Optional<Type> paramType = TypeParser.parseType(context);
            if (paramType.isEmpty()) {
                context.advance();
                continue;
            }
            Optional<String> paramName = context.getValueExpect(TokenType.IDENTIFIER);
            if (paramName.isEmpty()) {
                context.advance();
                continue;
            }
            parameters.put(paramType.get(), paramName.get());
            if (!context.isAndAdvance(Operator.COMMA)) {
                break;
            }
        }
        if (!context.expect(Operator.RIGHT_PAREN)) {
            context.advance();
            return Optional.empty();
        }

        Optional<String> type = Optional.empty();
        if (context.isAndAdvance(Keyword.EXTENDS)) {
            type = context.getValueExpect(TokenType.IDENTIFIER);
        }

        return Optional.of(new TypeFunction(returnType.get(), functionName, parameters, type));
    }

}
