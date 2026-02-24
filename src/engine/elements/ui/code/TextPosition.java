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

    public boolean isRangeInRange(TextPosition selfEnd, TextPosition rangeStart, TextPosition rangeEnd) {
        return selfEnd.index() >= rangeStart.index() && this.index <= rangeEnd.index();
    }

    public TextPosition copy() {
        return new TextPosition(this.line, this.column, this.index);
    }

    @Override
    public String toString() {
        return "column: " + this.column + ", line: " + this.line + ", index: " + this.index;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TextPosition other = (TextPosition) obj;
        return this.line == other.line && this.column == other.column && this.index == other.index;
    }
}

