package galaga.gscript.ast.declaration.module;

import java.util.LinkedList;
import java.util.List;

import galaga.gscript.ast.declaration.DeclarationBase;

public record ImportDeclaration(LinkedList<String> importPaths, List<String> importObjects, boolean isWildCard)
        implements DeclarationBase {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("import ");
        sb.append(String.join(".", importPaths));
        if (isWildCard) {
            sb.append(".*");
        } else if (!importObjects.isEmpty()) {
            sb.append(".{");
            sb.append(String.join(", ", importObjects));
            sb.append("}");
        }
        sb.append(";");
        return sb.toString();
    }

}
