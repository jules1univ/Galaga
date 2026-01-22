package galaga.gscript.lexer.rules;

public enum Operator {
    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MODULO("%"),

    EQUALS("=="),
    NOT_EQUALS("!="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),

    AND("&&"),
    OR("||"),
    NOT("!"),

    BITWISE_AND("&"),
    BITWISE_OR("|"),
    BITWISE_XOR("^"),
    BITWISE_NOT("~"),

    INCREMENT("++"),
    DECREMENT("--"),

    ASSIGN("="),
    ASSIGN_PLUS("+="),
    ASSIGN_MINUS("-="),
    ASSIGN_MULTIPLY("*="),
    ASSIGN_DIVIDE("/="),
    ASSIGN_MODULO("%="),

    DOT("."),
    COMMA(","),
    SEMICOLON(";"),
    COLON(":"),
    LEFT_PAREN("("),
    RIGHT_PAREN(")"),
    LEFT_BRACE("{"),
    RIGHT_BRACE("}"),
    LEFT_BRACKET("["),
    RIGHT_BRACKET("]");

    private final String text;

    private Operator(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static Operator fromText(String text) {
        for (Operator operator : Operator.values()) {
            if (operator.getText().equals(text)) {
                return operator;
            }
        }
        return null;
    }

    public static boolean isOperator(String value) {
        for (Operator operator : Operator.values()) {
            if (operator.getText().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
