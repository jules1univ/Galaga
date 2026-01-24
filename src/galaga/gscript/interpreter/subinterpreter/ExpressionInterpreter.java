package galaga.gscript.interpreter.subinterpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import galaga.gscript.ast.expression.Expression;
import galaga.gscript.ast.expression.ExpressionVisitor;
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
import galaga.gscript.interpreter.InterpreterContext;
import galaga.gscript.lexer.rules.OperatorPriority;
import galaga.gscript.types.values.BooleanValue;
import galaga.gscript.types.values.FloatValue;
import galaga.gscript.types.values.FunctionValue;
import galaga.gscript.types.values.IntegerValue;
import galaga.gscript.types.values.ListValue;
import galaga.gscript.types.values.MapValue;
import galaga.gscript.types.values.StringValue;
import galaga.gscript.types.values.Value;

public class ExpressionInterpreter implements ExpressionVisitor<Value> {
    private final InterpreterContext context;

    public ExpressionInterpreter(InterpreterContext context) {
        this.context = context;
    }

    @Override
    public Value visitBinaryExpression(BinaryExpression node) {
        if (!OperatorPriority.isBinaryOperator(node.operator())) {
            throw new RuntimeException("Operator " + node.operator() + " is not a binary operator.");
        }

        Value left = node.left().accept(this.context.getInterpreter());
        Value right = node.right().accept(this.context.getInterpreter());

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

        Value operand = node.operand().accept(this.context.getInterpreter());
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

        Optional<FunctionValue> functionOpt = Optional.empty();
        if (node.invoker() instanceof IdentifierExpression identExpr) {

            if (!this.context.getScope().hasVariable(identExpr.name())) {
                throw new RuntimeException("Function '" + identExpr.name() + "' is not defined.");
            }

            Value rawValue = this.context.getScope().getVariable(identExpr.name()).get();
            if (!(rawValue instanceof FunctionValue funcVal)) {
                throw new RuntimeException("Object '" + identExpr.name() + "' is not a function.");
            }

            functionOpt = Optional.of(funcVal);
        } else if (node.invoker() instanceof FunctionExpression funcExpr) {
            functionOpt = Optional
                    .of(new FunctionValue(Optional.empty(), funcExpr.parameters(), funcExpr.body(),
                            this.context.getScope(), false));
        } else if (node.invoker() instanceof IndexExpression indexExpr) {
            Value rawFunction = this.visitIndexExpression(indexExpr);
            if (!(rawFunction instanceof FunctionValue funcVal)) {
                throw new RuntimeException("The indexed value is not a function.");
            }
            functionOpt = Optional.of(funcVal);
        }

        if (functionOpt.isEmpty()) {
            throw new RuntimeException("Invalid function call.");
        }

        FunctionValue function = functionOpt.get();
        if (function.parameters().size() != node.arguments().size()) {
            throw new RuntimeException(
                    "Function '" + function.name().orElse("<anonymous>") + "' expects " + function.parameters().size()
                            + " arguments, but got " + node.arguments().size() + ".");
        }

        if (function.isNative()) {
            if (function.name().isEmpty()) {
                throw new RuntimeException("Native function must have a name.");
            }

            if (!this.context.isNativeDefined(function.name().get())) {
                throw new RuntimeException("Native function '" + function.name().get() + "' is not defined.");
            }
        }

        return this.context.scope(
                function.isNative() ? this.context.getScope() : function.parent(),
                (Void v) -> {
                    Map<String, Value> argsMap = new HashMap<>();
                    for (int i = 0; i < function.parameters().size(); i++) {
                        String paramName = function.parameters().get(i);
                        Value argValue = node.arguments().get(i).accept(this.context.getInterpreter());

                        this.context.getScope().setVariable(paramName, argValue);
                        argsMap.put(paramName, argValue);
                    }

                    if (function.isNative()) {
                        return this.context.getNative(function.name().get()).apply(argsMap);
                    } else {
                        return this.context.function((Void vv) -> {
                            return function.body().accept(this.context.getInterpreter());
                        });
                    }
                });
    }

    @Override
    public Value visitIndexExpression(IndexExpression node) {

        Value rawCollection = node.target().accept(this.context.getInterpreter());
        if (rawCollection instanceof ListValue list) {
            Value rawIndex = node.index().accept(this.context.getInterpreter());
            if (!(rawIndex instanceof IntegerValue index)) {
                throw new RuntimeException("Index must be an integer.");
            }

            if (index.value() < 0 || index.value() >= list.value().size()) {
                throw new RuntimeException("Index out of bounds.");
            }

            return list.value().get(index.value());

        } else if (rawCollection instanceof MapValue map) {
            Value rawKey = node.index().accept(this.context.getInterpreter());
            if (!map.value().containsKey(rawKey)) {
                throw new RuntimeException("Key not found in map.");
            }

            return map.value().get(rawKey);
        }

        throw new RuntimeException("Target is not indexable.");
    }

    @Override
    public Value visitIdentifierExpression(IdentifierExpression node) {
        if (!this.context.getScope().hasVariable(node.name()) && !this.context.getScope().hasConstant(node.name())) {
            throw new RuntimeException("Object '" + node.name() + "' is not defined.");
        }

        if (this.context.getScope().hasConstant(node.name())) {
            return this.context.getScope().getConstant(node.name()).get();
        }
        return this.context.getScope().getVariable(node.name()).get();
    }

    @Override
    public Value visitLiteralExpression(LiteralExpression node) {
        return node.value();
    }

    @Override
    public Value visitListExpression(ListExpression node) {
        List<Value> elements = new ArrayList<>();
        for (var element : node.elements()) {
            elements.add(element.accept(this.context.getInterpreter()));
        }
        return new ListValue(elements);
    }

    @Override
    public Value visitMapExpression(MapExpression node) {
        Map<Value, Value> map = new HashMap<>();
        for (Map.Entry<Expression, Expression> entry : node.entries().entrySet()) {
            Value key = entry.getKey().accept(this.context.getInterpreter());
            Value value = entry.getValue().accept(this.context.getInterpreter());
            map.put(key, value);
        }
        return new MapValue(map);
    }

    @Override
    public Value visitFunctionExpression(FunctionExpression node) {
        return new FunctionValue(
                Optional.empty(),
                node.parameters(),
                node.body(),
                this.context.getScope(),
                false);
    }

    @Override
    public Value visitRangeExpression(RangeExpression node) {
        Value start = node.start().accept(this.context.getInterpreter());
        if (!(start instanceof IntegerValue startInt)) {
            throw new RuntimeException("Range start must be an integer.");
        }
        Value end = node.end().accept(this.context.getInterpreter());
        if (!(end instanceof IntegerValue endInt)) {
            throw new RuntimeException("Range end must be an integer.");
        }

        return new ListValue(
                IntStream.rangeClosed(startInt.value(), endInt.value())
                        .mapToObj(IntegerValue::new)
                        .collect(Collectors.toList()));
    }

}
