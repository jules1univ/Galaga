package galaga.gscript.ast.types;

import galaga.gscript.ast.ASTNodeError;

public class TypeError extends ASTNodeError implements TypeBase{

    public TypeError(String message) {
        super(message);
    }

    @Override
    public String format() {
        return "/* TypeError: " + message + " */";
    }
    
}
