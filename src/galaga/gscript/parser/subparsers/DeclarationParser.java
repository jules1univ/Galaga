package galaga.gscript.parser.subparsers;

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
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.token.TokenType;
import galaga.gscript.parser.ParserContext;

public final class DeclarationParser {
    public static boolean isTypeDeclaration(ParserContext context) {
        return context.is(Keyword.TYPE) && context.nextIs(TokenType.IDENTIFIER);
    }

    public static Optional<String> parseTypeDeclaration(ParserContext context) {
        if (!context.expect(Keyword.TYPE)) {
            return Optional.empty();
        }

        return context.getValueExpect(TokenType.IDENTIFIER);
    }

    public static boolean isEnumDeclaration(ParserContext context) {
        return context.is(Keyword.ENUM);
    }

    public static Optional<EnumDeclaration> parseEnum(ParserContext context, String name) {
        if (!context.expect(Keyword.ENUM) && !context.expect(Operator.LEFT_BRACE)) {
            return Optional.empty();
        }

        Map<String, Optional<Integer>> values = Map.of();
        while (!context.isEnd() && !context.is(Operator.RIGHT_BRACE)) {
            Optional<String> valueNameOpt = context.getValueExpect(TokenType.IDENTIFIER);
            if (valueNameOpt.isEmpty()) {
                context.advance();
                continue;
            }

            Optional<Integer> valueNumber = Optional.empty();
            if (context.isAndAdvance(Operator.ASSIGN)) {
                Optional<String> numberStrOpt = context.getValueExpect(TokenType.NUMBER);
                if (numberStrOpt.isEmpty()) {
                    context.advance();
                    continue;
                }

                try {
                    valueNumber = Optional.of(Integer.parseInt(numberStrOpt.get()));
                } catch (NumberFormatException e) {
                    context.pushError("Invalid number format: %s", numberStrOpt.get());
                }
            }
            values.put(valueNameOpt.get(), valueNumber);
            context.isAndAdvance(Operator.COMMA);
        }

        if (!context.expect(Operator.RIGHT_BRACE)) {
            context.advance();
            context.advanceIfSemicolon();
            return Optional.empty();
        }
        context.advanceIfSemicolon();
        return Optional.of(new EnumDeclaration(name, values));
    }

    public static boolean isStructDeclaration(ParserContext context) {
        return context.is(Keyword.STRUCT);
    }

    public static Optional<StructDeclaration> parseStruct(ParserContext context, String name) {
        if (!context.expect(Keyword.STRUCT) && !context.expect(Operator.LEFT_BRACE)) {
            return Optional.empty();
        }
        Map<Type, String> fields = Map.of();
        while (!context.isEnd() && !context.is(Operator.RIGHT_BRACE)) {
            Optional<Type> fieldType = TypeParser.parseType(context);
            if (fieldType.isEmpty()) {
                context.advance();
                continue;
            }

            Optional<String> fieldName = context.getValueExpect(TokenType.IDENTIFIER);
            if (fieldName.isEmpty()) {
                context.advance();
                continue;
            }

            fields.put(fieldType.get(), fieldName.get());
            context.isAndAdvance(Operator.SEMICOLON);
        }

        if (!context.expect(Operator.RIGHT_BRACE)) {
            context.advance();
            context.advanceIfSemicolon();
            return Optional.empty();
        }
        context.advanceIfSemicolon();
        return Optional.of(new StructDeclaration(name, fields));
    }

    public static boolean isTypeAliasDeclaration(ParserContext context) {
        return context.is(Operator.ASSIGN);
    }

    public static Optional<TypeAliasDeclaration> parseTypeAlias(ParserContext context, String name) {
        if (!context.expect(Operator.ASSIGN)) {
            return Optional.empty();
        }

        List<Type> types = new ArrayList<>();
        Optional<Type> type = TypeParser.parseType(context);
        if (type.isEmpty()) {
            context.advance();
            return Optional.empty();
        }
        types.add(type.get());
        while (!context.isEnd() && context.isAndAdvance(Operator.BITWISE_OR)) {
            type = TypeParser.parseType(context);
            if (type.isEmpty()) {
                context.advance();
                continue;
            }
            types.add(type.get());
        }
        context.advanceIfSemicolon();
        return Optional.of(new TypeAliasDeclaration(name, types));
    }

    public static boolean isFunctionDeclaration(ParserContext context) {
        return context.is(Operator.LEFT_BRACE);
    }

    public static Optional<FunctionDeclaration> parseFunction(ParserContext context, TypeFunction function) {
        if (!context.expect(Operator.LEFT_BRACE)) {
            return Optional.empty();
        }

        List<Statement> body = new ArrayList<>();
        // TODO

        if(context.expect(Operator.RIGHT_BRACE)) {
            context.advance();
            context.advanceIfSemicolon();
            return Optional.empty();
        }
        return Optional.of(new FunctionDeclaration(function, body));
    }
}
