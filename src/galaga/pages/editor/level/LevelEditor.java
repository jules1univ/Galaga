package galaga.pages.editor.level;

import java.awt.Color;
import java.awt.Font;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.codeinput.CodeInput;
import engine.elements.ui.codeinput.SyntaxHighlighter;
import engine.graphics.Renderer;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;

public class LevelEditor extends Page<GalagaPage> {

    private CodeInput levelCode;
    private Font textFont;

    public LevelEditor() {
        super(GalagaPage.EDITOR_LEVEL);
    }

    @Override
    public boolean onActivate() {

        this.textFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_TEXT);
        if (this.textFont == null) {
            return false;
        }

        SyntaxHighlighter highlighter = new SyntaxHighlighter();
        highlighter.addToken("[", Color.ORANGE);
        highlighter.addToken("]", Color.ORANGE);
        highlighter.addPattern("\\[.*?\\]", Color.LIGHT_GRAY);
        
        highlighter.addPattern("\\d+", Color.MAGENTA);

        highlighter.addToken("=", Color.RED);

        highlighter.addPattern("\".*?\"", Color.YELLOW);

        this.levelCode = new CodeInput(Position.zero(), Size.of(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT * 0.9f),
                highlighter, this.textFont);
        if (!this.levelCode.init()) {
            return false;
        }

        this.state = PageState.ACTIVE;
        return true;
    }

    @Override
    public boolean onDeactivate() {
        this.state = PageState.INACTIVE;
        return true;
    }

    @Override
    public void onReceiveArgs(Object... args) {
    }

    @Override
    public void update(float dt) {
        this.levelCode.update(dt);
    }

    @Override
    public void draw(Renderer renderer) {
        this.levelCode.draw(renderer);
    }

}
