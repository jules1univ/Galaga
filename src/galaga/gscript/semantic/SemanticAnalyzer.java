package galaga.gscript.semantic;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.Program;
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

public class SemanticAnalyzer implements ASTVisitor<Void> {

    @Override
    public Void visitBinaryExpression(BinaryExpression node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitBinaryExpression'");
    }

    @Override
    public Void visitUnaryExpression(UnaryExpression node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitUnaryExpression'");
    }

    @Override
    public Void visitCallExpression(CallExpression node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitCallExpression'");
    }

    @Override
    public Void visitIndexExpression(IndexExpression node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitIndexExpression'");
    }

    @Override
    public Void visitIdentifierExpression(IdentifierExpression node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitIdentifierExpression'");
    }

    @Override
    public Void visitLiteralExpression(LiteralExpression node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitLiteralExpression'");
    }

    @Override
    public Void visitListExpression(ListExpression node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitListExpression'");
    }

    @Override
    public Void visitMapExpression(MapExpression node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitMapExpression'");
    }

    @Override
    public Void visitFunctionExpression(FunctionExpression node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitFunctionExpression'");
    }

    @Override
    public Void visitRangeExpression(RangeExpression node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitRangeExpression'");
    }

    @Override
    public Void visitBlockStatement(BlockStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitBlockStatement'");
    }

    @Override
    public Void visitIfStatement(IfStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitIfStatement'");
    }

    @Override
    public Void visitWhileStatement(WhileStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitWhileStatement'");
    }

    @Override
    public Void visitDoWhileStatement(DoWhileStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitDoWhileStatement'");
    }

    @Override
    public Void visitForStatement(ForStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitForStatement'");
    }

    @Override
    public Void visitReturnStatement(ReturnStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitReturnStatement'");
    }

    @Override
    public Void visitBreakStatement(BreakStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitBreakStatement'");
    }

    @Override
    public Void visitContinueStatement(ContinueStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitContinueStatement'");
    }

    @Override
    public Void visitAssignmentStatement(AssignmentStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitAssignmentStatement'");
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitExpressionStatement'");
    }

    @Override
    public Void visitFunctionDeclaration(FunctionDeclaration node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitFunctionDeclaration'");
    }

    @Override
    public Void visitVariableDeclaration(VariableDeclaration node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitVariableDeclaration'");
    }

    @Override
    public Void visitNativeFunctionDeclaration(NativeFunctionDeclaration node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNativeFunctionDeclaration'");
    }

    @Override
    public Void visitProgram(Program node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitProgram'");
    }
    
}
