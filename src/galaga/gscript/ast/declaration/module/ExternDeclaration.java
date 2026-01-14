package galaga.gscript.ast.declaration.module;

import galaga.gscript.ast.declaration.Declaration;
import galaga.gscript.ast.types.Type;
import galaga.gscript.ast.types.TypeFunction;

public record ExternDeclaration(Type type, TypeFunction function) implements Declaration{

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("extern type ");
        if (type != null) {
            sb.append(type.format());
        }else{
            sb.append(function.format());
        }
        sb.append(";");
        return sb.toString();
    }
    
}
