package galaga.gscript.ast.types;

import java.util.Map;
import java.util.Optional;

public record TypeFunction(
        Type returnType,
        String name,
        Map<Type, String> parameters,
        Optional<Type> extendsType) implements TypeBase {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(returnType.format());
        sb.append(" ");
        sb.append(name);
        sb.append("(");
        boolean first = true;
        for (Map.Entry<Type, String> param : parameters.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(param.getKey().format());
            sb.append(" ");
            sb.append(param.getValue());
            first = false;
        }
        sb.append(")");
        if (extendsType.isPresent()) {
            sb.append(" extends ");
            sb.append(extendsType.get().format());
        }
        return sb.toString();
    }

}
