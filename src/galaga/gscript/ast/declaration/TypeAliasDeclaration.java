package galaga.gscript.ast.declaration;

import java.util.List;
import java.util.Optional;

import galaga.gscript.ast.types.Type;
import galaga.gscript.ast.types.TypeFunction;

public record TypeAliasDeclaration(String name, Optional<TypeFunction> function, List<Type> types) implements Declaration {
    
    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("type ").append(name).append(" = ");
        if(function.isPresent()) {
            sb.append(function.get().format());
            sb.append(";");
            return sb.toString();
        }
        
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
