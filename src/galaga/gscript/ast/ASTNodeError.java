package galaga.gscript.ast;

public abstract class ASTNodeError implements ASTNode {
    protected final String message;

    public ASTNodeError(String message) {
        this.message = message;
    }
}
