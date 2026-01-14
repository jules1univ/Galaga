package galaga.gscript.parser;

import java.util.List;
import java.util.Optional;

import galaga.gscript.ast.Program;
import galaga.gscript.ast.declaration.EnumDeclaration;
import galaga.gscript.ast.declaration.FunctionDeclaration;
import galaga.gscript.ast.declaration.StructDeclaration;
import galaga.gscript.ast.declaration.TypeAliasDeclaration;
import galaga.gscript.ast.declaration.module.ExternDeclaration;
import galaga.gscript.ast.declaration.module.ImportDeclaration;
import galaga.gscript.ast.declaration.module.ModuleDeclaration;
import galaga.gscript.ast.types.TypeFunction;
import galaga.gscript.lexer.Lexer;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.parser.subparsers.DeclarationParser;
import galaga.gscript.parser.subparsers.ModuleParser;
import galaga.gscript.parser.subparsers.TypeParser;

public final class Parser {
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
            Optional<ModuleDeclaration> moduleDecl = ModuleParser.parseModule(this.context);
            if (moduleDecl.isPresent()) {
                this.context.push(moduleDecl.get());
                return;
            }
            this.context.pushError("Failed to parse module declaration.");
        } else if (ModuleParser.isImportDeclaration(this.context)) {
            Optional<ImportDeclaration> importDecl = ModuleParser.parseImport(this.context);
            if (importDecl.isPresent()) {
                this.context.push(importDecl.get());
                return;
            }
            this.context.pushError("Failed to parse import declaration.");
        } else if (ModuleParser.isExternDeclaration(this.context)) {
            Optional<ExternDeclaration> externDecl = ModuleParser.parseExtern(this.context);
            if (externDecl.isPresent()) {
                this.context.push(externDecl.get());
                return;
            }
            this.context.pushError("Failed to parse extern declaration.");
        }
    }

    private void parseTypeDeclarations() {
        if (!DeclarationParser.isTypeDeclaration(this.context)) {
            return;
        }
        Optional<String> typeName = DeclarationParser.parseTypeDeclaration(this.context);
        if (typeName.isEmpty()) {
            return;
        }

        if (DeclarationParser.isEnumDeclaration(this.context)) {
            Optional<EnumDeclaration> enumDecl = DeclarationParser.parseEnum(this.context, typeName.get());
            if (enumDecl.isPresent()) {
                this.context.push(enumDecl.get());
                return;
            }
            this.context.pushError("Failed to parse enum declaration: " + typeName.get());
        } else if (DeclarationParser.isStructDeclaration(this.context)) {
            Optional<StructDeclaration> structDecl = DeclarationParser.parseStruct(this.context, typeName.get());
            if (structDecl.isPresent()) {
                this.context.push(structDecl.get());
                return;
            }
            this.context.pushError("Failed to parse struct declaration: " + typeName.get());
        } else if (DeclarationParser.isTypeAliasDeclaration(this.context)) {
            Optional<TypeAliasDeclaration> typeAliasDecl = DeclarationParser.parseTypeAlias(this.context,
                    typeName.get());
            if (typeAliasDecl.isPresent()) {
                this.context.push(typeAliasDecl.get());
                return;
            }
            this.context.pushError("Failed to parse type alias declaration: " + typeName.get());
        } else {
            this.context.pushError("Unknown type declaration: " + typeName.get());
        }

    }

    public Program parse() {
        while (!this.context.isEnd()) {
            this.parseModuleDeclarations();
            this.parseTypeDeclarations();

            if (DeclarationParser.isFunctionDeclaration(this.context)) {
                Optional<TypeFunction> functionSignature = TypeParser.parseTypeFunction(this.context);
                if (functionSignature.isPresent()) {
                    Optional<FunctionDeclaration> function = DeclarationParser.parseFunction(this.context,
                            functionSignature.get());
                    if (function.isPresent()) {
                        this.context.push(function.get());
                        continue;
                    }
                }else{
                    this.context.pushError("Failed to parse function declaration.");
                }
            }
        }

        return this.context.build();
    }
}
