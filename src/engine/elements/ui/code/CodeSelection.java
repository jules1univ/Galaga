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
        this.start = null;
    }

    public int replaceText(String newText) {
        if (!this.active) {
            return 0;
        }
        TextPosition end = this.state.getCursor().getTextPosition();

        int index = this.state.getText().insert(newText, this.start, end);
        this.disable();
        return index;
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
        if(!this.active) {
            return null;
        }

        int index = this.state.getCursor().getTextPosition().index();
        if (index < this.start.index()) {
            return this.state.getCursor().getTextPosition();
        }
        return this.start;
    }

    public TextPosition getEnd() {
        if(!this.active) {
            return null;
        }

        int index = this.state.getCursor().getTextPosition().index();
        if (index < this.start.index()) {
            return this.start;
        }
        return this.state.getCursor().getTextPosition();
    }

    public boolean isActive() {
        return this.active;
    }
}
