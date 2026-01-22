package galaga.gscript.formatter;

import java.util.function.Supplier;

import galaga.gscript.ast.ASTNode;
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
import galaga.gscript.ast.expression.operator.AssignmentExpression;
import galaga.gscript.ast.expression.operator.BinaryExpression;
import galaga.gscript.ast.expression.operator.UnaryExpression;
import galaga.gscript.ast.statement.BlockStatement;
import galaga.gscript.ast.statement.ExpressionStatement;
import galaga.gscript.ast.statement.ReturnStatement;
import galaga.gscript.ast.statement.logic.IfStatement;
import galaga.gscript.ast.statement.logic.loop.BreakStatement;
import galaga.gscript.ast.statement.logic.loop.ContinueStatement;
import galaga.gscript.ast.statement.logic.loop.DoWhileStatement;
import galaga.gscript.ast.statement.logic.loop.ForStatement;
import galaga.gscript.ast.statement.logic.loop.WhileStatement;

public final class Formatter implements ASTVisitor<String> {
    private int indentLevel = 0;

    private String indent() {
        return "  ".repeat(indentLevel);
    }

    private void indent(Supplier<Void> action) {
        indentLevel++;
        action.get();
        indentLevel--;
    }

    @Override
    public String visitProgram(Program node) {
        StringBuilder sb = new StringBuilder();
        Declaration lastDeclaration = null;
        for (Declaration declaration : node.declarations()) {
            sb.append(declaration.accept(this));

            if (lastDeclaration != null && !lastDeclaration.getClass().equals(declaration.getClass())) {
                sb.append("\n");
            }
            lastDeclaration = declaration;
        }
        return sb.toString();
    }

    @Override
    public String visitFunctionDeclaration(FunctionDeclaration node) {
        StringBuilder sb = new StringBuilder();
        sb.append("fn ").append(node.name()).append("(");
        sb.append(String.join(", ", node.parameters()));
        sb.append(") {\n");
        indent(() -> {
            sb.append(node.body().accept(this));
            return null;
        });
        sb.append(indent()).append("}\n");
        return sb.toString();
    }

    @Override
    public String visitVariableDeclaration(VariableDeclaration node) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.isConstant() ? "const " : "let ");
        sb.append(node.name()).append(" = ");
        sb.append(node.value().accept(this)).append(";\n");
        return sb.toString();
    }

    @Override
    public String visitNativeFunctionDeclaration(NativeFunctionDeclaration node) {
        StringBuilder sb = new StringBuilder();
        sb.append("native ").append(node.name()).append("(");
        sb.append(String.join(", ", node.parameters()));
        sb.append(");\n");
        return sb.toString();
    }

    @Override
    public String visitBlockStatement(BlockStatement node) {
        StringBuilder sb = new StringBuilder();
        for (ASTNode statement : node.statements()) {
            sb.append(indent()).append(statement.accept(this));
        }
        return sb.toString();
    }

    @Override
    public String visitIfStatement(IfStatement node) {
        StringBuilder sb = new StringBuilder();
        sb.append("if (").append(node.condition().accept(this)).append(") {\n");
        indent(() -> {
            sb.append(node.thenBranch().accept(this));
            return null;
        });
        sb.append(indent()).append("}");
        if (node.elseBranch().isPresent()) {
            sb.append(" else {\n");
            indent(() -> {
                sb.append(node.elseBranch().get().accept(this));
                return null;
            });
            sb.append(indent()).append("}");
        }
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public String visitWhileStatement(WhileStatement node) {
        StringBuilder sb = new StringBuilder();
        sb.append("while (").append(node.condition().accept(this)).append(") {\n");
        indent(() -> {
            sb.append(node.body().accept(this));
            return null;
        });
        sb.append(indent()).append("}\n");
        return sb.toString();
    }

    @Override
    public String visitDoWhileStatement(DoWhileStatement node) {
        StringBuilder sb = new StringBuilder();
        sb.append("do {\n");
        indent(() -> {
            sb.append(node.body().accept(this));
            return null;
        });
        sb.append(indent()).append("} while (").append(node.condition().accept(this)).append(");\n");
        return sb.toString();
    }

    @Override
    public String visitForStatement(ForStatement node) {
        StringBuilder sb = new StringBuilder();
        sb.append("for (");
        sb.append(node.variable()).append(" in ").append(node.iterable().accept(this)).append(") {\n");
        indent(() -> {
            sb.append(node.body().accept(this));
            return null;
        });
        sb.append(indent()).append("}\n");
        return sb.toString();
    }

    @Override
    public String visitReturnStatement(ReturnStatement node) {
        StringBuilder sb = new StringBuilder();
        sb.append("return");
        if (node.value().isPresent()) {
            sb.append(" ").append(node.value().get().accept(this));
        }
        sb.append(";\n");
        return sb.toString();
    }

    @Override
    public String visitBreakStatement(BreakStatement node) {
        return "break;\n";
    }

    @Override
    public String visitContinueStatement(ContinueStatement node) {
        return "continue;\n";
    }

    @Override
    public String visitExpressionStatement(ExpressionStatement node) {
        return node.expression().accept(this) + ";\n";
    }

    @Override
    public String visitBinaryExpression(BinaryExpression node) {
        return node.left().accept(this) + " " + node.operator() + " " + node.right().accept(this);
    }

    @Override
    public String visitUnaryExpression(UnaryExpression node) {
        if (node.isPrefix()) {
            return node.operator().getText() + node.operand().accept(this);
        } else {
            return node.operand().accept(this) + node.operator().getText();
        }
    }

    @Override
    public String visitAssignmentExpression(AssignmentExpression node) {
        return node.target().accept(this) + " = " + node.value().accept(this);
    }

    @Override
    public String visitCallExpression(CallExpression node) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.callee().accept(this)).append("(");
        sb.append(String.join(", ", node.arguments().stream().map(arg -> arg.accept(this)).toList()));
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String visitIndexExpression(IndexExpression node) {
        return node.target().accept(this) + "[" + node.index().accept(this) + "]";
    }

    @Override
    public String visitIdentifierExpression(IdentifierExpression node) {
        return node.name();
    }

    @Override
    public String visitLiteralExpression(LiteralExpression node) {
        return node.value().toString();
    }

    @Override
    public String visitListExpression(ListExpression node) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(String.join(", ", node.elements().stream().map(element -> element.accept(this)).toList()));
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String visitMapExpression(MapExpression node) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(node.key().accept(this)).append(": ").append(node.value().accept(this));
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String visitFunctionExpression(FunctionExpression node) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(String.join(", ", node.parameters()));
        sb.append(") => {\n");
        indent(() -> {
            sb.append(node.body().accept(this));
            return null;
        });
        sb.append(indent()).append("}");
        return sb.toString();
    }

    @Override
    public String visitRangeExpression(RangeExpression node) {
        return node.start().accept(this) + ".." + node.end().accept(this);
    }
}