package galaga.gscript.ast.declaration.module;

import java.util.LinkedList;

import galaga.gscript.ast.declaration.DeclarationBase;

public record ModuleDeclaration(LinkedList<String>  path) implements DeclarationBase {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("module ");
        sb.append(String.join(".", this.path));
        sb.append(";");
        return sb.toString();
    }

}
