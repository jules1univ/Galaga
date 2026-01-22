package galaga.gscript.ast.declaration;

import java.util.List;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.statement.BlockStatement;

public record FunctionDeclaration(String name, List<String> parameters, BlockStatement body) implements Declaration {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitFunctionDeclaration(this);
    }
}
