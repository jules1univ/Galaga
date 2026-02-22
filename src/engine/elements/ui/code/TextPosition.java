package engine.elements.ui.code;

public record TextPosition(int line, int column, int index) {
    
    public static TextPosition of(int line, int column, int index) {
        return new TextPosition(line, column, index);
    }

    
    public static boolean isBefore(TextPosition a, TextPosition b) {
        return a.index() < b.index();
    }

    public static boolean isAfter(TextPosition a, TextPosition b) {
        return a.index() > b.index();
    }

    public boolean isInRange(TextPosition start, TextPosition end) {
        return this.index >= start.index() && this.index <= end.index();
    }

    public TextPosition copy() {
        return new TextPosition(this.line, this.column, this.index);
    }
}

