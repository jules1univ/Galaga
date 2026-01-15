package galaga.gscript.lexer.rules;

import java.util.Map;

public final class OperatorPriority {
    public static final int MIN_PRIORITY = 1;
    public static final int MAX_PRIORITY = 7;
    public static final Map<Operator, Integer> OPERATOR_PRIORITY_MAP = Map.ofEntries(
            Map.entry(Operator.ASSIGN, 1),
            Map.entry(Operator.OR, 2),
            Map.entry(Operator.AND, 3),
            Map.entry(Operator.EQUALS, 4),
            Map.entry(Operator.NOT_EQUALS, 4),
            Map.entry(Operator.LESS_THAN, 5),
            Map.entry(Operator.LESS_THAN_OR_EQUAL, 5),
            Map.entry(Operator.GREATER_THAN, 5),
            Map.entry(Operator.GREATER_THAN_OR_EQUAL, 5),
            Map.entry(Operator.PLUS, 6),
            Map.entry(Operator.MINUS, 6),
            Map.entry(Operator.MULTIPLY, 7),
            Map.entry(Operator.DIVIDE, 7),
            Map.entry(Operator.MODULO, 7));

    public static int getPriority(Operator operator) {
        return OPERATOR_PRIORITY_MAP.getOrDefault(operator, -1);
    }

   
}
