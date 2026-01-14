package galaga.gscript.ast.declaration.module;

import java.util.Optional;

import galaga.gscript.ast.declaration.Declaration;
import galaga.gscript.ast.types.Type;
import galaga.gscript.ast.types.TypeFunction;

public record ExternDeclaration(Optional<Type> type, Optional<TypeFunction> function) implements Declaration {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("extern type ");
        if (type.isPresent()) {
            sb.append(type.get().format());
        } else if (function.isPresent()) {
            sb.append(function.get().format());
        }
        sb.append(";");
        return sb.toString();
    }

}
