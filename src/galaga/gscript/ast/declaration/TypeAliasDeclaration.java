package galaga.gscript.ast.declaration;

import java.util.List;

import galaga.gscript.ast.types.Type;

public record TypeAliasDeclaration(String name, List<Type> types) implements Declaration {
    
    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("type ").append(name).append(" = ");
        for (int i = 0; i < types.size(); i++) {
            sb.append(types.get(i).format());
            if (i < types.size() - 1) {
                sb.append(" | ");
            }
        }
        sb.append(";");
        return sb.toString();
    }
    
}
