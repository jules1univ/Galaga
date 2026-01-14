package galaga.gscript.ast.declaration.module;

import java.util.LinkedList;
import java.util.List;

import galaga.gscript.ast.declaration.Declaration;

public record ImportDeclaration(LinkedList<String> importPaths, List<String> importFunctions, boolean isWildCard)
        implements Declaration {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("import ");
        sb.append(String.join(".", importPaths));
        if (isWildCard) {
            sb.append(".*");
        } else if (!importFunctions.isEmpty()) {
            sb.append(" { ");
            sb.append(String.join(", ", importFunctions));
            sb.append(" }");
        }
        sb.append(";");
        return sb.toString();
    }

}
