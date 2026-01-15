package galaga.gscript.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import galaga.gscript.ast.Program;
import galaga.gscript.ast.declaration.DeclarationBase;
import galaga.gscript.ast.declaration.DeclarationError;
import galaga.gscript.ast.types.TypeBase;
import galaga.gscript.lexer.Lexer;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.parser.subparsers.DeclarationParser;
import galaga.gscript.parser.subparsers.ModuleParser;
import galaga.gscript.parser.subparsers.TypeParser;

public final class Parser {
    private final List<DeclarationBase> declarations = new ArrayList<>();
    private final ParserContext context;

    public static Parser of(Lexer lexer) {
        return new Parser(ParserContext.of(lexer));
    }

    public static Parser of(List<Token> tokens) {
        return new Parser(ParserContext.of(tokens));
    }

    private Parser(ParserContext ctx) {
        this.context = ctx;
    }

    private void parseModuleDeclarations() {
        if (ModuleParser.isModuleDeclaration(this.context)) {
            this.declarations.add(ModuleParser.parseModule(this.context));
            return;
        } else if (ModuleParser.isImportDeclaration(this.context)) {
            this.declarations.add(ModuleParser.parseImport(this.context));
            return;
        } else if (ModuleParser.isExternDeclaration(this.context)) {
            this.declarations.add(ModuleParser.parseExtern(this.context));
            return;
        }
    }

    private void parseTypeDeclarations() {
        if (!DeclarationParser.isTypeDeclaration(this.context)) {
            return;
        }
        Optional<String> typeNameOpt = DeclarationParser.parseTypeDeclaration(this.context);
        if (typeNameOpt.isEmpty()) {
            return;
        }
        String typeName = typeNameOpt.get();
        if (DeclarationParser.isEnumDeclaration(this.context)) {
            DeclarationBase enumDecl = DeclarationParser.parseEnum(this.context, typeName);
            this.declarations.add(enumDecl);
        } else if (DeclarationParser.isStructDeclaration(this.context)) {
            DeclarationBase structDecl = DeclarationParser.parseStruct(this.context, typeName);
            this.declarations.add(structDecl);
        } else {
            DeclarationBase typeAliasDecl = DeclarationParser.parseTypeAlias(this.context, typeName);
            this.declarations.add(typeAliasDecl);
        }
    }

    private void parseFunctionDeclaration() {
        if (!DeclarationParser.isFunctionDeclaration(this.context)) {
            return;
        }
        TypeBase functionSignature = TypeParser.parseTypeFunction(this.context);
        this.declarations.add(DeclarationParser.parseFunction(this.context, functionSignature));
    }

    public Program parse() {
        int lastIndex = -1;
        while (!this.context.isEnd()) {
            if (this.context.getIndex() == lastIndex) {
                this.declarations.add(new DeclarationError(
                        String.format("Parser is stuck at token: %s", this.context.getCurrentToken())));
                this.context.advance();
            } else {
                lastIndex = this.context.getIndex();
            }

            this.parseModuleDeclarations();
            this.parseTypeDeclarations();
            this.parseFunctionDeclaration();

        }

        return new Program(this.declarations);
    }
}
