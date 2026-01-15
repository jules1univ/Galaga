package galaga.gscript.parser.subparsers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.types.Type;
import galaga.gscript.ast.types.TypeBase;
import galaga.gscript.ast.types.TypeEnumData;
import galaga.gscript.ast.types.TypeError;
import galaga.gscript.ast.types.TypeFunction;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.token.TokenType;
import galaga.gscript.parser.ParserContext;

public final class TypeParser {

    public static TypeBase parseType(ParserContext context) {
        boolean isConst = context.getValueIf(Keyword.CONST).isPresent();
        boolean isRef = context.getValueIf(Keyword.REF).isPresent();
        Optional<String> typeName = context.getValueExpect(TokenType.IDENTIFIER);
        if (typeName.isEmpty()) {
            context.advance();
            return new TypeError("Expected type name.");
        }

        boolean isArray = false;
        if (context.isAndAdvance(Operator.LEFT_BRACKET)) {
            if (!context.expect(Operator.RIGHT_BRACKET)) {
                context.advance();
                return new TypeError("Expected closing ']' for array type.");
            } else {
                isArray = true;
            }
        }

        return new Type(typeName.get(), isConst, isRef, isArray);
    }

    public static TypeBase parseTypeFunction(ParserContext context) {
        TypeBase returnType = TypeParser.parseType(context);
        String functionName = context.getValue();
        if (!context.expect(TokenType.IDENTIFIER) || !context.expect(Operator.LEFT_PAREN)) {
            return new TypeError("Expected function name and parameter list.");
        }

        Map<TypeBase, String> parameters = new HashMap<>();
        while (!context.isEnd() && !context.is(Operator.RIGHT_PAREN)) {
            TypeBase paramType = TypeParser.parseType(context);
            Optional<String> paramName = context.getValueExpect(TokenType.IDENTIFIER);
            if (paramName.isEmpty()) {
                context.advance();
                continue;
            }
            parameters.put(paramType, paramName.get());
            if (!context.isAndAdvance(Operator.COMMA)) {
                break;
            }
        }
        if (!context.expect(Operator.RIGHT_PAREN)) {
            context.advance();
            return new TypeError("Expected closing ')' for parameter list.");
        }

        Optional<String> type = Optional.empty();
        if (context.isAndAdvance(Keyword.EXTENDS)) {
            type = context.getValueExpect(TokenType.IDENTIFIER);
        }

        return new TypeFunction(returnType, functionName, parameters, type);
    }

    public static TypeBase parseTypeEnumData(ParserContext context) {
        Map<TypeBase, String> data = new HashMap<>();
        if (context.isAndAdvance(Operator.LEFT_BRACE)) {
            while (!context.isEnd() && !context.is(Operator.RIGHT_BRACE)) {
                TypeBase dataType = TypeParser.parseType(context);
                Optional<String> dataName = context.getValueExpect(TokenType.IDENTIFIER);
                if (dataName.isEmpty()) {
                    context.advance();
                    continue;
                }

                data.put(dataType, dataName.get());
                if (!context.isAndAdvance(Operator.SEMICOLON)) {
                    break;
                }
            }

            if (!context.expect(Operator.RIGHT_BRACE)) {
                return (TypeError)context.getLastError();
            }
        }

        Optional<ExpressionBase> value = Optional.empty();
        if(ExpressionParser.isLiteralExpression(context))
        {
            value = Optional.of(ExpressionParser.parseLiteralExpression(context));
        }
        return new TypeEnumData(data, value);
    }
}
