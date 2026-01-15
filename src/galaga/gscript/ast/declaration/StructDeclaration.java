package galaga.gscript.ast.declaration;

import java.util.Map;

import galaga.gscript.ast.types.TypeBase;


public record StructDeclaration(String name, Map<TypeBase, String> fields) implements DeclarationBase {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("type ");
        sb.append(name);
        sb.append(" = ");
        sb.append("struct ");
        sb.append(" {\n");
        for (Map.Entry<TypeBase, String> field : fields.entrySet()) {
            sb.append("    ");
            sb.append(field.getKey().format());
            sb.append(" ");
            sb.append(field.getValue());
            sb.append(";\n");
        }
        sb.append("}");
        return sb.toString();
    }

}
