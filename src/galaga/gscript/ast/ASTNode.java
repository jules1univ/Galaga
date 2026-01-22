package galaga.gscript.ast;

public interface ASTNode {
    <T> T accept(ASTVisitor<T> visitor);
}