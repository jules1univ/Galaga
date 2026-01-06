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

        SyntaxHighlighter iniHighlighter = new SyntaxHighlighter();

        iniHighlighter.addPattern("[;#].*", new Color(120, 120, 120));
        iniHighlighter.addPattern("\\[[^\\]]+\\]", Color.LIGHT_GRAY);
        iniHighlighter.addPattern("^[ \\t]*[a-zA-Z0-9_.-]+(?=\\s*=)", new Color(80, 160, 220));
        iniHighlighter.addPattern("=", Color.RED);
        iniHighlighter.addPattern("\"(\\\\.|[^\"])*\"", Color.YELLOW);
        iniHighlighter.addPattern("\\b\\d+\\b", Color.MAGENTA);
        iniHighlighter.addPattern("(?<=\\=).*", new Color(200, 200, 200));

        this.levelCode = new CodeInput(Position.zero(), Size.of(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT * 0.9f),
                iniHighlighter, this.textFont);
        if (!this.levelCode.init()) {
            return false;
        }

        // mini ini file as example
        this.levelCode.setText(
                "[Level]\n" +
                        "Name = \"First Level\"\n" +
                        "EnemyCount = 10\n" +
                        "SpawnRate = 2.5\n" +
                        "\n" +
                        "[Enemy1]\n" +
                        "Type = \"Bee\"\n" +
                        "PositionX = 100\n" +
                        "PositionY = 50\n" +
                        "\n" +
                        "[Enemy2]\n" +
                        "Type = \"Butterfly\"\n" +
                        "PositionX= 200\n" +
                        "PositionY=75\n");
        this.levelCode.setFocused(true);

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
