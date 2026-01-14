package galaga.gscript.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import galaga.gscript.ast.declaration.module.ExternDeclaration;
import galaga.gscript.ast.declaration.module.ImportDeclaration;
import galaga.gscript.ast.declaration.module.ModuleDeclaration;
import galaga.gscript.ast.types.Type;
import galaga.gscript.ast.types.TypeFunction;
import galaga.gscript.lexer.Keyword;
import galaga.gscript.lexer.Operator;
import galaga.gscript.lexer.token.TokenType;

public final class ModuleParser {
    public static void parseImport(ParserContext context) {
        if (!context.expect(Keyword.IMPORT)) {
            return;
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
            }
        }

        while (functionMode && !context.isEnd() && context.is(TokenType.IDENTIFIER)) {
            functions.add(context.getValueAndAdvance());
            if (context.isAndAdvance(Operator.COMMA)) {
                continue;
            } else if (context.isAndAdvance(Operator.RIGHT_BRACE)) {
                context.advanceIfSemicolon();
                break;
            }
        }

        context.push(new ImportDeclaration(paths, functions, isWildcard));
    }

    public static void parseExtern(ParserContext context) {
        if (!context.expect(Keyword.EXTERN)) {
            return;
        }

        if (context.isAndAdvance(Keyword.TYPE)) {
            Type typeName = TypeParser.parseType(context);
            context.advanceIfSemicolon();
            context.push(new ExternDeclaration(typeName, null));
        } else {
            TypeFunction function = TypeParser.parseTypeFunction(context);
            context.advanceIfSemicolon();
            context.push(new ExternDeclaration(null, function));
        }
    }

    public static void parseModule(ParserContext context) {
        if (!context.expect(Keyword.MODULE)) {
            return;
        }

        String moduleName = context.getValue();
        if (!context.expect(TokenType.IDENTIFIER)) {
            return;
        }

        context.push(new ModuleDeclaration(moduleName));
    }
}
