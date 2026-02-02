package engine.elements.ui.code;

import java.util.ArrayList;
import java.util.List;

public class CodeText {
    private final CodeState state;

    private String content;
    private List<TextPosition> lines = new ArrayList<>();

    public CodeText(CodeState state) {
        this.state = state;

        this.content = "";
    }

    public void setContent(String content) {
        this.content = content;

        this.lines.clear();

        int line = 0;
        int column = 0;
        for (int i = 0; i < content.length(); i++) {

            if (content.charAt(i) == '\n') {
                this.lines.add(TextPosition.of(line, column, i));
                line++;
                column = 0;
            }
            column++;
        }

        this.state.getHistory().add(this.content);
        this.state.getCursor().resetBlink();
        this.state.getView().markDirty();
    }

    public String getContent() {
        System.out.println("Getting content: " + this.content);
        return this.content;
    }

    public void insert(String newText, TextPosition start, TextPosition end) {
        String before = this.content.substring(0, start.index());
        String after = this.content.substring(end.index());

        this.setContent(before + newText + after);
        this.state.getCursor().setTextPosition(this.getTextPositionFromIndex(start.index() + newText.length()));
    }

    public void insert(char ch) {
        this.insert(String.valueOf(ch),
                this.state.getCursor().getTextPosition(),
                this.state.getCursor().getTextPosition());
    }
    
    public void delete() {
        if (this.state.getCursor().getTextPosition().index() == 0) {
            return;
        }

        char ch = this.content.charAt(this.state.getCursor().getTextPosition().index() - 1);
        if (ch == '\n') {
            this.lines.remove(this.state.getCursor().getTextPosition().line());
        }
        String before = this.content.substring(0, this.state.getCursor().getTextPosition().index() - 1);
        String after = this.content.substring(this.state.getCursor().getTextPosition().index());
        this.setContent(before + after);

        this.state.getCursor().setTextPosition(this.getTextPositionFromIndex(
                this.state.getCursor().getTextPosition().index() - 1));
    }

    public int getLineLength(int line) {
        if (line < 0 || line >= this.lines.size()) {
            return 0;
        }

        TextPosition linePos = this.lines.get(line);
        int lineStartIndex = linePos.index();

        int lineEndIndex = this.content.length();
        if (line + 1 < this.lines.size()) {
            lineEndIndex = this.lines.get(line + 1).index();
        }

        return lineEndIndex - lineStartIndex;
    }

    public TextPosition getTextPosition(int line, int column) {
        line = Math.clamp(line, 0, this.lines.size() - 1);

        TextPosition linePos = this.lines.get(line);
        int lineStartIndex = linePos.index();

        int lineEndIndex = this.content.length();
        if (line + 1 < this.lines.size()) {
            lineEndIndex = this.lines.get(line + 1).index();
        }
        int lineLength = lineEndIndex - lineStartIndex;

        column = Math.clamp(column, 0, lineLength);
        return TextPosition.of(line, column, lineStartIndex + column);
    }

    public TextPosition getTextPositionFromIndex(int index) {
        index = Math.clamp(index, 0, this.content.length());

        int low = 0;
        int high = this.lines.size() - 1;
        int line = 0;
        int column = 0;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            TextPosition midPos = this.lines.get(mid);

            if (midPos.index() <= index) {
                line = midPos.line();
                column = index - midPos.index();
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return TextPosition.of(line, column, index);
    }

    public List<TextPosition> getLines() {
        return this.lines;
    }
}
