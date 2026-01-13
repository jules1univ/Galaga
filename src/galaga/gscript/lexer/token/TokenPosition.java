package galaga.gscript.lexer.token;

public final class TokenPosition {
    private final int line;
    private final int column;

    public static TokenPosition of(int line, int column) {
        return new TokenPosition(line, column);
    }

    private TokenPosition(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "(" + line + ", " + column + ")";
    }
}
