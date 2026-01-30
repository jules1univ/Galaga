package engine.elements.ui.code;

import java.util.List;

import engine.elements.ui.code.cursor.TextPosition;

public final class TextManager {

    private CodeContext context;

    public TextManager() {
    }

    public void init(CodeContext context) {
        this.context = context;
    }

    public String get(int lineIndex) {
        if (lineIndex < 0 || lineIndex >= this.context.lines().size()) {
            return "";
        }
        return this.context.lines().get(lineIndex);
    }

    public String getCursor() {
        return this.get(this.context.cursor().getPosition().line());
    }

    public String getCursor(int offset) {
        int lineIndex = this.context.cursor().getPosition().line() + offset;
        return this.get(lineIndex);
    }

    public void merge(int lineIndex, int offset) {
        if (lineIndex <= 0) {
            return;
        }

        String currentLine = this.context.lines().get(lineIndex);
        String mergedLine = this.context.lines().get(lineIndex + offset) + currentLine;

        update(lineIndex + offset, mergedLine);
        delete(lineIndex);
    }

    public void split(TextPosition position) {
        String line = this.context.lines().get(position.line());
        String newLine = line.substring(position.column());
        String remainingLine = line.substring(0, position.column());

        this.update(position.line(), remainingLine);
        this.insert(position.line() + 1, newLine);
    }

    public void delete(TextPosition position) {
        if (!this.context.isValidPosition(position)) {
            return;
        }

        String line = this.context.lines().get(position.line());
        if (position.column() == 0) {
            return;
        }
        line = line.substring(0, position.column() - 1) + line.substring(position.column());
        this.update(position.line(), line);
    }

    public void delete(int lineIndex) {
        if (this.context.lines().size() > 1) {
            this.context.lines().remove(lineIndex);
            // TODO:
            // highlightedthis.context.lines().remove(lineIndex);
        }
    }

    public void insert(TextPosition position, char ch) {
        if (!this.context.isValidPosition(position)) {
            return;
        }

        String line = this.context.lines().get(position.line());
        line = line.substring(0, position.column()) + ch + line.substring(position.column());
        this.update(position.line(), line);
    }

    public void insert(int lineIndex, String line) {
        this.context.lines().add(lineIndex, line);
        // TODO:
        // highlightedthis.context.lines().add(lineIndex, highlighter.line(text,
        // Color.WHITE));
    }

    public void insert(TextPosition position, List<String> insertLines) {
        int lineIndex = position.line();
        for (int i = 0; i < insertLines.size(); i++) {
            String insert = insertLines.get(i);

            if (i == 0) {
                String line = this.context.lines().get(lineIndex);
                line = line.substring(0, position.column()) + insert + line.substring(position.column());
                this.update(lineIndex, line);
                continue;
            }

            lineIndex++;
            this.insert(lineIndex, insert);
        }
    }

    private void update(int lineIndex, String text) {
        this.context.lines().set(lineIndex, text);
        this.context.highlighter().update(lineIndex, text);
    }

}