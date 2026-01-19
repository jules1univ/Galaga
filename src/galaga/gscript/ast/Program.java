package galaga.gscript.ast;

import java.util.List;
import java.util.Map;

import galaga.gscript.ast.declaration.DeclarationBase;
import galaga.gscript.ast.declaration.EnumDeclaration;
import galaga.gscript.ast.declaration.FunctionDeclaration;
import galaga.gscript.ast.declaration.StructDeclaration;
import galaga.gscript.ast.declaration.TypeAliasDeclaration;
import galaga.gscript.ast.declaration.module.ImportDeclaration;
import galaga.gscript.ast.declaration.module.ModuleDeclaration;
import galaga.gscript.ast.declaration.module.NativeDeclaration;

public record Program(
        List<ModuleDeclaration> modules,
        List<ImportDeclaration> imports,
        List<NativeDeclaration> natives,
        Map<String, FunctionDeclaration> functions,
        Map<String, StructDeclaration> structs,
        Map<String, EnumDeclaration> enums,
        Map<String, TypeAliasDeclaration> typeAliases)
        implements ASTNode {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        DeclarationBase lastDecl = null;

        for (ModuleDeclaration module : modules) {
            if (lastDecl != null) {
                sb.append("\n");
            }
            sb.append(module.format());
            sb.append("\n");
            lastDecl = module;
        }

        for (ImportDeclaration importDecl : imports) {
            if (lastDecl != null) {
                sb.append("\n");
            }
            sb.append(importDecl.format());
            sb.append("\n");
            lastDecl = importDecl;
        }

        for (NativeDeclaration nativeDecl : natives) {
            if (lastDecl != null) {
                sb.append("\n");
            }
            sb.append(nativeDecl.format());
            sb.append("\n");
            lastDecl = nativeDecl;
        }

        for (TypeAliasDeclaration typeAlias : typeAliases.values()) {
            if (lastDecl != null) {
                sb.append("\n");
            }
            sb.append(typeAlias.format());
            sb.append("\n");
            lastDecl = typeAlias;
        }

        for (StructDeclaration struct : structs.values()) {
            if (lastDecl != null) {
                sb.append("\n");
            }
            sb.append(struct.format());
            sb.append("\n");
            lastDecl = struct;
        }

        for (EnumDeclaration enumDecl : enums.values()) {
            if (lastDecl != null) {
                sb.append("\n");
            }
            sb.append(enumDecl.format());
            sb.append("\n");
            lastDecl = enumDecl;
        }

        for (FunctionDeclaration function : functions.values()) {
            if (lastDecl != null) {
                sb.append("\n");
            }
            sb.append(function.format());
            sb.append("\n");
            lastDecl = function;
        }

        return sb.toString();
    }
}
