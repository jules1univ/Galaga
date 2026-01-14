package galaga.gscript.parser.subparsers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import galaga.gscript.ast.declaration.module.ExternDeclaration;
import galaga.gscript.ast.declaration.module.ImportDeclaration;
import galaga.gscript.ast.declaration.module.ModuleDeclaration;
import galaga.gscript.ast.types.Type;
import galaga.gscript.ast.types.TypeFunction;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.token.TokenType;
import galaga.gscript.parser.ParserContext;

public final class ModuleParser {
    public static boolean isImportDeclaration(ParserContext context) {
        return context.is(Keyword.IMPORT);
    }

    public static Optional<ImportDeclaration> parseImport(ParserContext context) {
        if (!context.expect(Keyword.IMPORT)) {
            return Optional.empty();
        }

        LinkedList<String> paths = new LinkedList<>();
        List<String> functions = new ArrayList<>();
        boolean isWildcard = false;

        boolean functionMode = false;
        while (!context.isEnd() && context.is(TokenType.IDENTIFIER)) {
            paths.add(context.getValueAndAdvance());

            if (context.isAndAdvance(Operator.DOT)) {
                continue;
            } else if (context.isAndAdvance(Operator.LEFT_BRACE)) {
                functionMode = true;
                break;
            } else if (context.isAndAdvance(Operator.MULTIPLY)) {
                isWildcard = true;
                break;
            } else {
                return Optional.empty();
            }
        }

        while (functionMode && !context.isEnd() && context.is(TokenType.IDENTIFIER)) {
            functions.add(context.getValueAndAdvance());
            if (context.isAndAdvance(Operator.COMMA)) {
                continue;
            } else if (context.isAndAdvance(Operator.RIGHT_BRACE)) {
                break;
            } else {
                return Optional.empty();
            }
        }

        context.advanceIfSemicolon();
        return Optional.of(new ImportDeclaration(paths, functions, isWildcard));
    }

    public static boolean isExternDeclaration(ParserContext context) {
        return context.is(Keyword.EXTERN);
    }

    public static Optional<ExternDeclaration> parseExtern(ParserContext context) {
        if (!context.expect(Keyword.EXTERN)) {
            return Optional.empty();
        }

        if (context.isAndAdvance(Keyword.TYPE)) {
            Optional<Type> typeName = TypeParser.parseType(context);
            if (typeName.isEmpty()) {
                context.advance();
                context.advanceIfSemicolon();
                return Optional.empty();
            }
            context.advanceIfSemicolon();
            return Optional.of(new ExternDeclaration(typeName, Optional.empty()));
        }

        Optional<TypeFunction> function = TypeParser.parseTypeFunction(context);
        if (function.isEmpty()) {
            context.advance();
            context.advanceIfSemicolon();
            return Optional.empty();
        }
        context.advanceIfSemicolon();
        return Optional.of(new ExternDeclaration(Optional.empty(), function));
    }

    public static boolean isModuleDeclaration(ParserContext context) {
        return context.is(Keyword.MODULE);
    }

    public static Optional<ModuleDeclaration> parseModule(ParserContext context) {
        if (!context.expect(Keyword.MODULE)) {
            return Optional.empty();
        }

        Optional<String> moduleName = context.getValueExpect(TokenType.IDENTIFIER);
        if (moduleName.isEmpty()) {
            context.advance();
            return Optional.empty();
        }
        return Optional.of(new ModuleDeclaration(moduleName.get()));
    }
}
