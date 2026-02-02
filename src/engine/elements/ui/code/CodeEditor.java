package engine.elements.ui.code;

import java.awt.Font;

import engine.elements.ui.UIElement;
import engine.elements.ui.code.highlighter.SyntaxHighlighter;
import engine.graphics.Renderer;
import engine.utils.Position;
import engine.utils.Size;

public class CodeEditor extends UIElement {
    private final CodeState state;
    private boolean focused;

    public CodeEditor(Position position, Size size, SyntaxHighlighter highlighter, Font font) {
        this.position = position;
        this.size = size;

        this.state = new CodeState(this, highlighter, font);
    }

    public void setContent(String content) {
        this.state.getText().setContent(content);
    }

    public String getContent() {
        return this.state.getText().getContent();
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean init() {
        return this.state.getCursor().init() &&
                this.state.getView().init() &&
                this.state.getInput().init();
    }

    @Override
    public void update(float dt) {
        if (this.focused) {
            this.state.getInput().update(dt);
        }
        this.state.getCursor().update(dt);
        this.state.getView().update(dt);
    }

    @Override
    public void draw(Renderer renderer) {
        this.state.getView().draw(renderer);
        this.state.getCursor().draw(renderer);
    }
}
