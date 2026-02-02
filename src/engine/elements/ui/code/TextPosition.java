package engine.elements.ui.code;

public record TextPosition(int line, int column, int index) {
    
    public static TextPosition of(int line, int column, int index) {
        return new TextPosition(line, column, index);
    }

    
    public TextPosition copy() {
        return new TextPosition(this.line, this.column, this.index);
    }
}

