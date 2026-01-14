package galaga.gscript.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import galaga.gscript.ast.declaration.EnumDeclaration;
import galaga.gscript.ast.declaration.FunctionDeclaration;
import galaga.gscript.ast.declaration.StructDeclaration;
import galaga.gscript.ast.declaration.TypeAliasDeclaration;
import galaga.gscript.ast.statement.Statement;
import galaga.gscript.ast.types.Type;
import galaga.gscript.ast.types.TypeFunction;
import galaga.gscript.lexer.Keyword;
import galaga.gscript.lexer.Operator;
import galaga.gscript.lexer.token.TokenType;

public final class DeclarationParser {
    public static void parseEnum(ParserContext context, String name) {
        if (!context.expect(Keyword.ENUM) && !context.expect(Operator.LEFT_BRACE)) {
            return;
        }

        Map<String, Optional<Integer>> values = Map.of();
        while (!context.isEnd() && !context.is(Operator.RIGHT_BRACE)) {
            String valueName = context.getValue();
            if (context.expect(TokenType.IDENTIFIER)) {
                context.advance();
            }

            Optional<Integer> valueNumber = Optional.empty();
            if (context.isAndAdvance(Operator.ASSIGN)) {
                String numberStr = context.getValue();
                if (!context.expect(TokenType.NUMBER)) {
                    context.advance();
                }

                try {
                    valueNumber = Optional.of(Integer.parseInt(numberStr));
                } catch (NumberFormatException e) {
                    context.pushError("Invalid number format: %s", numberStr);
                }
            }
            values.put(valueName, valueNumber);
            context.isAndAdvance(Operator.COMMA);
        }

        if (!context.expect(Operator.RIGHT_BRACE)) {
            context.advance();
        }
        context.advanceIfSemicolon();
        context.push(new EnumDeclaration(name, values));
    }

    public static void parseStruct(ParserContext context, String name) {
        if (!context.expect(Keyword.STRUCT) && !context.expect(Operator.LEFT_BRACE)) {
            return;
        }
        Map<Type, String> fields = Map.of();
        while (!context.isEnd() && !context.is(Operator.RIGHT_BRACE)) {
            Type fieldType = TypeParser.parseType(context);

            String fieldName = context.getValue();
            if (!context.expect(TokenType.IDENTIFIER)) {
                context.advance();
            }
            fields.put(fieldType, fieldName);
            context.isAndAdvance(Operator.SEMICOLON);
        }

        if (!context.expect(Operator.RIGHT_BRACE)) {
            context.advance();
        }
        context.advanceIfSemicolon();
        context.push(new StructDeclaration(name, fields));
    }

    public static void parseTypeAlias(ParserContext context) {
        if (!context.expect(Keyword.TYPE)) {
            return;
        }

        String typeName = context.getValue();
        if (!context.expect(TokenType.IDENTIFIER) && !context.expect(Operator.ASSIGN)) {
            return;
        }

        List<Type> types = new ArrayList<>();
        types.add(TypeParser.parseType(context));
        while (!context.isEnd() && context.isAndAdvance(Operator.BITWISE_OR)) {
            types.add(TypeParser.parseType(context));
        }
        context.advanceIfSemicolon();
        context.push(new TypeAliasDeclaration(typeName, types));
    }

    public static void parseFunction(ParserContext context, TypeFunction function) {
        if(!context.expect(Operator.LEFT_BRACE)) {
            return;
        }
        List<Statement> body = new ArrayList<>();
        // TODO
        context.push(new FunctionDeclaration(function, body));
    }
}
