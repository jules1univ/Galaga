package galaga.gscript.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import galaga.gscript.ast.ASTDepthVisitor;
import galaga.gscript.ast.Program;
import galaga.gscript.ast.declaration.Declaration;
import galaga.gscript.ast.declaration.FunctionDeclaration;
import galaga.gscript.ast.declaration.NativeFunctionDeclaration;
import galaga.gscript.ast.declaration.VariableDeclaration;
import galaga.gscript.ast.expression.Expression;
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
import galaga.gscript.ast.statement.Statement;
import galaga.gscript.ast.statement.logic.IfStatement;
import galaga.gscript.ast.statement.logic.loop.BreakStatement;
import galaga.gscript.ast.statement.logic.loop.ContinueStatement;
import galaga.gscript.ast.statement.logic.loop.DoWhileStatement;
import galaga.gscript.ast.statement.logic.loop.ForStatement;
import galaga.gscript.ast.statement.logic.loop.WhileStatement;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.rules.OperatorPriority;
import galaga.gscript.types.values.BooleanValue;
import galaga.gscript.types.values.FloatValue;
import galaga.gscript.types.values.FunctionValue;
import galaga.gscript.types.values.IntegerValue;
import galaga.gscript.types.values.ListValue;
import galaga.gscript.types.values.MapValue;
import galaga.gscript.types.values.NullValue;
import galaga.gscript.types.values.StringValue;
import galaga.gscript.types.values.Value;

public final class Interpreter extends ASTDepthVisitor<Value> {
    private final Scope globalScope = new Scope(null);
    private Scope currentScope = globalScope;

    private boolean isInFunction = false;
    private boolean isInLoop = false;

    private boolean shouldBreak = false;
    private boolean shouldContinue = false;
    private Value returnValue = null;

    private Map<String, Function<Scope, Value>> nativeFunctions = new HashMap<>();

    public void interpret(Program program) {
        program.accept(this);
    }

    public void defineNativeFunction(String name, Function<Scope, Value> function) {
        nativeFunctions.put(name, function);
    }

    public Optional<Value> callFunction(String name) {
        return callFunction(name, new Value[] {});
    }

    public Optional<Value> callFunction(String name, Value... args) {
        Optional<Value> functionValueOpt = globalScope.getVariable(name);
        if (functionValueOpt.isPresent() && functionValueOpt.get() instanceof FunctionValue function) {
            Scope previousScope = this.currentScope;
            this.currentScope = new Scope(globalScope);

            int provided = args == null ? 0 : args.length;
            if (function.parameters().size() != provided) {
                throw new RuntimeException("Function '" + name + "' expects " + function.parameters().size()
                        + " arguments, but got " + provided + ".");
            }

            List<String> parameters = function.parameters();
            for (int i = 0; i < parameters.size(); i++) {
                String paramName = parameters.get(i);
                Value argValue = args[i];
                this.currentScope.setVariable(paramName, argValue);
            }

            this.isInFunction = true;
            Value retValue = function.body().accept(this);
            this.isInFunction = false;

            this.currentScope = previousScope;

            if (retValue != null) {
                return Optional.of(retValue);
            }
        }

        return Optional.empty();

    }

    public Optional<Value> getVariable(String name) {
        return globalScope.getVariable(name);
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
        if (currentScope.hasVariable(node.name())) {
            throw new RuntimeException("Function '" + node.name() + "' is already defined in this scope.");
        }

        FunctionValue functionValue = new FunctionValue(node.parameters(), node.body(), currentScope);
        currentScope.setVariable(node.name(), functionValue);
        return null;
    }

    @Override
    public Value visitVariableDeclaration(VariableDeclaration node) {
        if (currentScope.hasVariable(node.name()) || currentScope.hasConstant(node.name())) {
            throw new RuntimeException("Object '" + node.name() + "' is already defined in this scope.");
        }
        if (node.isConstant()) {
            currentScope.defineConstant(node.name(), node.value().accept(this));
        } else {
            currentScope.setVariable(node.name(), node.value().accept(this));
        }
        return null;
    }

