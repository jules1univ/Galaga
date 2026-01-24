package galaga.gscript.interpreter.subinterpreter;

import galaga.gscript.ast.expression.IdentifierExpression;
import galaga.gscript.ast.expression.operator.BinaryExpression;
import galaga.gscript.ast.statement.AssignmentStatement;
import galaga.gscript.ast.statement.BlockStatement;
import galaga.gscript.ast.statement.ExpressionStatement;
import galaga.gscript.ast.statement.ReturnStatement;
import galaga.gscript.ast.statement.Statement;
import galaga.gscript.ast.statement.StatementVisitor;
import galaga.gscript.ast.statement.logic.IfStatement;
import galaga.gscript.ast.statement.logic.loop.BreakStatement;
import galaga.gscript.ast.statement.logic.loop.ContinueStatement;
import galaga.gscript.ast.statement.logic.loop.DoWhileStatement;
import galaga.gscript.ast.statement.logic.loop.ForStatement;
import galaga.gscript.ast.statement.logic.loop.WhileStatement;
import galaga.gscript.interpreter.InterpreterContext;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.rules.OperatorPriority;
import galaga.gscript.types.values.BooleanValue;
import galaga.gscript.types.values.ListValue;
import galaga.gscript.types.values.NullValue;
import galaga.gscript.types.values.Value;

public class StatementInterpreter implements StatementVisitor<Value> {
    private final InterpreterContext context;

    public StatementInterpreter(InterpreterContext context) {
        this.context = context;
    }

    @Override
    public Value visitBlockStatement(BlockStatement node) {
        for (Statement statement : node.statements()) {
            statement.accept(this.context.getInterpreter());

            if (this.context.isFunctionContext()) {
                if (this.context.hasFunctionReturn()) {
                    return this.context.getFunctionReturn();
                }
            }

            if (this.context.isLoopContext()) {
                if (this.context.isLoopBreakRequested() || this.context.isLoopSkip()) {
                    break;
                }
            }
        }
        return null;
    }

    @Override
    public Value visitIfStatement(IfStatement node) {
        if (node.condition().accept(this.context.getInterpreter()) instanceof BooleanValue boolVal) {
            if (boolVal.value()) {
                this.context.scope(() -> {
                    node.thenBranch().accept(this.context.getInterpreter());
                });
            } else if (node.elseBranch().isPresent()) {
                this.context.scope(() -> {
                    node.elseBranch().get().accept(this.context.getInterpreter());
                });
            }
            return null;
        }

        throw new RuntimeException("Condition expression must evaluate to a boolean.");
    }

    @Override
    public Value visitWhileStatement(WhileStatement node) {
        this.context.loop(() -> {
            while (true) {
                Value conditionValue = node.condition().accept(this.context.getInterpreter());
                if (conditionValue instanceof BooleanValue boolVal) {
                    if (!boolVal.value()) {
                        break;
                    }
                } else {
                    throw new RuntimeException("Condition expression must evaluate to a boolean.");
                }

                this.context.scope(() -> {
                    node.body().accept(this.context.getInterpreter());
                });

                if (this.context.isLoopBreakRequested()) {
                    this.context.resetLoopBreak();
                    break;
                }
                if (this.context.isLoopSkip()) {
                    this.context.requestLoopSkip();
                    continue;
                }
            }
        });
        return null;
    }

    @Override
    public Value visitDoWhileStatement(DoWhileStatement node) {
        this.context.loop(() -> {
            do {
                this.context.scope(() -> {
                    node.body().accept(this.context.getInterpreter());
                });
                if (this.context.isLoopBreakRequested()) {
                    this.context.resetLoopBreak();
                    break;
                }
                if (this.context.isLoopSkip()) {
                    this.context.resetLoopSkip();
                    continue;
                }
                Value conditionValue = node.condition().accept(this.context.getInterpreter());
                if (conditionValue instanceof BooleanValue boolVal) {
                    if (!boolVal.value()) {
                        break;
                    }
                } else {
                    throw new RuntimeException("Condition expression must evaluate to a boolean.");
                }
            } while (true);
        });
        return null;
    }

