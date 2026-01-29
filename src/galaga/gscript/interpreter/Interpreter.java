package galaga.gscript.interpreter;

import java.util.List;
import java.util.Optional;
import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.Program;
import galaga.gscript.ast.declaration.Declaration;
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
import galaga.gscript.interpreter.subinterpreter.DeclarationInterpreter;
import galaga.gscript.interpreter.subinterpreter.ExpressionInterpreter;
import galaga.gscript.interpreter.subinterpreter.StatementInterpreter;
import galaga.gscript.runtime.values.FunctionValue;
import galaga.gscript.runtime.values.Value;

public final class Interpreter implements ASTVisitor<Value> {
    private final InterpreterContext context;
    private final ExpressionInterpreter expr;
    private final StatementInterpreter stmt;
    private final DeclarationInterpreter decl;

    public Interpreter() {
        super();
        this.context = new InterpreterContext(this);
        this.decl = new DeclarationInterpreter(this.context);
        this.stmt = new StatementInterpreter(this.context);
        this.expr = new ExpressionInterpreter(this.context);
    }

    public void run(Program program) {
        program.accept(this);
    }

    public InterpreterContext getContext() {
        return this.context;
    }

    public Optional<Value> callFunction(String name) {
        return callFunction(name, new Value[] {});
    }

    public Optional<Value> callFunction(String name, Value... args) {
        Optional<Value> functionValueOpt = this.context.getGlobalScope().getVariable(name);
        if (functionValueOpt.isPresent() && functionValueOpt.get() instanceof FunctionValue function) {
            return this.context.scope((Void v) -> {

                int provided = args == null ? 0 : args.length;
                if (function.parameters().size() != provided) {
                    throw new RuntimeException("Function '" + name + "' expects " + function.parameters().size()
                            + " arguments, but got " + provided + ".");
                }

                List<String> parameters = function.parameters();
                for (int i = 0; i < parameters.size(); i++) {
                    String paramName = parameters.get(i);
                    Value argValue = args[i];
                    this.context.getScope().setVariable(paramName, argValue);
                }
                return this.context.function((Void vv) -> {
                    Value retValue = function.body().accept(this);
                    if (retValue != null) {
                        return Optional.of(retValue);
                    } else {
                        return Optional.empty();
                    }
                });
            });

        }

        return Optional.empty();

    }

    public Optional<Value> getVariable(String name) {
        return this.context.getGlobalScope().getVariable(name);
    }

    @Override
    public Value visitProgram(Program node) {
        for (Declaration declaration : node.declarations()) {
            declaration.accept(this);
        }
        return null;
    }

    @Override
    public Value visitFunctionDeclaration(FunctionDeclaration node) {
        return this.decl.visitFunctionDeclaration(node);
    }

    @Override
    public Value visitVariableDeclaration(VariableDeclaration node) {
        return this.decl.visitVariableDeclaration(node);
    }

    @Override
    public Value visitNativeFunctionDeclaration(NativeFunctionDeclaration node) {
        return this.decl.visitNativeFunctionDeclaration(node);
    }

    @Override
    public Value visitBinaryExpression(BinaryExpression node) {
        return this.expr.visitBinaryExpression(node);
    }

    @Override
    public Value visitUnaryExpression(UnaryExpression node) {
        return this.expr.visitUnaryExpression(node);
    }

    @Override
    public Value visitCallExpression(CallExpression node) {
        return this.expr.visitCallExpression(node);
    }

    @Override
    public Value visitIndexExpression(IndexExpression node) {
        return this.expr.visitIndexExpression(node);
    }

    @Override
    public Value visitIdentifierExpression(IdentifierExpression node) {
        return this.expr.visitIdentifierExpression(node);
    }

    @Override
    public Value visitLiteralExpression(LiteralExpression node) {
        return this.expr.visitLiteralExpression(node);
    }

    @Override
    public Value visitListExpression(ListExpression node) {
        return this.expr.visitListExpression(node);
    }

    @Override
    public Value visitMapExpression(MapExpression node) {
        return this.expr.visitMapExpression(node);
    }

    @Override
    public Value visitFunctionExpression(FunctionExpression node) {
        return this.expr.visitFunctionExpression(node);
    }

    @Override
    public Value visitRangeExpression(RangeExpression node) {
        return this.expr.visitRangeExpression(node);
    }

    @Override
    public Value visitBlockStatement(BlockStatement node) {
        return this.stmt.visitBlockStatement(node);
    }

    @Override
    public Value visitIfStatement(IfStatement node) {
        return this.stmt.visitIfStatement(node);
    }

    @Override
    public Value visitWhileStatement(WhileStatement node) {
        return this.stmt.visitWhileStatement(node);
    }

    @Override
    public Value visitDoWhileStatement(DoWhileStatement node) {
        return this.stmt.visitDoWhileStatement(node);
    }

    @Override
    public Value visitForStatement(ForStatement node) {
        return this.stmt.visitForStatement(node);
    }

    @Override
    public Value visitReturnStatement(ReturnStatement node) {
        return this.stmt.visitReturnStatement(node);
    }

    @Override
    public Value visitBreakStatement(BreakStatement node) {
        return this.stmt.visitBreakStatement(node);
    }

    @Override
    public Value visitContinueStatement(ContinueStatement node) {
        return this.stmt.visitContinueStatement(node);
    }

    @Override
    public Value visitAssignmentStatement(AssignmentStatement node) {
        return this.stmt.visitAssignmentStatement(node);
    }

    @Override
    public Value visitExpressionStatement(ExpressionStatement node) {
        return this.stmt.visitExpressionStatement(node);
    }

}
