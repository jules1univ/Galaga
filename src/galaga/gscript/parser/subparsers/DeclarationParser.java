package galaga.gscript.parser.subparsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import galaga.gscript.ast.declaration.DeclarationBase;
import galaga.gscript.ast.declaration.EnumDeclaration;
import galaga.gscript.ast.declaration.FunctionDeclaration;
import galaga.gscript.ast.declaration.StructDeclaration;
import galaga.gscript.ast.declaration.TypeAliasDeclaration;
import galaga.gscript.ast.statement.StatementBase;
import galaga.gscript.ast.types.TypeBase;
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
        return context.is(Operator.ASSIGN) && context.nextIs(Keyword.ENUM);
    }

    public static DeclarationBase parseEnum(ParserContext context, String name) {
        if (!context.expect(Operator.ASSIGN)) {
            return (DeclarationBase) context.getLastError();
        }

        if (!context.expect(Keyword.ENUM) || !context.expect(Operator.LEFT_BRACE)) {
            return (DeclarationBase) context.getLastError();
        }

        Map<String, TypeBase> values = new HashMap<>();
        while (!context.isEnd() && !context.is(Operator.RIGHT_BRACE)) {
            Optional<String> valueNameOpt = context.getValueExpect(TokenType.IDENTIFIER);
            if (valueNameOpt.isEmpty()) {
                context.advance();
                continue;
            }

            TypeBase valueDataOpt = TypeParser.parseTypeEnumData(context);
            values.put(valueNameOpt.get(), valueDataOpt);
            context.isAndAdvance(Operator.COMMA);
        }

        if (!context.expect(Operator.RIGHT_BRACE)) {
            context.advance();
            context.advanceIfSemicolon();
            return (DeclarationBase) context.getLastError();
        }
        context.advanceIfSemicolon();
        return new EnumDeclaration(name, values);
    }

    public static boolean isStructDeclaration(ParserContext context) {
        return context.is(Operator.ASSIGN) && context.nextIs(Keyword.STRUCT);
    }

    public static DeclarationBase parseStruct(ParserContext context, String name) {
        if (!context.expect(Operator.ASSIGN)) {
            return (DeclarationBase) context.getLastError();
        }

        if (!context.expect(Keyword.STRUCT) || !context.expect(Operator.LEFT_BRACE)) {
            return (DeclarationBase) context.getLastError();
        }

        Map<TypeBase, String> fields = new HashMap<>();
        while (!context.isEnd() && !context.is(Operator.RIGHT_BRACE)) {
            TypeBase fieldType = TypeParser.parseType(context);
            Optional<String> fieldName = context.getValueExpect(TokenType.IDENTIFIER);
            if (fieldName.isEmpty()) {
                context.advance();
                continue;
            }

            fields.put(fieldType, fieldName.get());
            context.isAndAdvance(Operator.SEMICOLON);
        }

        if (!context.expect(Operator.RIGHT_BRACE)) {
            context.advance();
            context.advanceIfSemicolon();
            return (DeclarationBase) context.getLastError();
        }
        context.advanceIfSemicolon();
        return new StructDeclaration(name, fields);
    }

    public static DeclarationBase parseTypeAlias(ParserContext context, String name) {
        if (!context.expect(Operator.ASSIGN)) {
            return (DeclarationBase) context.getLastError();
        }

        if (context.is(TokenType.IDENTIFIER) && context.nextIs(TokenType.IDENTIFIER)) {
            TypeBase functionType = TypeParser.parseTypeFunction(context);
            context.advanceIfSemicolon();
            return new TypeAliasDeclaration(name, Optional.of(functionType), new ArrayList<>());
        }

        List<TypeBase> types = new ArrayList<>();
        types.add(TypeParser.parseType(context));
        while (!context.isEnd() && context.isAndAdvance(Operator.BITWISE_OR)) {
            types.add( TypeParser.parseType(context));
        }
        context.advanceIfSemicolon();
        return new TypeAliasDeclaration(name, Optional.empty(), types);
    }

    public static boolean isFunctionDeclaration(ParserContext context) {
        return context.is(Operator.LEFT_BRACE);
    }

    public static DeclarationBase parseFunction(ParserContext context, TypeBase function) {
        if (!context.expect(Operator.LEFT_BRACE)) {
            return (DeclarationBase) context.getLastError();
        }

        StatementBase body = StatementParser.parseBlock(context);
        if (context.expect(Operator.RIGHT_BRACE)) {
            context.advance();
            context.advanceIfSemicolon();
            return (DeclarationBase) context.getLastError();
        }
        return new FunctionDeclaration(function, body);
    }
}
