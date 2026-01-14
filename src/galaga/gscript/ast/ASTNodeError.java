package galaga.gscript.ast;

public class ASTNodeError implements ASTNode {
    private final String message;

    public ASTNodeError(String message) {
        this.message = message;
    }

    @Override
    public String format() {
        return "/* ERROR: " + message + " */";
    }
    
}
