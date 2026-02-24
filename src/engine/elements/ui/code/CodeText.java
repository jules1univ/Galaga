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
        content = content.replace("\r\n", "\n").replace("\r", "\n");
        content = content.replace("\t", " ".repeat(2));
        
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


    public String getContent(int from, int to) {
        if (from < 0) {
            from = 0;
        } else if (from > this.content.length()) {
            from = this.content.length();
        }

        if (to < 0) {
            to = 0;
        } else if (to > this.content.length()) {
            to = this.content.length();
        }

        return this.content.substring(from, to);
    }

    public String getContent(TextPosition start, TextPosition end) {
        int startIndex = Math.min(start.index(), end.index());
        int endIndex = Math.max(start.index(), end.index());
        return this.getContent(startIndex, endIndex);
    }

    public String getLineContent(int line) {
        if (line < 0 || line >= this.lines.size()) {
            return "";
        }

        return this.lines.get(line);
    }

    public int insert(String newText, TextPosition start, TextPosition end) {
        String before = this.content.substring(0, start.index());
        String after = this.content.substring(end.index());
        this.setContent(before + newText + after);

        return before.length() + newText.length();
    }

    public int insert(char ch) {
        return this.insert(String.valueOf(ch),
                this.state.getCursor().getTextPosition(),
                this.state.getCursor().getTextPosition());
    }

    public int delete() {
        if (this.state.getCursor().getTextPosition().index() == 0) {
            return -1;
        }
        
        String before = this.content.substring(0, this.state.getCursor().getTextPosition().index() - 1);
        String after = this.content.substring(this.state.getCursor().getTextPosition().index());
        this.setContent(before + after);
        return before.length();
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
