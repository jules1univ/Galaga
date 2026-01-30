package engine.elements.ui.code.selection;

import java.util.List;

import engine.elements.ui.code.CodeContext;
import engine.elements.ui.code.CodeEditor;
import engine.elements.ui.code.cursor.TextPosition;

public final class SelectionManager {

    private boolean active = false;
    private TextPosition starTextPosition = TextPosition.empty();
    private final CodeContext context;

    public SelectionManager(CodeEditor codeInput) {
        this.context = codeInput.getContext();
    }

    public void reset() {
        this.active = false;
    }

    public void enable(TextPosition position) {
        this.active = true;
        this.starTextPosition = position.copy();
    }

    public void disable() {
        this.active = false;
        this.starTextPosition = TextPosition.empty();
    }

    public boolean isActive() {
        return active;
    }

    public void replace(String replacement) {
        SelectRange range = this.range(this.context.cursor().getPosition());
        this.replace(range, replacement);
    }

    public void replace(SelectRange range, String replacement) {
        if (range.start().line() == range.end().line()) {
            String line = this.context.lines().get(range.start().line());
            line = line.substring(0, range.start().column()) + replacement + line.substring(range.end().column());
        } else {
            String firstLine = this.context.lines().get(range.start().line());
            String lastLine = this.context.lines().get(range.end().line());

            firstLine = firstLine.substring(0, range.start().column()) + replacement
                    + lastLine.substring(range.end().column());

            for (int i = range.end().line(); i > range.start().line(); i--) {
                this.context.text().delete(i);
            }
        }
    }

    public TextPosition start() {
        return this.starTextPosition;
    }

    public SelectRange range() {
        return new SelectRange(starTextPosition, this.context.cursor().getPosition());
    }

    public SelectRange range(TextPosition current) {
        return new SelectRange(starTextPosition, current);
    }

    public String text() {
        return this.range(this.context.cursor().getPosition()).getSelectedContent(this.context.lines());
    }

    public List<String> lines() {
        return this.range(this.context.cursor().getPosition()).getSelectedLines(this.context.lines());
    }

}