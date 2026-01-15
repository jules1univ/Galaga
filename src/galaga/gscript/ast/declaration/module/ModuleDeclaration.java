package galaga.gscript.ast.declaration.module;

import galaga.gscript.ast.declaration.DeclarationBase;

public record ModuleDeclaration(String name) implements DeclarationBase {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("module ");
        sb.append(name);
        sb.append(";");
        return sb.toString();
    }

}
