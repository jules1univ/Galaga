package galaga.gscript.lexer.rules;

public enum Keyword {
    MODULE("module"),
    IMPORT("import"),
    EXTERN("extern"),

    TYPE("type"),
    STRUCT("struct"),
    ENUM("enum"),

    
    EXTENDS("extends"),
    RETURN("return"),

    IF("if"),
    ELSE("else"),
    SWITCH("switch"),
    CASE("case"),
    DO("do"),
    WHILE("while"),
    FOR("for"),
    BREAK("break"),
    CONTINUE("continue"),
    
    CONST("const"),
    REF("ref"),
    
    
    TRUE("true"),
    FALSE("false");

    private final String text;

    Keyword(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static boolean isKeyword(String value) {
        for (Keyword keyword : Keyword.values()) {
            if (keyword.getText().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
