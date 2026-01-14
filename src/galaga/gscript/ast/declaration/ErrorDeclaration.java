package galaga.gscript.ast.declaration;

import galaga.gscript.ast.ASTNodeError;

public class ErrorDeclaration extends ASTNodeError implements Declaration {
    public ErrorDeclaration(String message) {
        super(message);
    }
}