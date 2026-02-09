package galaga.pages.editor.enemy;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.Alignment;
import engine.elements.ui.code.CodeEditor;
import engine.elements.ui.code.highlighter.defaults.RegexSyntaxHighlighter;
import engine.elements.ui.text.Text;
import engine.graphics.Renderer;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;
import galaga.gscript.lexer.rules.Keyword;
import galaga.pages.files.FileExplorerResult;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Optional;

public class EnemyEditor extends Page<GalagaPage> {

    private CodeEditor script;
    private Font textFont;
    private Font titleFont;

    private Text open;
    private Text back;
    private Text save;
    private Text test;

    private EnemyEditorOption option;

    public EnemyEditor() {
        super(GalagaPage.EDITOR_ENEMY);
    }

    private void updateEditorMenu() {
        switch (this.option) {
            case OPEN -> {
                this.open.setColor(Color.ORANGE);

                this.script.setFocused(false);
                this.back.setColor(Color.WHITE);
                this.save.setColor(Color.WHITE);
                this.test.setColor(Color.WHITE);
            }
            case SAVE -> {
                this.save.setColor(Color.ORANGE);

                this.script.setFocused(false);
                this.open.setColor(Color.WHITE);
                this.back.setColor(Color.WHITE);
                this.test.setColor(Color.WHITE);
            }
            case BACK -> {
                this.back.setColor(Color.ORANGE);

                this.script.setFocused(false);
                this.open.setColor(Color.WHITE);
                this.save.setColor(Color.WHITE);
                this.test.setColor(Color.WHITE);
            }
            case TEST -> {
                this.test.setColor(Color.ORANGE);

                this.script.setFocused(false);
                this.open.setColor(Color.WHITE);
                this.save.setColor(Color.WHITE);
                this.back.setColor(Color.WHITE);
            }
            case EDIT -> {
                this.script.setFocused(true);

                this.open.setColor(Color.WHITE);
                this.back.setColor(Color.WHITE);
                this.save.setColor(Color.WHITE);
                this.test.setColor(Color.WHITE);
            }
        }
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

        RegexSyntaxHighlighter gscriptHighlighter = new RegexSyntaxHighlighter(Color.WHITE);
        gscriptHighlighter.addPattern("\\b(" + String.join("|",
                Arrays.stream(Keyword.values()).map(Enum::name).toArray(String[]::new)).toLowerCase() + ")\\b",
                Color.RED);
        gscriptHighlighter.addPattern(
                "==|!=|<=|>=|\\+|-|\\*|/|=|<|>",
                Color.PINK);
        gscriptHighlighter.addPattern(
                "\\bfn\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\b",
                Color.BLUE);
        gscriptHighlighter.addPattern(
                "\\b([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(",
                Color.MAGENTA);
        gscriptHighlighter.addPattern(
                "\"([^\"\\\\]|\\\\.)*\"",
                Color.YELLOW);
        gscriptHighlighter.addPattern(
                "\\b\\d+\\.\\d+\\b",
                Color.CYAN);
        gscriptHighlighter.addPattern(
                "[(){}\\[\\],;]",
                Color.LIGHT_GRAY);

        gscriptHighlighter.addPattern(
                "//.*",
                Color.GREEN);
        gscriptHighlighter.addPattern(
                "/\\*[\\s\\S]*?\\*/",
                Color.GREEN);

        this.script = new CodeEditor(Position.zero(), Size.of(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT * 0.8f),
                gscriptHighlighter, this.textFont);
        if (!this.script.init()) {
            return false;
        }
        this.script.setFocused(true);
        this.script.setContent("// Write your enemy script here...\n");

        int margin = 50;
        this.open = new Text("OPEN",
                Position.of(this.script.getPosition().getX() + margin,
                        this.script.getPosition().getY() + this.script.getSize().getHeight() + margin),
                Color.WHITE, this.titleFont);
        if (!this.open.init()) {
            return false;
        }
        this.open.setCenter(Alignment.BEGIN, Alignment.BEGIN);

        this.back = new Text("BACK",
                Position.of(this.script.getPosition().getX() + margin,
                        this.open.getPosition().getY() + this.open.getSize().getHeight() + margin / 8.f),
                Color.WHITE, this.titleFont);
        if (!this.back.init()) {
            return false;
        }
        this.back.setCenter(Alignment.BEGIN, Alignment.BEGIN);

        this.save = new Text("SAVE",
                Position.of(Config.WINDOW_WIDTH - margin,
                        this.script.getPosition().getY() + this.script.getSize().getHeight() + margin),
                Color.WHITE, this.titleFont);
        if (!this.save.init()) {
            return false;
        }
        this.save.setCenter(Alignment.END, Alignment.BEGIN);

        this.test = new Text("TEST",
                Position.of(Config.WINDOW_WIDTH - margin,
                        this.open.getPosition().getY() + this.open.getSize().getHeight() + margin / 8.f),
                Color.WHITE, this.titleFont);
        if (!this.test.init()) {
            return false;
        }
        this.test.setCenter(Alignment.END, Alignment.BEGIN);

        this.option = EnemyEditorOption.EDIT;
        this.updateEditorMenu();

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
        this.script.setContent(contentOpt.get());
    }

    @Override
    public void update(float dt) {
        if (Galaga.getContext().getInput().isKeyPressed(
            Galaga.getContext().getState().keyboard.getKey("menu_navigate").orElse(KeyEvent.VK_TAB)
        )) {
            switch (this.option) {
                case EDIT -> this.option = EnemyEditorOption.OPEN;
                case OPEN -> this.option = EnemyEditorOption.SAVE;
                case SAVE -> this.option = EnemyEditorOption.TEST;
                case TEST -> this.option = EnemyEditorOption.BACK;
                case BACK -> this.option = EnemyEditorOption.EDIT;
            }
            this.updateEditorMenu();
        }

        if (Galaga.getContext().getInput().isKeyPressedNoConsume(
            Galaga.getContext().getState().keyboard.getKey("menu_confirm").orElse(KeyEvent.VK_ENTER)
        )) {
            switch (this.option) {
                case OPEN -> {
                    // Open file explorer
                }
                case SAVE -> {
                    // Save enemy script
                }
                case TEST -> {
                    // TODO: test enemy & compile script
                }
                case BACK -> {
                    Galaga.getContext().getApplication().setCurrentPage(GalagaPage.EDITOR_MENU);
                    Galaga.getContext().getInput().resetPressedKeys();
                }
                case EDIT -> {
                }
            }
        }

        this.script.update(dt);
    }

    @Override
    public void draw(Renderer renderer) {
        this.script.draw(renderer);

        renderer.drawLine(
                Position.of(
                        this.script.getPosition().getX(),
                        this.script.getPosition().getY() + this.script.getSize().getHeight() + 10),
                Position.of(
                        this.script.getPosition().getX() + this.script.getSize().getWidth(),
                        this.script.getPosition().getY() + this.script.getSize().getHeight() + 10),
                Color.WHITE,
                2.f);

        this.open.draw(renderer);
        this.back.draw(renderer);

        this.save.draw(renderer);
        this.test.draw(renderer);
    }

}
