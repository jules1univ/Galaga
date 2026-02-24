package engine.elements.ui.code;

import java.awt.Font;

import engine.elements.ui.code.highlighter.SyntaxHighlighter;

public class CodeState {
    public static final int HISTORY_MAX_SIZE = 100;
    public static final int HISTORY_RANGE = 10;

    public static final float CURSOR_WIDTH = 2.5f;
    public static final float CURSOR_BLINK_INTERVAL = 0.5f;
    public static final float CURSOR_HEIGHT_MULTIPLIER = 0.8f;

    public static final float LINE_SPACING = 4.f;
    public static final float LINE_NUMBER_PADDING_LEFT = 5;
    public static final float LINE_NUMBER_PADDING_RIGHT = 10;

    public static float TEXT_SPACE_SIZE = 10.f;

    public static final int SCROLL_LINE_GAP = 3;

    private final CodeEditor editor;
    private final SyntaxHighlighter highlighter;

    private final CodeView view;
    private final CodeCursor cursor;
    private final CodeInput input;
    private final CodeSelection selection;
    private final CodeText text;
    private final CodeHistory history;

    public CodeState(CodeEditor editor, SyntaxHighlighter highlighter, Font font) {
        this.editor = editor;
        this.highlighter = highlighter;

        this.input = new CodeInput(this);
        this.view = new CodeView(this, font);
        this.cursor = new CodeCursor(this, font);
        this.selection = new CodeSelection(this);
        this.text = new CodeText(this);
        this.history = new CodeHistory(this);
    }

    public CodeEditor getEditor() {
        return this.editor;
    }

    public SyntaxHighlighter getHighlighter() {
        return this.highlighter;
    }

    public CodeView getView() {
        return this.view;
    }

    public CodeCursor getCursor() {
        return this.cursor;
    }

    public CodeSelection getSelection() {
        return this.selection;
    }

    public CodeText getText() {
        return this.text;
    }

    public CodeHistory getHistory() {
        return this.history;
    }

    public CodeInput getInput() {
        return this.input;
    }
}