    @Override
    public Value visitForStatement(ForStatement node) {
        Value iterableValue = node.iterable().accept(this.context.getInterpreter());
        if (iterableValue instanceof ListValue listVal) {
            this.context.loop(() -> {
                for (Value item : listVal.value()) {
                    this.context.getScope().setVariable(node.variable(), item);

                    this.context.scope(() -> {
                        node.body().accept(this.context.getInterpreter());
                    });

                    if (this.context.isLoopBreakRequested()) {
                        this.context.resetLoopBreak();
                        break;
                    }
                    if (this.context.isLoopSkip()) {
                        this.context.resetLoopSkip();
                        continue;
                    }
                }
            });
        } else {
            throw new RuntimeException("Iterable expression must evaluate to a list.");
        }
        return null;
    }

    @Override
    public Value visitReturnStatement(ReturnStatement node) {
        if (!this.context.isFunctionContext()) {
            throw new RuntimeException("Cannot use 'return' outside of a function.");
        }
        Value retValue;
        if (node.value().isPresent()) {
            retValue = node.value().get().accept(this.context.getInterpreter());
        } else {
            retValue = new NullValue();
        }

        this.context.setFunctionReturn(retValue);
        return null;
    }

    @Override
    public Value visitBreakStatement(BreakStatement node) {
        if (!this.context.isLoopContext()) {
            throw new RuntimeException("Cannot use 'break' outside of a loop.");
        }
        this.context.requestLoopBreak();
        return null;
    }

    @Override
    public Value visitContinueStatement(ContinueStatement node) {
        if (!this.context.isLoopContext()) {
            throw new RuntimeException("Cannot use 'continue' outside of a loop.");
        }
        this.context.requestLoopSkip();
        return null;
    }

    @Override
    public Value visitAssignmentStatement(AssignmentStatement node) {
        if (!OperatorPriority.isAssignmentOperator(node.operator())) {
            throw new RuntimeException("Operator " + node.operator() + " is not an assignment operator.");
        }

        switch (node.operator()) {
            case ASSIGN: {
                if (node.isConstant()) {
                    if (this.context.getScope().hasConstant(node.name())) {
                        throw new RuntimeException("Constant '" + node.name() + "' is already defined in this scope.");
                    }
                    this.context.getScope().defineConstant(node.name(), node.value().accept(this.context.getInterpreter()));
                } else {
                    this.context.getScope().setVariable(node.name(), node.value().accept(this.context.getInterpreter()));
                }
            }
                break;
            case ASSIGN_PLUS:
            case ASSIGN_MINUS:
            case ASSIGN_MULTIPLY:
            case ASSIGN_DIVIDE:
            case ASSIGN_MODULO: {
                if (node.isConstant() || this.context.getScope().hasConstant(node.name())) {
                    throw new RuntimeException("Cannot add to constant '" + node.name() + "'.");
                }
                if (!this.context.getScope().hasVariable(node.name())) {
                    throw new RuntimeException("Variable '" + node.name() + "' is not defined.");
                }

                Operator baseOperator;
                switch (node.operator()) {
                    case ASSIGN_PLUS:
                        baseOperator = Operator.PLUS;
                        break;
                    case ASSIGN_MINUS:
                        baseOperator = Operator.MINUS;
                        break;
                    case ASSIGN_MULTIPLY:
                        baseOperator = Operator.MULTIPLY;
                        break;
                    case ASSIGN_DIVIDE:
                        baseOperator = Operator.DIVIDE;
                        break;
                    case ASSIGN_MODULO:
                        baseOperator = Operator.MODULO;
                        break;
                    default:
                        throw new RuntimeException("Unknown assignment operator: " + node.operator());
                }

                Value newValue = this.context.getInterpreter().visitBinaryExpression(
                        new BinaryExpression(new IdentifierExpression(node.name()), baseOperator, node.value()));
                this.context.getScope().setVariable(node.name(), newValue);
            }
                break;
            default:
                throw new RuntimeException("Unknown assignment operator: " + node.operator());
        }

        return null;
    }

    @Override
    public Value visitExpressionStatement(ExpressionStatement node) {
        node.expression().accept(this.context.getInterpreter());
        return null;
    }

}
