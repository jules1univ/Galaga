package galaga.gscript.lexer.rules;

import java.util.Map;

public final class OperatorPriority {
    public static final int MIN_PRIORITY = 1;
    public static final int MAX_PRIORITY = 14;

    public static final Map<Operator, Integer> OPERATOR_PRIORITY_MAP = Map.ofEntries(
            Map.entry(Operator.MULTIPLY, 13),
            Map.entry(Operator.DIVIDE, 13),
            Map.entry(Operator.MODULO, 13),

            Map.entry(Operator.PLUS, 12),
            Map.entry(Operator.MINUS, 12),

            Map.entry(Operator.LESS_THAN, 10),
            Map.entry(Operator.LESS_THAN_OR_EQUAL, 10),
            Map.entry(Operator.GREATER_THAN, 10),
            Map.entry(Operator.GREATER_THAN_OR_EQUAL, 10),

            Map.entry(Operator.EQUALS, 9),
            Map.entry(Operator.NOT_EQUALS, 9),

            Map.entry(Operator.BITWISE_AND, 8),

            Map.entry(Operator.BITWISE_XOR, 7),

            Map.entry(Operator.BITWISE_OR, 6),

            Map.entry(Operator.AND, 5),

            Map.entry(Operator.OR, 4),

            Map.entry(Operator.ASSIGN, 2),
            Map.entry(Operator.ASSIGN_PLUS, 2),
            Map.entry(Operator.ASSIGN_MINUS, 2),
            Map.entry(Operator.ASSIGN_MULTIPLY, 2),
            Map.entry(Operator.ASSIGN_DIVIDE, 2),
            Map.entry(Operator.ASSIGN_MODULO, 2));

    public static int getPriority(Operator operator) {
        return OPERATOR_PRIORITY_MAP.getOrDefault(operator, -1);
    }

    public static boolean isUnaryOperator(Operator operator) {
        return operator == Operator.NOT
                || operator == Operator.BITWISE_NOT
                || operator == Operator.INCREMENT
                || operator == Operator.DECREMENT
                || operator == Operator.PLUS 
                || operator == Operator.MINUS;
    }

    public static boolean isBinaryOperator(Operator operator) {
        return OPERATOR_PRIORITY_MAP.containsKey(operator);
    }
}