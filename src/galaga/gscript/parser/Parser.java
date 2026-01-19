package galaga.gscript.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import galaga.gscript.ast.Program;
import galaga.gscript.ast.declaration.EnumDeclaration;
import galaga.gscript.ast.declaration.FunctionDeclaration;
import galaga.gscript.ast.declaration.StructDeclaration;
import galaga.gscript.ast.declaration.TypeAliasDeclaration;
import galaga.gscript.ast.declaration.module.ImportDeclaration;
import galaga.gscript.ast.declaration.module.ModuleDeclaration;
import galaga.gscript.ast.declaration.module.NativeDeclaration;
import galaga.gscript.ast.types.TypeFunction;
import galaga.gscript.lexer.Lexer;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.parser.subparsers.DeclarationParser;
import galaga.gscript.parser.subparsers.ModuleParser;
import galaga.gscript.parser.subparsers.TypeParser;

public final class Parser {
    private final List<ModuleDeclaration> modules = new ArrayList<>();
    private final List<ImportDeclaration> imports = new ArrayList<>();
    private final List<NativeDeclaration> natives = new ArrayList<>();

    private final Map<String, FunctionDeclaration> functions = new HashMap<>();
    private final Map<String, StructDeclaration> structs = new HashMap<>();
    private final Map<String, EnumDeclaration> enums = new HashMap<>();
    private final Map<String, TypeAliasDeclaration> typeAliases = new HashMap<>();

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

    private void parseModuleDeclarations() throws ParserException {
        if (ModuleParser.isModuleDeclaration(this.context)) {
            this.modules.add(ModuleParser.parseModule(this.context));
            return;
        } else if (ModuleParser.isImportDeclaration(this.context)) {
            this.imports.add(ModuleParser.parseImport(this.context));
            return;
        } else if (ModuleParser.isNativeDeclaration(this.context)) {
            this.natives.add(ModuleParser.parseNative(this.context));
            return;
        }
    }

    private void parseTypeDeclarations() throws ParserException {
        if (!DeclarationParser.isTypeDeclaration(this.context)) {
            return;
        }
        String typeName = DeclarationParser.parseTypeDeclaration(this.context);
        if (DeclarationParser.isEnumDeclaration(this.context)) {
            EnumDeclaration enumDecl = DeclarationParser.parseEnum(this.context, typeName);
            this.enums.put(enumDecl.name(), enumDecl);
        } else if (DeclarationParser.isStructDeclaration(this.context)) {
            StructDeclaration structDecl = DeclarationParser.parseStruct(this.context, typeName);
            this.structs.put(structDecl.name(), structDecl);
        } else {
            TypeAliasDeclaration typeAliasDecl = DeclarationParser.parseTypeAlias(this.context, typeName);
            this.typeAliases.put(typeAliasDecl.name(), typeAliasDecl);
        }
    }

    private void parseFunctionDeclaration() throws ParserException {
        if (!TypeParser.isType(this.context)) {
            return;
        }
        TypeFunction signature = TypeParser.parseTypeFunction(this.context);
        if (!DeclarationParser.isFunctionDeclaration(this.context)) {
            return;
        }

        FunctionDeclaration functionDecl = DeclarationParser.parseFunction(this.context, signature);
        this.functions.put(signature.name(), functionDecl);
    }

    public Program parse() throws ParserException {
        int lastIndex = -1;
        while (!this.context.isEnd()) {
            if (this.context.getIndex() == lastIndex) {
                throw new ParserException("Parser is stuck at index " + this.context.getIndex());
            } else {
                lastIndex = this.context.getIndex();
            }

            this.parseModuleDeclarations();
            this.parseTypeDeclarations();
            this.parseFunctionDeclaration();

        }

        return new Program(
                this.modules,
                this.imports,
                this.natives,
                this.functions,
                this.structs,
                this.enums,
                this.typeAliases
        );
    }
}