    @Override
    public Value visitNativeFunctionDeclaration(NativeFunctionDeclaration node) {
        if (currentScope.hasVariable(node.name())) {
            throw new RuntimeException("Function '" + node.name() + "' is already defined in this scope.");
        }

        if( !nativeFunctions.containsKey(node.name())) {
            throw new RuntimeException("Native function '" + node.name() + "' is not defined.");
        }

        FunctionValue functionValue = new FunctionValue(node.parameters(), null, null);
        currentScope.setVariable(node.name(), functionValue);
        return null;
    }

    @Override
    public Value visitBlockStatement(BlockStatement node) {
        for (Statement statement : node.statements()) {
            statement.accept(this);
            if (this.returnValue != null && this.isInFunction) {
                Value retVal = this.returnValue;
                this.returnValue = null;
                return retVal;
            }
        }
        return null;
    }

    @Override
    public Value visitIfStatement(IfStatement node) {
        if (node.condition().accept(this) instanceof BooleanValue boolVal) {
            if (boolVal.value()) {
                this.depth(() -> {
                    node.thenBranch().accept(this);
                });
            } else if (node.elseBranch().isPresent()) {
                this.depth(() -> {
                    node.elseBranch().get().accept(this);
                });
            }
            return null;
        }

        throw new RuntimeException("Condition expression must evaluate to a boolean.");
    }

    @Override
    public Value visitWhileStatement(WhileStatement node) {
        this.isInLoop = true;
        while (true) {
            Value conditionValue = node.condition().accept(this);
            if (conditionValue instanceof BooleanValue boolVal) {
                if (!boolVal.value()) {
                    break;
                }
            } else {
                throw new RuntimeException("Condition expression must evaluate to a boolean.");
            }

            this.depth(() -> {
                node.body().accept(this);
            });
            if (this.shouldBreak) {
                this.shouldBreak = false;
                break;
            }
            if (this.shouldContinue) {
                this.shouldContinue = false;
                continue;
            }
        }
        this.isInLoop = false;
        return null;
    }

    @Override
    public Value visitDoWhileStatement(DoWhileStatement node) {
        do {
            this.depth(() -> {
                node.body().accept(this);
            });
            if (this.shouldBreak) {
                this.shouldBreak = false;
                break;
            }
            if (this.shouldContinue) {
                this.shouldContinue = false;
                continue;
            }
            Value conditionValue = node.condition().accept(this);
            if (conditionValue instanceof BooleanValue boolVal) {
                if (!boolVal.value()) {
                    break;
                }
            } else {
                throw new RuntimeException("Condition expression must evaluate to a boolean.");
            }
        } while (true);
        return null;
    }

    @Override
    public Value visitForStatement(ForStatement node) {
        Value iterableValue = node.iterable().accept(this);
        if (iterableValue instanceof ListValue listVal) {
            for (Value item : listVal.value()) {
                this.currentScope.setVariable(node.variable(), item);

                this.isInLoop = true;
                this.depth(() -> {
                    node.body().accept(this);
                });
                this.isInLoop = false;

                if (this.shouldBreak) {
                    this.shouldBreak = false;
                    break;
                }
                if (this.shouldContinue) {
                    this.shouldContinue = false;
                    continue;
                }
            }
        } else {
            throw new RuntimeException("Iterable expression must evaluate to a list.");
        }
        return null;
    }

    @Override
    public Value visitReturnStatement(ReturnStatement node) {
        if (!this.isInFunction) {
            throw new RuntimeException("Cannot use 'return' outside of a function.");
        }
        if (node.value().isPresent()) {
            this.returnValue = node.value().get().accept(this);
        } else {
            this.returnValue = new NullValue();
        }
        return null;
    }

    @Override
    public Value visitBreakStatement(BreakStatement node) {
        if (!this.isInLoop) {
            throw new RuntimeException("Cannot use 'break' outside of a loop.");
        }
        this.shouldBreak = true;
        return null;
    }

