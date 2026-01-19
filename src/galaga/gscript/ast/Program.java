package galaga.gscript.ast;

import java.util.List;
import java.util.Map;

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

        for (ModuleDeclaration module : modules) {
            sb.append(module.format());
            sb.append("\n");
        }
        sb.append("\n");

        for (ImportDeclaration importDecl : imports) {
            sb.append(importDecl.format());
            sb.append("\n");
        }
        sb.append("\n");

        for (NativeDeclaration nativeDecl : natives) {
            sb.append(nativeDecl.format());
            sb.append("\n");
        }
        sb.append("\n");

        for (TypeAliasDeclaration typeAlias : typeAliases.values()) {
            sb.append(typeAlias.format());
            sb.append("\n");
        }
        sb.append("\n");

        for (StructDeclaration struct : structs.values()) {
            sb.append(struct.format());
            sb.append("\n");
        }
        sb.append("\n");

        for (EnumDeclaration enumDecl : enums.values()) {
            sb.append(enumDecl.format());
            sb.append("\n");
        }

        sb.append("\n");

        for (FunctionDeclaration function : functions.values()) {
            sb.append(function.format());
            sb.append("\n");
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }
}
