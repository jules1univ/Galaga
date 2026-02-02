package galaga.pages.editor.level;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.Alignment;
import engine.elements.ui.code.CodeEditor;
import engine.elements.ui.code.highlighter.defaults.RegexSyntaxHighlighter;
import engine.elements.ui.text.Text;
import engine.graphics.Renderer;
import engine.resource.ResourceAlias;
import engine.utils.Position;
import engine.utils.Size;
import engine.utils.ini.Ini;
import engine.utils.ini.IniSection;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;
import galaga.level.LevelResource;
import galaga.pages.files.FileExplorerArgs;
import galaga.pages.files.FileExplorerResult;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

public class LevelEditor extends Page<GalagaPage> {

    private CodeEditor levelCode;
    private Font textFont;
    private Font titleFont;

    private Text open;
    private Text back;
    private Text save;
    private Text test;

    private LevelEditorOption option;

    public LevelEditor() {
        super(GalagaPage.EDITOR_LEVEL);
    }

    private void updateEditorMenu() {
        switch (this.option) {
            case OPEN -> {
                this.open.setColor(Color.ORANGE);

                this.levelCode.setFocused(false);
                this.back.setColor(Color.WHITE);
                this.save.setColor(Color.WHITE);
                this.test.setColor(Color.WHITE);
            }
            case SAVE -> {
                this.save.setColor(Color.ORANGE);

                this.levelCode.setFocused(false);
                this.open.setColor(Color.WHITE);
                this.back.setColor(Color.WHITE);
                this.test.setColor(Color.WHITE);
            }
            case BACK -> {
                this.back.setColor(Color.ORANGE);

                this.levelCode.setFocused(false);
                this.open.setColor(Color.WHITE);
                this.save.setColor(Color.WHITE);
                this.test.setColor(Color.WHITE);
            }
            case TEST -> {
                this.test.setColor(Color.ORANGE);

                this.levelCode.setFocused(false);
                this.open.setColor(Color.WHITE);
                this.save.setColor(Color.WHITE);
                this.back.setColor(Color.WHITE);
            }
            case EDIT -> {
                this.levelCode.setFocused(true);

                this.open.setColor(Color.WHITE);
                this.back.setColor(Color.WHITE);
                this.save.setColor(Color.WHITE);
                this.test.setColor(Color.WHITE);
            }
        }
    }

    private boolean exportLevel(String filename, String path) {
        String content = this.levelCode.getContent();
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(content.getBytes());
            fos.flush();

            String filenameNoExt = filename.replaceAll("\\.\\w+$", "");
            
            int aliasId = 0;
            String aliasName = filenameNoExt;
            while (ResourceAlias.exists(aliasName)) {
                aliasId++;
                aliasName = filenameNoExt + "_" + aliasId;
            }

            ResourceAlias alias = ResourceAlias.file(aliasName,
                    path,
                    null);
            Config.LEVELS_CUSTOM.add(alias);
            Galaga.getContext().getResource().add(alias, LevelResource.NAME);
            Galaga.getContext().getResource().load(alias);
            return true;
        } catch (IOException e) {
            Log.error("Level saving failed: %s", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onActivate() {

        this.textFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_TEXT);
        if (this.textFont == null) {
            return false;
        }

        this.titleFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_LARGE);
        if (this.titleFont == null) {
            return false;
        }

        RegexSyntaxHighlighter iniHighlighter = new RegexSyntaxHighlighter(Color.WHITE);

        iniHighlighter.addPattern("[;#].*", new Color(120, 120, 120));
        iniHighlighter.addPattern("\\[[^\\]]+\\]", Color.LIGHT_GRAY);
        iniHighlighter.addPattern("^[ \\t]*[a-zA-Z0-9_.-]+(?=\\s*=)", new Color(80, 160, 220));
        iniHighlighter.addPattern("=", Color.RED);
        iniHighlighter.addPattern("\"(\\\\.|[^\"])*\"", Color.YELLOW);
        iniHighlighter.addPattern("\\b\\d+\\b", Color.MAGENTA);
        iniHighlighter.addPattern("(?<=\\=).*", new Color(200, 200, 200));

        this.levelCode = new CodeEditor(Position.zero(), Size.of(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT * 0.8f),
                iniHighlighter, this.textFont);
        if (!this.levelCode.init()) {
            return false;
        }

        Ini levelTemplate = Ini.empty();

        IniSection levelSection = levelTemplate.addSection("level");
        levelSection.set("name", "Level ?");
        levelSection.set("formation_speed", "?");
        levelSection.set("missile_cooldown", "?");
        levelSection.set("attack_cooldown", "?");

        IniSection formationSection = levelTemplate.addSection("formation");
        formationSection.set("layers", "?");
        formationSection.set("stages", "?");

        IniSection layerSection = levelTemplate.addSection("layer0");
        layerSection.set("type", "?");
        layerSection.set("speed", "?");
        layerSection.set("score", "?");
        layerSection.set("count", "?");

        this.levelCode.setContent(levelTemplate.toString());
        this.levelCode.setFocused(true);

        int margin = 50;
        this.open = new Text("OPEN",
                Position.of(this.levelCode.getPosition().getX() + margin,
                        this.levelCode.getPosition().getY() + this.levelCode.getSize().getHeight() + margin),
                Color.WHITE, this.titleFont);
        if (!this.open.init()) {
            return false;
        }
        this.open.setCenter(Alignment.BEGIN, Alignment.BEGIN);

        this.back = new Text("BACK",
                Position.of(this.levelCode.getPosition().getX() + margin,
                        this.open.getPosition().getY() + this.open.getSize().getHeight() + margin/8.f),
                Color.WHITE, this.titleFont);
        if (!this.back.init()) {
            return false;
        }
        this.back.setCenter(Alignment.BEGIN, Alignment.BEGIN);

        this.save = new Text("SAVE",
                Position.of(Config.WINDOW_WIDTH - margin,
                        this.levelCode.getPosition().getY() + this.levelCode.getSize().getHeight() + margin),
                Color.WHITE, this.titleFont);
        if (!this.save.init()) {
            return false;
        }
        this.save.setCenter(Alignment.END, Alignment.BEGIN);

        this.test = new Text("TEST",
                Position.of(Config.WINDOW_WIDTH - margin,
                        this.open.getPosition().getY() + this.open.getSize().getHeight() + margin/8.f),
                Color.WHITE, this.titleFont);
        if (!this.test.init()) {
            return false;
        }
        this.test.setCenter(Alignment.END, Alignment.BEGIN);

        this.option = LevelEditorOption.EDIT;

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
        if (args == null || args.length != 1) {
            return;
        }

        if (!(args[0] instanceof FileExplorerResult)) {
            return;
        }

        FileExplorerResult result = (FileExplorerResult) args[0];
        Optional<String> contentOpt = result.getFileContent();
        if (contentOpt.isEmpty()) {
            return;
        }
        this.levelCode.setContent(contentOpt.get());
    }

