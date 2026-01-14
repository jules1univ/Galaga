package galaga.gscript.ast;

public record ASTNodeError(String message) implements ASTNode {
    
    @Override
    public String format() {
        return "/* ERROR: " + message + " */";
    }
    
}
