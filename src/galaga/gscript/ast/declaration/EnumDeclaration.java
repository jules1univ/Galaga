package galaga.gscript.ast.declaration;

import java.util.Map;

import galaga.gscript.ast.types.TypeEnumData;

public record EnumDeclaration(String name, Map<String, TypeEnumData> values) implements DeclarationBase {

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
            sb.append(entry.getValue().format());

            if (entry != values.entrySet().toArray()[values.size() - 1]) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("};");
        return sb.toString();
    }

}
