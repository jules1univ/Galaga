package galaga.gscript.ast.declaration;

import galaga.gscript.ast.ASTNodeError;

public class DeclarationError extends ASTNodeError implements DeclarationBase {

    public DeclarationError(String message) {
        super(message);
    }

    @Override
    public String format() {
        return "/* ErrorDeclaration: " + message + " */";
    }

}
