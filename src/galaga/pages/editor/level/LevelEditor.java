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
import engine.utils.ini.Ini;
import engine.utils.ini.IniSection;
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

        this.levelCode = new CodeInput(Position.zero(), Size.of(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT * 0.8f),
                iniHighlighter, this.textFont);
        if (!this.levelCode.init()) {
            return false;
        }

        Ini levelTemplate = Ini.empty();
        
        IniSection levelSection =  levelTemplate.addSection("level");
        levelSection.set("name", "Level ?");
        levelSection.set("formation_speed", "?");
        levelSection.set("attack_cooldown", "?");

        IniSection formationSection = levelTemplate.addSection("formation");
        formationSection.set("layers", "?");
        formationSection.set("stages", "?");

        IniSection layerSection = levelTemplate.addSection("layer0");
        layerSection.set("type", "?");
        layerSection.set("speed", "?");
        layerSection.set("score", "?");
        layerSection.set("count", "?");


        this.levelCode.setText(levelTemplate.toString());
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
