package galaga.gscript.parser;

import java.util.Map;

public final class FunctionSignature extends ParserObject {

    private final String name;
    private final String type;
    private final Map<TypeSignature, String> parameters;

    public FunctionSignature(String name, String type, Map<TypeSignature, String> parameters) {
        this.name = name;
        this.type = type;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return type;
    }

    public Map<TypeSignature, String> getParameters() {
        return parameters;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        sb.append(" ");
        sb.append(name);
        sb.append("(");

        if (!this.parameters.isEmpty()) {

            for (Map.Entry<TypeSignature, String> entry : this.parameters.entrySet()) {
                sb.append(entry.getKey().format());
                sb.append(" ");
                sb.append(entry.getValue());
                sb.append(", ");
            }
        }

        sb.append(")");
        sb.append(";\n");
        return sb.toString();
    }

}
