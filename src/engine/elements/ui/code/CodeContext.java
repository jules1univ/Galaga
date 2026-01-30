package engine.elements.ui.code;

import java.util.List;

import engine.elements.ui.code.cursor.CursorManager;
import engine.elements.ui.code.cursor.TextPosition;
import engine.elements.ui.code.highlighter.SyntaxHighlighter;
import engine.elements.ui.code.selection.SelectionManager;

public record CodeContext(
        CursorManager cursor,
        TextManager text,
        HistoryManager history,
        SelectionManager selection,
        InputManager input,
        CodeViewRenderer view,
        SyntaxHighlighter highlighter,
        List<String> lines) {

    public boolean isValidPosition(TextPosition position) {
        if (position.line() < 0 || position.line() >= lines.size()) {
            return false;
        }
        String line = lines.get(position.line());
        return position.column() >= 0 && position.column() <= line.length();
    }


}