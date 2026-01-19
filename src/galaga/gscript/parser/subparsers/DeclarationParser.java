package galaga.gscript.parser.subparsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import galaga.gscript.ast.declaration.EnumDeclaration;
import galaga.gscript.ast.declaration.FunctionDeclaration;
import galaga.gscript.ast.declaration.StructDeclaration;
import galaga.gscript.ast.declaration.TypeAliasDeclaration;
import galaga.gscript.ast.types.Type;
import galaga.gscript.ast.types.TypeEnumData;
import galaga.gscript.ast.types.TypeFunction;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.token.TokenType;
import galaga.gscript.parser.ParserContext;
import galaga.gscript.parser.ParserException;

public final class DeclarationParser {
    public static boolean isTypeDeclaration(ParserContext context) {
        return context.is(Keyword.TYPE) && context.nextIs(TokenType.IDENTIFIER);
    }

    public static String parseTypeDeclaration(ParserContext context) throws ParserException {
        context.expect(Keyword.TYPE);
        return context.getValueExpect(TokenType.IDENTIFIER);
    }

    public static boolean isEnumDeclaration(ParserContext context) {
        return context.is(Operator.ASSIGN) && context.nextIs(Keyword.ENUM);
    }

    public static EnumDeclaration parseEnum(ParserContext context, String name) throws ParserException {
        context.expect(Operator.ASSIGN);
        context.expect(Keyword.ENUM);
        context.expect(Operator.LEFT_BRACE);

        Map<String, TypeEnumData> values = new HashMap<>();
        while (!context.isEnd() && !context.is(Operator.RIGHT_BRACE)) {
            String valueName = context.getValueExpect(TokenType.IDENTIFIER);
            TypeEnumData valueData = TypeParser.parseTypeEnumData(context);
            values.put(valueName, valueData);
            context.isAndAdvance(Operator.COMMA);
        }
        context.expect(Operator.RIGHT_BRACE);
        context.advanceIfSemicolon();
        return new EnumDeclaration(name, values);
    }

    public static boolean isStructDeclaration(ParserContext context) {
        return context.is(Operator.ASSIGN) && context.nextIs(Keyword.STRUCT);
    }

    public static StructDeclaration parseStruct(ParserContext context, String name) throws ParserException {
        context.expect(Operator.ASSIGN);
        context.expect(Keyword.STRUCT);
        context.expect(Operator.LEFT_BRACE);

        Map<Type, String> fields = new HashMap<>();
        while (!context.isEnd() && !context.is(Operator.RIGHT_BRACE)) {
            Type fieldType = TypeParser.parseType(context);
            String fieldName = context.getValueExpect(TokenType.IDENTIFIER);
            fields.put(fieldType, fieldName);
            context.isAndAdvance(Operator.SEMICOLON);
        }

        context.expect(Operator.RIGHT_BRACE);
        context.advanceIfSemicolon();
        return new StructDeclaration(name, fields);
    }

    public static TypeAliasDeclaration parseTypeAlias(ParserContext context, String name) throws ParserException {
        context.expect(Operator.ASSIGN);
        if (context.is(TokenType.IDENTIFIER) && context.nextIs(TokenType.IDENTIFIER)) {
            TypeFunction functionType = TypeParser.parseTypeFunction(context);
            context.advanceIfSemicolon();
            return new TypeAliasDeclaration(name, Optional.of(functionType), new ArrayList<>());
        }

        List<Type> types = new ArrayList<>();
        types.add(TypeParser.parseType(context));
        while (!context.isEnd() && context.isAndAdvance(Operator.BITWISE_OR)) {
            types.add(TypeParser.parseType(context));
        }
        context.advanceIfSemicolon();
        return new TypeAliasDeclaration(name, Optional.empty(), types);
    }

    public static boolean isFunctionDeclaration(ParserContext context) {
        return context.is(Operator.LEFT_BRACE);
    }

    public static FunctionDeclaration parseFunction(ParserContext context, TypeFunction function) throws ParserException {
        return new FunctionDeclaration(function, StatementParser.parseBlock(context));
    }
}
