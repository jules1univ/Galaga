package galaga.gscript.ast.statement;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.lexer.rules.Operator;

public record AssignmentStatement(String name, Operator operator, Expression value, boolean isConstant) implements Statement {
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitAssignmentStatement(this);
    }
    
}
