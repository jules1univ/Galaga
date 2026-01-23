package galaga.gscript.ast.statement;

import galaga.gscript.ast.statement.logic.IfStatement;
import galaga.gscript.ast.statement.logic.loop.BreakStatement;
import galaga.gscript.ast.statement.logic.loop.ContinueStatement;
import galaga.gscript.ast.statement.logic.loop.DoWhileStatement;
import galaga.gscript.ast.statement.logic.loop.ForStatement;
import galaga.gscript.ast.statement.logic.loop.WhileStatement;

public interface StatementVisitor<T> {

    T visitBlockStatement(BlockStatement node);

    T visitIfStatement(IfStatement node);

    T visitWhileStatement(WhileStatement node);

    T visitDoWhileStatement(DoWhileStatement node);

    T visitForStatement(ForStatement node);

    T visitReturnStatement(ReturnStatement node);

    T visitBreakStatement(BreakStatement node);

    T visitContinueStatement(ContinueStatement node);

    T visitAssignmentStatement(AssignmentStatement node);

    T visitExpressionStatement(ExpressionStatement node);
}
