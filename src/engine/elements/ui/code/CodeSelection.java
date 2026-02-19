package engine.elements.ui.code;

public class CodeSelection {
    private final CodeState state;

    private boolean active;
    private TextPosition start;

    public CodeSelection(CodeState state) {
        this.state = state;

    }

    public void enable() {
        this.active = true;
        this.start = this.state.getCursor().getTextPosition().copy();
    }

    public void disable() {
        this.active = false;
    }

    public void replaceText(String newText) {
        if (!this.active) {
            return;
        }
        TextPosition end = this.state.getCursor().getTextPosition();

        this.state.getText().insert(newText, this.start, end);

        this.state.getCursor()
                .setTextPosition(this.state.getText().getTextPositionFromIndex(this.start.index() + newText.length()));

        this.disable();
    }

    public String getText() {
        if (!this.active) {
            return "";
        }
        TextPosition end = this.state.getCursor().getTextPosition();

        int startIndex = Math.min(this.start.index(), end.index());
        int endIndex = Math.max(this.start.index(), end.index());

        return this.state.getText().getContent().substring(startIndex, endIndex);
    }

    public TextPosition getStart() {
        return this.start;
    }

    public boolean isActive() {
        return this.active;
    }
}
