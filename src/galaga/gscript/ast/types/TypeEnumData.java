package galaga.gscript.ast.types;

import java.util.Map;
import java.util.Optional;

import galaga.gscript.ast.ASTNode;

public record TypeEnumData(Map<Type, String> data, Optional<Integer> value) implements ASTNode {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        if (data.size() > 0) {
            sb.append(" { ");
            for (var entry : data.entrySet()) {
                sb.append(entry.getKey().format());
                sb.append(" ");
                sb.append(entry.getValue());
                sb.append(";");
            }
            sb.append(" }");
        }

        if (value.isPresent()) {
            sb.append(" = ");
            sb.append(value.get());
        }
        return sb.toString();
    }

}
