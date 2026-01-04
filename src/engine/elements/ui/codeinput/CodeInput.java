package engine.elements.ui.codeinput;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.graphics.Renderer;
import engine.input.InputKeyListener;
import engine.utils.Pair;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Galaga;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public final class CodeInput extends UIElement {

    private static final float CURSOR_WIDTH = 3f;

    private final Font font;
    private Color color;
    private Color background;

    private final List<String> lines = new ArrayList<>();
    private int lineIndex = 0;
    private int colIndex = 0;

    private int maxDisplayLines;

    private boolean focused = false;

    private Renderer viewRenderer;
    private boolean isDirty = true;

    private final SyntaxHighlighter highlighter;
    private final List<Pair<String, Color>[]> highlightedLines = new ArrayList<>();

    public CodeInput(Position position, Size size,SyntaxHighlighter highlighter, Font font) {
        super();
        this.position = position.copy();
        this.size = size.copy();
        this.font = font;
        this.highlighter = highlighter;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }


    private void rebuildLinesView() {
        
        this.isDirty = false;
    }

    @Override
    public boolean init() {
        this.lines.add("");

        int textHeight = Galaga.getContext().getRenderer().getTextSize("X", this.font).getIntHeight();
        this.maxDisplayLines = (int) (this.size.getHeight() / textHeight);

        this.viewRenderer = Renderer.ofSub(this.size);

        return true;
    }

    @Override
    public void update(float dt) {
        if (!this.focused) {
            return;
        }

        if (this.isDirty) {
            this.rebuildLinesView();
        }
    }

    @Override
    public void draw(Renderer renderer) {
        
        renderer.draw(this.viewRenderer, this.position);

        if(this.focused) {
            // Draw cusror
        }
    }
}
