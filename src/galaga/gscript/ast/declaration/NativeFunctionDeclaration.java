package galaga.gscript.ast.declaration;

import java.util.List;

import galaga.gscript.ast.ASTVisitor;

public record NativeFunctionDeclaration(String name, List<String> parameters) implements Declaration {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNativeFunctionDeclaration(this);
    }
}

