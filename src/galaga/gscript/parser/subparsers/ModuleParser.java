package galaga.gscript.parser.subparsers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import galaga.gscript.ast.declaration.DeclarationBase;
import galaga.gscript.ast.declaration.DeclarationError;
import galaga.gscript.ast.declaration.module.ExternDeclaration;
import galaga.gscript.ast.declaration.module.ImportDeclaration;
import galaga.gscript.ast.declaration.module.ModuleDeclaration;
import galaga.gscript.ast.types.TypeBase;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.token.TokenType;
import galaga.gscript.parser.ParserContext;

public final class ModuleParser {
    public static boolean isImportDeclaration(ParserContext context) {
        return context.is(Keyword.IMPORT);
    }

    public static DeclarationBase parseImport(ParserContext context) {
        if (!context.expect(Keyword.IMPORT)) {
            return (DeclarationBase)context.getLastError();
        }

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
                return new DeclarationError("Invalid import syntax.");
            }
        }

        while (functionMode && !context.isEnd() && context.is(TokenType.IDENTIFIER)) {
            functions.add(context.getValueAndAdvance());
            if (context.isAndAdvance(Operator.COMMA)) {
                continue;
            } else if (context.isAndAdvance(Operator.RIGHT_BRACE)) {
                break;
            } else {
                return new DeclarationError("Invalid import function list syntax.");
            }
        }

        context.advanceIfSemicolon();
        return new ImportDeclaration(paths, functions, isWildcard);
    }

    public static boolean isExternDeclaration(ParserContext context) {
        return context.is(Keyword.EXTERN);
    }

    public static DeclarationBase parseExtern(ParserContext context) {
        if (!context.expect(Keyword.EXTERN)) {
            return (DeclarationBase)context.getLastError();
        }

        if (context.isAndAdvance(Keyword.TYPE)) {
            TypeBase typeName = TypeParser.parseType(context);
            context.advanceIfSemicolon();
            return new ExternDeclaration(typeName);
        }

        TypeBase function = TypeParser.parseTypeFunction(context);
        context.advanceIfSemicolon();
        return new ExternDeclaration(function);
    }

    public static boolean isModuleDeclaration(ParserContext context) {
        return context.is(Keyword.MODULE);
    }

    public static DeclarationBase parseModule(ParserContext context) {
        if (!context.expect(Keyword.MODULE)) {
            return (DeclarationBase)context.getLastError();
        }

        Optional<String> moduleName = context.getValueExpect(TokenType.IDENTIFIER);
        if (moduleName.isEmpty()) {
            context.advance();
            context.advanceIfSemicolon();
            return (DeclarationBase)context.getLastError();
        }

        context.advanceIfSemicolon();
        return new ModuleDeclaration(moduleName.get());
    }
}