    @Override
    public void update(float dt) {
        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_TAB)) {
            switch (this.option) {
                case EDIT -> this.option = LevelEditorOption.OPEN;
                case OPEN -> this.option = LevelEditorOption.SAVE;
                case SAVE -> this.option = LevelEditorOption.TEST;
                case TEST -> this.option = LevelEditorOption.BACK;
                case BACK -> this.option = LevelEditorOption.EDIT;
            }
            this.updateEditorMenu();
        }

        if (Galaga.getContext().getInput().isKeyPressedNoConsume(KeyEvent.VK_ENTER)) {
            switch (this.option) {
                case OPEN -> {
                    Galaga.getContext().getApplication().setCurrentPage(GalagaPage.FILE_EXPLORER,
                            FileExplorerArgs.ofOpenMode(Config.PATH_CUSTOM_LEVELS, this.id));
                }
                case SAVE -> {
                    int aliasId = 0;
                    String filename = "custom_level_" + aliasId;
                    while (ResourceAlias.exists(filename)) {
                        aliasId++;
                        filename = "custom_level_" + aliasId;
                    }
                    Galaga.getContext().getApplication().setCurrentPage(GalagaPage.FILE_EXPLORER,
                            FileExplorerArgs.ofSaveMode(Config.PATH_CUSTOM_LEVELS, filename + ".lvl", this.id,
                                    GalagaPage.EDITOR_MENU, this::exportLevel),
                            this.levelCode.getContent());

                }
                case TEST -> {
                    // TODO: test level
                }
                case BACK -> {
                    Galaga.getContext().getApplication().setCurrentPage(GalagaPage.EDITOR_MENU);
                    Galaga.getContext().getInput().resetPressedKeys();
                }
                case EDIT -> {
                }
            }
        }

        this.levelCode.update(dt);
    }

    @Override
    public void draw(Renderer renderer) {
        this.levelCode.draw(renderer);

        renderer.drawLine(
                Position.of(
                        this.levelCode.getPosition().getX(),
                        this.levelCode.getPosition().getY() + this.levelCode.getSize().getHeight() + 10),
                Position.of(
                        this.levelCode.getPosition().getX() + this.levelCode.getSize().getWidth(),
                        this.levelCode.getPosition().getY() + this.levelCode.getSize().getHeight() + 10),
                Color.WHITE,
                2.f);

        this.open.draw(renderer);
        this.back.draw(renderer);

        this.save.draw(renderer);
        this.test.draw(renderer);
    }

}
