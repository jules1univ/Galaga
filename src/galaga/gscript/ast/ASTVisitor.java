package galaga.gscript.ast;

import galaga.gscript.ast.declaration.DeclarationVisitor;
import galaga.gscript.ast.expression.ExpressionVisitor;
import galaga.gscript.ast.statement.StatementVisitor;

public interface ASTVisitor<T> extends ExpressionVisitor<T>, StatementVisitor<T>, DeclarationVisitor<T> {
    T visitProgram(Program node);
}
