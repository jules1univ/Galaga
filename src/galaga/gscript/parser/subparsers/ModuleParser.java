package galaga.gscript.parser.subparsers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import galaga.gscript.ast.declaration.module.NativeDeclaration;
import galaga.gscript.ast.declaration.module.ImportDeclaration;
import galaga.gscript.ast.declaration.module.ModuleDeclaration;
import galaga.gscript.ast.types.Type;
import galaga.gscript.ast.types.TypeFunction;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.token.TokenType;
import galaga.gscript.parser.ParserContext;
import galaga.gscript.parser.ParserException;

public final class ModuleParser {
    public static boolean isImportDeclaration(ParserContext context) {
        return context.is(Keyword.IMPORT);
    }

    public static ImportDeclaration parseImport(ParserContext context) throws ParserException {
        context.expect(Keyword.IMPORT);

        LinkedList<String> paths = new LinkedList<>();
        List<String> functions = new ArrayList<>();
        boolean isWildcard = false;

        boolean functionMode = false;
        while (!context.isEnd()) {
            if (context.is(TokenType.IDENTIFIER)) {
                paths.add(context.getValueAndAdvance());
            } else if (context.isAndAdvance(Operator.DOT)) {
                continue;
            } else if (context.isAndAdvance(Operator.LEFT_BRACE)) {
                functionMode = true;
                break;
            } else if (context.isAndAdvance(Operator.MULTIPLY)) {
                isWildcard = true;
                break;
            } else {
                throw new ParserException(context, "Invalid import path syntax.");
            }
        }

        while (functionMode && !context.isEnd() && context.is(TokenType.IDENTIFIER)) {
            functions.add(context.getValueAndAdvance());
            if (context.isAndAdvance(Operator.COMMA)) {
                continue;
            } else if (context.isAndAdvance(Operator.RIGHT_BRACE)) {
                break;
            } else {
                throw new ParserException(context, "Invalid import function list syntax.");
            }
        }

        context.advanceIfSemicolon();
        return new ImportDeclaration(paths, functions, isWildcard);
    }

    public static boolean isNativeDeclaration(ParserContext context) {
        return context.is(Keyword.NATIVE);
    }

    public static NativeDeclaration parseNative(ParserContext context) throws ParserException {
        context.expect(Keyword.NATIVE);
        if (context.isAndAdvance(Keyword.TYPE)) {
            Type typeName = TypeParser.parseType(context);
            context.advanceIfSemicolon();
            return new NativeDeclaration(typeName);
        }

        TypeFunction function = TypeParser.parseTypeFunction(context);
        context.advanceIfSemicolon();
        return new NativeDeclaration(function);
    }

    public static boolean isModuleDeclaration(ParserContext context) {
        return context.is(Keyword.MODULE);
    }

    public static ModuleDeclaration parseModule(ParserContext context) throws ParserException {
        context.expect(Keyword.MODULE);
        String moduleName = context.getValueExpect(TokenType.IDENTIFIER);
        context.advanceIfSemicolon();
        return new ModuleDeclaration(moduleName);
    }
}
