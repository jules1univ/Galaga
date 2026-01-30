package engine.elements.ui.code;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.elements.ui.code.cursor.CursorManager;
import engine.elements.ui.code.highlighter.SyntaxHighlighter;
import engine.elements.ui.code.selection.SelectionManager;
import engine.graphics.Renderer;
import engine.utils.Position;
import engine.utils.Size;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public final class CodeEditor extends UIElement {

    public static final float LINE_SPACE_HEIGHT = 0.f;
    public static final float LINE_SPACE_BEGIN = 10.f;

    private final Font font;

    private boolean focused = false;
    private int maxDisplayLines;
    private float lineBegin;
    private float lineHeight;

    private final CodeContext context;

    public CodeEditor(Position position, Size size, SyntaxHighlighter highlighter, Font font) {
        super();
        this.position = position.copy();
        this.size = size.copy();
        this.font = font;

        this.context = new CodeContext(
                new CursorManager(this, font, position),
                new TextManager(this),
                new HistoryManager(),
                new SelectionManager(this),
                new InputManager(this),
                new CodeViewRenderer(this),
                highlighter,
                new ArrayList<>());
    }

    public CodeContext getContext() {
        return this.context;
    }

    public void setText(String text) {
        this.context.cursor().resetScroll();
        this.context.cursor().resetPosition();
        this.context.selection().reset();
        this.context.view().markDirty();

        this.context.history().clear();
        this.context.lines().clear();

        this.context.lines().addAll(List.of(text.split("\n", -1)));

        this.updateLineBegin();
    }

    public String getText() {
        return String.join("\n", this.context.lines());
    }

    public List<String> getLines() {
        return this.context.lines();
    }

    public String getSelectedText() {
        return this.context.selection().text();
    }

    public List<String> getSelectedLines() {
        return this.context.selection().lines();
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        if (focused) {
            this.context.cursor().resetBlink();
        }
    }

    private void updateLineBegin() {
        int lineCount = Math.max(this.maxDisplayLines, this.context.lines().size());
        Size textLineSize = Application.getContext().getRenderer().getTextSize(Integer.toString(lineCount), this.font);
        this.lineBegin = LINE_SPACE_BEGIN + textLineSize.getWidth();

        this.context.view().setLineBegin(this.lineBegin);
        this.context.cursor().setLineBegin(this.lineBegin);
    }

    @Override
    public boolean init() {
        this.lineHeight = Application.getContext().getRenderer().getMaxCharSize(this.font).getHeight()
                + LINE_SPACE_HEIGHT;
        this.maxDisplayLines = Math.floorDiv((int) this.size.getHeight(), (int) this.lineHeight) - 1;

        this.context.view().init(this.size, this.font, this.lineHeight);
        this.context.cursor().init(this.maxDisplayLines, this.lineBegin, this.lineHeight);

        this.updateLineBegin();
        return true;
    }

    @Override
    public void update(float dt) {
        if (this.context.view().isDirty()) {
            this.updateLineBegin();
            this.context.view().rebuild();
        }

        if (!this.focused) {
            return;
        }

        this.context.input().update();
        this.context.cursor().update(dt);
    }

    @Override
    public void draw(Renderer renderer) {
        this.context.view().draw(renderer, this.position);
        if (this.focused && this.context.cursor().shouldDrawCursor()) {
            this.context.cursor().draw(renderer);
        }
    }
}