package engine.elements.ui.code;

import java.util.ArrayList;
import java.util.List;

public class CodeText {
    private final CodeState state;
    private List<String> lines = new ArrayList<>();
    private String content = "";

    public CodeText(CodeState state) {
        this.state = state;
    }

    public void setContent(String content) {
        this.lines.clear();

        this.content = content;
        this.lines.addAll(List.of(content.split("\n", -1)));

        this.state.getHistory().add(content);
        this.state.getCursor().resetBlink();
        this.state.getView().markDirty();
    }

    public String getContent() {
        return this.content;
    }

    public String getLineContent(int line) {
        if (line < 0 || line >= this.lines.size()) {
            return "";
        }

        return this.lines.get(line);
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

        return this.lines.get(line).length();
    }

    public TextPosition getTextPosition(int line, int column) {
        if (line < 0) {
            line = 0;
        } else if (line >= this.lines.size()) {
            line = this.lines.size() - 1;
        }

        if (column < 0) {
            column = 0;
        } else if (column > this.lines.get(line).length()) {
            column = this.lines.get(line).length();
        }

        int index = 0;
        for (int i = 0; i < line; i++) {
            index += this.lines.get(i).length() + 1;
        }
        index += column;
        return TextPosition.of(line, column, index);
    }

    public TextPosition getTextPositionFromIndex(int index) {
        if (index < 0) {
            index = 0;
        } else if (index > this.content.length()) {
            index = this.content.length();
        }

        int line = 0;
        int column = 0;
        for (int i = 0; i < index; i++) {
            if (this.content.charAt(i) == '\n') {
                line++;
                column = 0;
            } else {
                column++;
            }
        }

        return TextPosition.of(line, column, index);
    }

    public int getLineCount() {
        return this.lines.size();
    }
}
