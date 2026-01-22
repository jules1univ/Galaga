package galaga.gscript.ast;

public abstract class ASTDepthVisitor<T> implements ASTVisitor<T> {
    protected int depth = 0;

    protected final void depth(Runnable action) {
        this.preDepth();
        action.run();
        this.postDepth();
    }

    protected abstract T getDepth();

    protected void preDepth() {
        this.depth++;
    }

    protected void postDepth() {
        this.depth--;
    }

}