    @Override
    public Value visitContinueStatement(ContinueStatement node) {
        if (!this.isInLoop) {
            throw new RuntimeException("Cannot use 'continue' outside of a loop.");
        }
        this.shouldContinue = true;
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
                    if (currentScope.hasConstant(node.name())) {
                        throw new RuntimeException("Constant '" + node.name() + "' is already defined in this scope.");
                    }
                    currentScope.defineConstant(node.name(), node.value().accept(this));
                } else {
                    currentScope.setVariable(node.name(), node.value().accept(this));
                }
            }
                break;
            case ASSIGN_PLUS:
            case ASSIGN_MINUS:
            case ASSIGN_MULTIPLY:
            case ASSIGN_DIVIDE:
            case ASSIGN_MODULO: {
                if (node.isConstant() || currentScope.hasConstant(node.name())) {
                    throw new RuntimeException("Cannot add to constant '" + node.name() + "'.");
                }
                if (!currentScope.hasVariable(node.name())) {
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

                Value newValue = this.visitBinaryExpression(
                        new BinaryExpression(new IdentifierExpression(node.name()), baseOperator, node.value()));
                currentScope.setVariable(node.name(), newValue);
            }
                break;
            default:
                throw new RuntimeException("Unknown assignment operator: " + node.operator());
        }

        return null;
    }

    @Override
    public Value visitExpressionStatement(ExpressionStatement node) {
        node.expression().accept(this);
        return null;
    }

    @Override
    public Value visitBinaryExpression(BinaryExpression node) {
        if (!OperatorPriority.isBinaryOperator(node.operator())) {
            throw new RuntimeException("Operator " + node.operator() + " is not a binary operator.");
        }

        Value left = node.left().accept(this);
        Value right = node.right().accept(this);

        switch (node.operator()) {
            case PLUS -> {
                if (left instanceof IntegerValue leftInt && right instanceof IntegerValue rightInt) {
                    return new IntegerValue(leftInt.value() + rightInt.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof FloatValue rightFloat) {
                    return new FloatValue(leftFloat.value() + rightFloat.value());
                } else if (left instanceof IntegerValue leftInt && right instanceof FloatValue rightFloat) {
                    return new FloatValue(leftInt.value() + rightFloat.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof IntegerValue rightInt) {
                    return new FloatValue(leftFloat.value() + rightInt.value());
                } else if (left instanceof StringValue leftStr && right instanceof StringValue rightStr) {
                    return new StringValue(leftStr.value() + rightStr.value());
                } else if (left instanceof ListValue leftList && right instanceof ListValue rightList) {
                    List<Value> combined = new ArrayList<>();
                    combined.addAll(leftList.value());
                    combined.addAll(rightList.value());
                    return new ListValue(combined);
                } else if (left instanceof MapValue leftMap && right instanceof MapValue rightMap) {
                    Map<Value, Value> combined = new HashMap<>();
                    combined.putAll(leftMap.value());
                    combined.putAll(rightMap.value());
                    return new MapValue(combined);
                } else {
                    throw new RuntimeException("Operands of PLUS operator is not supported.");
                }
            }
            case MINUS -> {
                if (left instanceof IntegerValue leftInt && right instanceof IntegerValue rightInt) {
                    return new IntegerValue(leftInt.value() - rightInt.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof FloatValue rightFloat) {
                    return new FloatValue(leftFloat.value() - rightFloat.value());
                } else if (left instanceof IntegerValue leftInt && right instanceof FloatValue rightFloat) {
                    return new FloatValue(leftInt.value() - rightFloat.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof IntegerValue rightInt) {
                    return new FloatValue(leftFloat.value() - rightInt.value());
                } else {
                    throw new RuntimeException("Operands of MINUS operator must be numbers.");
                }
            }
            case MULTIPLY -> {
                if (left instanceof IntegerValue leftInt && right instanceof IntegerValue rightInt) {
                    return new IntegerValue(leftInt.value() * rightInt.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof FloatValue rightFloat) {
                    return new FloatValue(leftFloat.value() * rightFloat.value());
                } else if (left instanceof IntegerValue leftInt && right instanceof FloatValue rightFloat) {
                    return new FloatValue(leftInt.value() * rightFloat.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof IntegerValue rightInt) {
                    return new FloatValue(leftFloat.value() * rightInt.value());
                } else {
                    throw new RuntimeException("Operands of MULTIPLY operator must be numbers.");
                }
            }
            case DIVIDE -> {
                if (left instanceof IntegerValue leftInt && right instanceof IntegerValue rightInt) {
                    if (rightInt.value() == 0) {
                        throw new RuntimeException("Division by zero.");
                    }
                    return new IntegerValue(leftInt.value() / rightInt.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof FloatValue rightFloat) {
                    if (rightFloat.value() == 0) {
                        throw new RuntimeException("Division by zero.");
                    }
                    return new FloatValue(leftFloat.value() / rightFloat.value());
                } else if (left instanceof IntegerValue leftInt && right instanceof FloatValue rightFloat) {
                    if (rightFloat.value() == 0) {
                        throw new RuntimeException("Division by zero.");
                    }
                    return new FloatValue(leftInt.value() / rightFloat.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof IntegerValue rightInt) {
                    if (rightInt.value() == 0) {
                        throw new RuntimeException("Division by zero.");
                    }
                    return new FloatValue(leftFloat.value() / rightInt.value());
                } else {
                    throw new RuntimeException("Operands of DIVIDE operator must be numbers.");
                }
            }
            case MODULO -> {
                if (left instanceof IntegerValue leftInt && right instanceof IntegerValue rightInt) {
                    if (rightInt.value() == 0) {
                        throw new RuntimeException("Modulo by zero.");
                    }
                    return new IntegerValue(leftInt.value() % rightInt.value());
                } else {
                    throw new RuntimeException("Operands of MODULO operator must be integers.");
                }
            }
            case AND -> {
                if (left instanceof BooleanValue leftBool && right instanceof BooleanValue rightBool) {
                    return new BooleanValue(leftBool.value() && rightBool.value());
                } else {
                    throw new RuntimeException("Operands of AND operator must be booleans.");
                }
            }
            case OR -> {
                if (left instanceof BooleanValue leftBool && right instanceof BooleanValue rightBool) {
                    return new BooleanValue(leftBool.value() || rightBool.value());
                } else {
                    throw new RuntimeException("Operands of OR operator must be booleans.");
                }
            }
            case EQUALS -> {
                if (left instanceof IntegerValue leftInt && right instanceof IntegerValue rightInt) {
                    return new BooleanValue(leftInt.value() == rightInt.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof FloatValue rightFloat) {
                    return new BooleanValue(leftFloat.value() == rightFloat.value());
                } else if (left instanceof BooleanValue leftBool && right instanceof BooleanValue rightBool) {
                    return new BooleanValue(leftBool.value() == rightBool.value());
                } else if (left instanceof StringValue leftStr && right instanceof StringValue rightStr) {
                    return new BooleanValue(leftStr.value().equals(rightStr.value()));
                } else if (left instanceof ListValue leftList && right instanceof ListValue rightList) {
                    return new BooleanValue(leftList.value().equals(rightList.value()));
                } else if (left instanceof MapValue leftMap && right instanceof MapValue rightMap) {
                    return new BooleanValue(leftMap.value().equals(rightMap.value()));
                } else {
                    throw new RuntimeException("Operands of EQUALS operator must be of the same type.");
                }
            }
            case NOT_EQUALS -> {
                if (left instanceof IntegerValue leftInt && right instanceof IntegerValue rightInt) {
                    return new BooleanValue(leftInt.value() != rightInt.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof FloatValue rightFloat) {
                    return new BooleanValue(leftFloat.value() != rightFloat.value());
                } else if (left instanceof BooleanValue leftBool && right instanceof BooleanValue rightBool) {
                    return new BooleanValue(leftBool.value() != rightBool.value());
                } else if (left instanceof StringValue leftStr && right instanceof StringValue rightStr) {
                    return new BooleanValue(!leftStr.value().equals(rightStr.value()));
                } else if (left instanceof ListValue leftList && right instanceof ListValue rightList) {
                    return new BooleanValue(!leftList.value().equals(rightList.value()));
                } else if (left instanceof MapValue leftMap && right instanceof MapValue rightMap) {
                    return new BooleanValue(!leftMap.value().equals(rightMap.value()));
                } else {
                    throw new RuntimeException("Operands of NOT_EQUALS operator must be of the same type.");
                }
            }
            case LESS_THAN -> {
                if (left instanceof IntegerValue leftInt && right instanceof IntegerValue rightInt) {
                    return new BooleanValue(leftInt.value() < rightInt.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof FloatValue rightFloat) {
                    return new BooleanValue(leftFloat.value() < rightFloat.value());
                } else if (left instanceof IntegerValue leftInt && right instanceof FloatValue rightFloat) {
                    return new BooleanValue(leftInt.value() < rightFloat.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof IntegerValue rightInt) {
                    return new BooleanValue(leftFloat.value() < rightInt.value());
                } else if (left instanceof StringValue leftStr && right instanceof StringValue rightStr) {
                    return new BooleanValue(leftStr.value().compareTo(rightStr.value()) < 0);
                } else if (left instanceof ListValue leftList && right instanceof ListValue rightList) {
                    return new BooleanValue(leftList.value().size() < rightList.value().size());
                } else if (left instanceof MapValue leftMap && right instanceof MapValue rightMap) {
                    return new BooleanValue(leftMap.value().size() < rightMap.value().size());
                } else {
                    throw new RuntimeException("Operands of LESS_THAN operator must be numbers.");
                }
            }
            case GREATER_THAN -> {
                if (left instanceof IntegerValue leftInt && right instanceof IntegerValue rightInt) {
                    return new BooleanValue(leftInt.value() > rightInt.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof FloatValue rightFloat) {
                    return new BooleanValue(leftFloat.value() > rightFloat.value());
                } else if (left instanceof IntegerValue leftInt && right instanceof FloatValue rightFloat) {
                    return new BooleanValue(leftInt.value() > rightFloat.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof IntegerValue rightInt) {
                    return new BooleanValue(leftFloat.value() > rightInt.value());
                } else if (left instanceof StringValue leftStr && right instanceof StringValue rightStr) {
                    return new BooleanValue(leftStr.value().compareTo(rightStr.value()) > 0);
                } else if (left instanceof ListValue leftList && right instanceof ListValue rightList) {
                    return new BooleanValue(leftList.value().size() > rightList.value().size());
                } else if (left instanceof MapValue leftMap && right instanceof MapValue rightMap) {
                    return new BooleanValue(leftMap.value().size() > rightMap.value().size());
                } else {
                    throw new RuntimeException("Operands of GREATER_THAN operator must be numbers.");
                }
            }
            case GREATER_THAN_OR_EQUAL -> {
                if (left instanceof IntegerValue leftInt && right instanceof IntegerValue rightInt) {
                    return new BooleanValue(leftInt.value() >= rightInt.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof FloatValue rightFloat) {
                    return new BooleanValue(leftFloat.value() >= rightFloat.value());
                } else if (left instanceof IntegerValue leftInt && right instanceof FloatValue rightFloat) {
                    return new BooleanValue(leftInt.value() >= rightFloat.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof IntegerValue rightInt) {
                    return new BooleanValue(leftFloat.value() >= rightInt.value());
                } else {
                    throw new RuntimeException("Operands of GREATER_THAN_OR_EQUAL operator must be numbers.");
                }
            }
            case LESS_THAN_OR_EQUAL -> {
                if (left instanceof IntegerValue leftInt && right instanceof IntegerValue rightInt) {
                    return new BooleanValue(leftInt.value() <= rightInt.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof FloatValue rightFloat) {
                    return new BooleanValue(leftFloat.value() <= rightFloat.value());
                } else if (left instanceof IntegerValue leftInt && right instanceof FloatValue rightFloat) {
                    return new BooleanValue(leftInt.value() <= rightFloat.value());
                } else if (left instanceof FloatValue leftFloat && right instanceof IntegerValue rightInt) {
                    return new BooleanValue(leftFloat.value() <= rightInt.value());
                } else {
                    throw new RuntimeException("Operands of LESS_THAN_OR_EQUAL operator must be numbers.");
                }
            }
            case BITWISE_AND -> {
                if (left instanceof IntegerValue leftInt && right instanceof IntegerValue rightInt) {
                    return new IntegerValue(leftInt.value() & rightInt.value());
                } else {
                    throw new RuntimeException("Operands of BITWISE_AND operator must be integers.");
                }
            }
            case BITWISE_OR -> {
                if (left instanceof IntegerValue leftInt && right instanceof IntegerValue rightInt) {
                    return new IntegerValue(leftInt.value() | rightInt.value());
                } else {
                    throw new RuntimeException("Operands of BITWISE_OR operator must be integers.");
                }
            }
            case BITWISE_XOR -> {
                if (left instanceof IntegerValue leftInt && right instanceof IntegerValue rightInt) {
                    return new IntegerValue(leftInt.value() ^ rightInt.value());
                } else {
                    throw new RuntimeException("Operands of BITWISE_XOR operator must be integers.");
                }
            }
            default -> throw new RuntimeException("Unknown binary operator: " + node.operator());
        }
    }

    @Override
    public Value visitUnaryExpression(UnaryExpression node) {
        if (!OperatorPriority.isUnaryOperator(node.operator())) {
            throw new RuntimeException("Operator " + node.operator() + " is not a unary operator.");
        }

        Value operand = node.operand().accept(this);
        switch (node.operator()) {
            case NOT -> {
                if (operand instanceof BooleanValue boolVal) {
                    return new BooleanValue(!boolVal.value());
                } else {
                    throw new RuntimeException("Operand of NOT operator must be a boolean.");
                }
            }
            case BITWISE_NOT -> {
                if (operand instanceof IntegerValue intVal) {
                    return new IntegerValue(~intVal.value());
                } else if (operand instanceof BooleanValue boolVal) {
                    return new BooleanValue(!boolVal.value());
                } else {
                    throw new RuntimeException("Operand of BITWISE_NOT operator must be an integer or boolean.");
                }
            }
            case INCREMENT -> {
                if (operand instanceof IntegerValue intVal) {
                    return new IntegerValue(intVal.value() + 1);
                } else if (operand instanceof FloatValue floatVal) {
                    return new FloatValue(floatVal.value() + 1);
                } else {
                    throw new RuntimeException("Operand of INCREMENT operator must be an integer or float.");
                }
            }
            case DECREMENT -> {
                if (operand instanceof IntegerValue intVal) {
                    return new IntegerValue(intVal.value() - 1);
                } else if (operand instanceof FloatValue floatVal) {
                    return new FloatValue(floatVal.value() - 1);
                } else {
                    throw new RuntimeException("Operand of DECREMENT operator must be an integer or float.");
                }
            }
            case PLUS -> {
                if (operand instanceof IntegerValue intVal) {
                    return new IntegerValue(+intVal.value());
                } else if (operand instanceof FloatValue floatVal) {
                    return new FloatValue(+floatVal.value());
                } else {
                    throw new RuntimeException("Operand of PLUS operator must be an integer or float.");
                }
            }
            case MINUS -> {
                if (operand instanceof IntegerValue intVal) {
                    return new IntegerValue(-intVal.value());
                } else if (operand instanceof FloatValue floatVal) {
                    return new FloatValue(-floatVal.value());
                } else {
                    throw new RuntimeException("Operand of MINUS operator must be an integer or float.");
                }
            }
            default -> throw new RuntimeException("Unknown unary operator: " + node.operator());
        }
    }

    @Override
    public Value visitCallExpression(CallExpression node) {
        if (node.invoker() instanceof IdentifierExpression func) {
            String functionName = func.name();
            Optional<Value> functionOpt = currentScope.getVariable(functionName);
            if (functionOpt.isEmpty() || !(functionOpt.get() instanceof FunctionValue function)) {
                throw new RuntimeException("Function '" + functionName + "' is not defined.");
            }

            if (function.parameters().size() != node.arguments().size()) {
                throw new RuntimeException("Function '" + functionName + "' expects " + function.parameters().size()
                        + " arguments, but got " + node.arguments().size() + ".");
            }

            boolean isNative = function.body() == null && function.parent() == null;

            Scope previousScope = this.currentScope;
            this.currentScope = new Scope(isNative ? this.currentScope : function.parent());

            for (int i = 0; i < function.parameters().size(); i++) {
                String paramName = function.parameters().get(i);
                Value argValue = node.arguments().get(i).accept(this);
                this.currentScope.setVariable(paramName, argValue);
            }

            Value retValue;
            if (isNative) {
                if (!nativeFunctions.containsKey(functionName)) {
                    throw new RuntimeException("Native function '" + functionName + "' is not defined.");
                }
                retValue = nativeFunctions.get(functionName).apply(this.currentScope);
            } else {
                this.isInFunction = true;
                retValue = function.body().accept(this);
                this.isInFunction = false;
            }

            this.currentScope = previousScope;
            return retValue;
        } else if (node.invoker() instanceof FunctionExpression funcExpr) {
            FunctionValue function = new FunctionValue(funcExpr.parameters(), funcExpr.body(), this.currentScope);

            if (function.parameters().size() != node.arguments().size()) {
                throw new RuntimeException("Function expects " + function.parameters().size()
                        + " arguments, but got " + node.arguments().size() + ".");
            }

            Scope previousScope = this.currentScope;
            this.currentScope = new Scope(function.parent());

            for (int i = 0; i < function.parameters().size(); i++) {
                String paramName = function.parameters().get(i);
                Value argValue = node.arguments().get(i).accept(this);
                this.currentScope.setVariable(paramName, argValue);
            }
            this.isInFunction = true;
            Value retValue = function.body().accept(this);
            this.isInFunction = false;

            this.currentScope = previousScope;
            return retValue;
        }

        throw new RuntimeException("Invalid function call.");
    }

    @Override
    public Value visitIndexExpression(IndexExpression node) {

        Value rawCollection = node.target().accept(this);
        if (rawCollection instanceof ListValue list) {
            Value rawIndex = node.index().accept(this);
            if (!(rawIndex instanceof IntegerValue index)) {
                throw new RuntimeException("Index must be an integer.");
            }

            if (index.value() < 0 || index.value() >= list.value().size()) {
                throw new RuntimeException("Index out of bounds.");
            }

            return list.value().get(index.value());

        } else if (rawCollection instanceof MapValue map) {
            Value rawKey = node.index().accept(this);
            if (!map.value().containsKey(rawKey)) {
                throw new RuntimeException("Key not found in map.");
            }

            return map.value().get(rawKey);
        }

        throw new RuntimeException("Target is not indexable.");
    }

    @Override
    public Value visitIdentifierExpression(IdentifierExpression node) {
        if (!currentScope.hasVariable(node.name()) && !currentScope.hasConstant(node.name())) {
            throw new RuntimeException("Object '" + node.name() + "' is not defined.");
        }

        if (currentScope.hasConstant(node.name())) {
            return currentScope.getConstant(node.name()).get();
        }
        return currentScope.getVariable(node.name()).get();
    }

    @Override
    public Value visitLiteralExpression(LiteralExpression node) {
        return node.value();
    }

    @Override
    public Value visitListExpression(ListExpression node) {
        List<Value> elements = new ArrayList<>();
        for (var element : node.elements()) {
            elements.add(element.accept(this));
        }
        return new ListValue(elements);
    }

    @Override
    public Value visitMapExpression(MapExpression node) {
        Map<Value, Value> map = new HashMap<>();
        for (Map.Entry<Expression, Expression> entry : node.entries().entrySet()) {
            Value key = entry.getKey().accept(this);
            Value value = entry.getValue().accept(this);
            map.put(key, value);
        }
        return new MapValue(map);
    }

    @Override
    public Value visitFunctionExpression(FunctionExpression node) {
        return new FunctionValue(node.parameters(), node.body(), this.currentScope);
    }

    @Override
    public Value visitRangeExpression(RangeExpression node) {
        Value start = node.start().accept(this);
        if (!(start instanceof IntegerValue startInt)) {
            throw new RuntimeException("Range start must be an integer.");
        }
        Value end = node.end().accept(this);
        if (!(end instanceof IntegerValue endInt)) {
            throw new RuntimeException("Range end must be an integer.");
        }

        return new ListValue(
                IntStream.rangeClosed(startInt.value(), endInt.value())
                        .mapToObj(IntegerValue::new)
                        .collect(Collectors.toList()));
    }

    @Override
    protected void preDepth() {
        super.preDepth();
        Scope parent = this.currentScope;
        this.currentScope = new Scope(parent);
    }

    @Override
    protected void postDepth() {
        super.postDepth();
        if (this.currentScope.getParent() != null) {
            this.currentScope = this.currentScope.getParent();
        }
    }

    @Override
    protected Value getDepth() {
        throw new UnsupportedOperationException("This method should not be called.");
    }

}
