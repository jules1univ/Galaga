package galaga.gscript.ast.declaration;

import java.util.Map;
import java.util.Optional;

public record EnumDeclaration(String name, Map<String, Optional<Integer>> values) implements Declaration {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("type ");
        sb.append(name);
        sb.append(" = ");
        sb.append("enum {\n");
        for (var entry : values.entrySet()) {
            sb.append("    ");
            sb.append(entry.getKey());
            if (entry.getValue().isPresent()) {
                sb.append(" = ");
                sb.append(entry.getValue().get());
            }
            sb.append(",\n");
        }
        sb.append("};");
        return sb.toString();
    }
    
}
