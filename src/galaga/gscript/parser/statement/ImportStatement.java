package galaga.gscript.parser.statement;

import java.util.List;

public final class ImportStatement extends Statement {
    private final List<String> paths;
    private final List<String> functions;

    private final boolean isWildcard;

    public ImportStatement(List<String> paths, List<String> functions, boolean isWildcard) {
        super();
        this.paths = paths;
        this.functions = functions;
        this.isWildcard = isWildcard;
    }

    public List<String> getPaths() {
        return paths;
    }

    public List<String> getFunctions() {
        return functions;
    }

    public boolean isWildcard() {
        return isWildcard;
    }
    
    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("import ");
        for (int i = 0; i < paths.size(); i++) {
            sb.append(paths.get(i));
            if (i < paths.size() - 1) {
                sb.append(".");
            }
        }
        if (isWildcard) {
            sb.append(".*");
        } else if (!functions.isEmpty()) {
            sb.append(" { ");
            for (int i = 0; i < functions.size(); i++) {
                sb.append(functions.get(i));
                if (i < functions.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(" }");
        }
        sb.append(";\n");
        return sb.toString();
    }
}
