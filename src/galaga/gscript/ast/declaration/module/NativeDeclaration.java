package galaga.gscript.ast.declaration.module;

import galaga.gscript.ast.declaration.DeclarationBase;
import galaga.gscript.ast.types.TypeBase;

public record NativeDeclaration(TypeBase type) implements DeclarationBase {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("native type ");
        sb.append(type.format());
        sb.append(";");
        return sb.toString();
    }

}
