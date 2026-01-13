package galaga.gscript.parser.statement;

import java.util.Map;
import java.util.Optional;

public class EnumDeclaration extends Statement {

    private final String name;
    private final Map<String, Optional<Integer>> values;

    public EnumDeclaration(String name, Map<String, Optional<Integer>> values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public Map<String, Optional<Integer>> getValues() {
        return values;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("type ");
        sb.append(this.name);
        sb.append(" = ");
        sb.append("enum ");
        sb.append(" {\n");
        for (Map.Entry<String, Optional<Integer>> entry : this.values.entrySet()) {
            sb.append("    ");
            sb.append(entry.getKey());
            if (entry.getValue().isPresent()) {
                sb.append(" = ");
                sb.append(entry.getValue().get());
            }
            sb.append(",\n");
        }
        sb.append("};\n");
        return sb.toString();
    }

}
