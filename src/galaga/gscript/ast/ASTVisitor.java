package galaga.gscript.ast;

import galaga.gscript.ast.declaration.FunctionDeclaration;
import galaga.gscript.ast.declaration.NativeFunctionDeclaration;
import galaga.gscript.ast.declaration.VariableDeclaration;
import galaga.gscript.ast.expression.IdentifierExpression;
import galaga.gscript.ast.expression.LiteralExpression;
import galaga.gscript.ast.expression.collection.IndexExpression;
import galaga.gscript.ast.expression.collection.ListExpression;
import galaga.gscript.ast.expression.collection.MapExpression;
import galaga.gscript.ast.expression.collection.RangeExpression;
import galaga.gscript.ast.expression.function.CallExpression;
import galaga.gscript.ast.expression.function.FunctionExpression;
import galaga.gscript.ast.expression.operator.BinaryExpression;
import galaga.gscript.ast.expression.operator.UnaryExpression;
import galaga.gscript.ast.statement.AssignmentStatement;
import galaga.gscript.ast.statement.BlockStatement;
import galaga.gscript.ast.statement.ExpressionStatement;
import galaga.gscript.ast.statement.ReturnStatement;
import galaga.gscript.ast.statement.logic.IfStatement;
import galaga.gscript.ast.statement.logic.loop.BreakStatement;
import galaga.gscript.ast.statement.logic.loop.ContinueStatement;
import galaga.gscript.ast.statement.logic.loop.DoWhileStatement;
import galaga.gscript.ast.statement.logic.loop.ForStatement;
import galaga.gscript.ast.statement.logic.loop.WhileStatement;

public interface ASTVisitor<T> {
    T visitProgram(Program node);

    T visitFunctionDeclaration(FunctionDeclaration node);

    T visitVariableDeclaration(VariableDeclaration node);

    T visitNativeFunctionDeclaration(NativeFunctionDeclaration node);

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

    T visitBinaryExpression(BinaryExpression node);

    T visitUnaryExpression(UnaryExpression node);

    T visitCallExpression(CallExpression node);

    T visitIndexExpression(IndexExpression node);

    T visitIdentifierExpression(IdentifierExpression node);

    T visitLiteralExpression(LiteralExpression node);

    T visitListExpression(ListExpression node);

    T visitMapExpression(MapExpression node);

    T visitFunctionExpression(FunctionExpression node);

    T visitRangeExpression(RangeExpression node);
}
