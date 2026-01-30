package engine.elements.ui.code.selection;

import java.util.List;

import engine.elements.ui.code.cursor.TextPosition;

public record SelectRange(TextPosition start, TextPosition end) {

    public String getSelectedContent(List<String> lines) {
        if (lines.isEmpty()) {
            return "";
        }

        StringBuilder selection = new StringBuilder();

        for (int lineIndex = start.line(); lineIndex <= end.line(); lineIndex++) {
            String line = lines.get(lineIndex);
            int lineLength = line.length();

            int fromColumn = (lineIndex == start.line()) ? start.column() : 0;
            int toColumn = (lineIndex == end.line()) ? end.column() : lineLength;

            fromColumn = Math.max(0, Math.min(fromColumn, lineLength));
            toColumn = Math.max(0, Math.min(toColumn, lineLength));

            if (fromColumn < toColumn) {
                selection.append(line, fromColumn, toColumn);
            }

            if (lineIndex < end.line()) {
                selection.append("\n");
            }
        }

        return selection.toString();
    }

    public String getSelectedContent(String text) {
        return getSelectedContent(List.of(text.split("\n", -1)));
    }

    public List<String> getSelectedLines(List<String> lines) {
        if (lines.isEmpty()) {
            return List.of();
        }

        return lines.subList(start.line(), end.line() + 1);
    }

}