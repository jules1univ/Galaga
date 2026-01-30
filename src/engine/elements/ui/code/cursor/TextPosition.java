package engine.elements.ui.code.cursor;

public record TextPosition(int line, int column) {
    public static TextPosition empty() {
        return new TextPosition(-1, -1);
    }

    public static TextPosition start() {
        return new TextPosition(0, 0);
    }

    public TextPosition copy() {
        return new TextPosition(this.line, this.column);
    }

    public boolean isZero() {
        return this.line == 0 && this.column == 0;
    }
}
