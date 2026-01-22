package galaga.gscript.lexer.rules;

public enum Keyword {
    NATIVE("native"),

    RETURN("return"),

    FN("fn"),
    IF("if"),
    ELSE("else"),
    DO("do"),
    WHILE("while"),
    FOR("for"),
    BREAK("break"),
    CONTINUE("continue"),

    LET("let"),
    CONST("const"),

    TRUE("true"),
    FALSE("false");

    private final String text;

    private Keyword(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static Keyword fromText(String text) {
        for (Keyword keyword : Keyword.values()) {
            if (keyword.getText().equals(text)) {
                return keyword;
            }
        }
        return null;
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
