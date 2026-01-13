package galaga.gscript.parser.statement;

import java.util.Map;

import galaga.gscript.parser.TypeSignature;

public final class StructDeclaration extends Statement {

    private final String name;
    private final Map<TypeSignature, String> fields;

    public StructDeclaration(String name, Map<TypeSignature, String> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return this.name;
    }

    public Map<TypeSignature, String> getFields() {
        return this.fields;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("type ");
        sb.append(this.name);
        sb.append(" = ");
        sb.append("struct ");
        sb.append(" {\n");
        for (Map.Entry<TypeSignature, String> entry : this.fields.entrySet()) {
            sb.append(entry.getKey().format());
            sb.append(" ");
            sb.append(entry.getValue());
            sb.append(";\n");
        }
        sb.append("};\n");
        return sb.toString();
    }

}
