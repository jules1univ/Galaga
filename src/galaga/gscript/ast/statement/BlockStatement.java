package galaga.gscript.ast.statement;

import java.util.List;

import galaga.gscript.ast.ASTVisitor;

public record BlockStatement(List<Statement> statements) implements Statement {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitBlockStatement(this);
    }
}