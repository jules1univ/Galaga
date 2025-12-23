package galaga.pages.editor.sprite;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Map.Entry;
import java.util.Map;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.text.Text;
import engine.elements.ui.text.TextPosition;
import engine.elements.ui.textarea.Textarea;
import engine.graphics.sprite.Sprite;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;

public class SpriteEditor extends Page<GalagaPage> {

    private final char[] pixels = new char[Config.SIZE_SPRITE_CANVAS_EDITOR * Config.SIZE_SPRITE_CANVAS_EDITOR];

    private final int canvasCellSize = Config.SIZE_SPRITE_CANVAS_EDITOR_CELL;
    private Size canvasSize;
    private Position canvasPosition;

    private Position cursor = Position.zero();
    private char selectedColor = 'N';

    private Font titleFont;
    private Text back;
    private Text save;

    private Font textFont;
    private Textarea info;

    private SpriteEditorOption option = SpriteEditorOption.EDIT;

    private final Map<Integer, Character> keyToColor = Map.ofEntries(
            Map.entry(KeyEvent.VK_1, 'W'),
            Map.entry(KeyEvent.VK_2, 'B'),
            Map.entry(KeyEvent.VK_3, 'R'),
            Map.entry(KeyEvent.VK_4, 'Y'),
            Map.entry(KeyEvent.VK_5, 'G'),
            Map.entry(KeyEvent.VK_6, 'C'),
            Map.entry(KeyEvent.VK_7, 'M'),
            Map.entry(KeyEvent.VK_8, 'O'),
            Map.entry(KeyEvent.VK_9, 'P'),
            Map.entry(KeyEvent.VK_0, 'L'),
            Map.entry(KeyEvent.VK_BACK_SPACE, 'N'),
            Map.entry(KeyEvent.VK_DELETE, 'N'));

    public SpriteEditor() {
        super(GalagaPage.EDITOR_SPRITE);
    }

    @Override
    public boolean onActivate() {
        int padding = 50;

        this.canvasSize = Size.of(Config.SIZE_SPRITE_CANVAS_EDITOR * this.canvasCellSize + 1,
                Config.SIZE_SPRITE_CANVAS_EDITOR * this.canvasCellSize + 1);
        this.canvasPosition = Position.of((Config.WINDOW_WIDTH - this.canvasSize.getWidth()) / 2.f, padding);

        for (int i = 0; i < this.pixels.length; i++) {
            this.pixels[i] = 'N';
        }

        this.textFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_TEXT);
        if (this.textFont == null) {
            return false;
        }
        this.titleFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_LARGE);
        if (this.titleFont == null) {
            return false;
        }

        this.info = new Textarea(
                "- ARROW KEYS to move cursor\n- SPACE to draw\n- KEYS 0-9 to select color\n- DELETE to erase\n- TAB to switch buttons\n- ENTER to confirm",
                Position.of(
                        padding,
                        this.canvasPosition.getY() + this.canvasSize.getHeight() + padding),
                Color.WHITE, this.textFont);
        if (!this.info.init()) {
            return false;
        }
        this.info.setCenter(TextPosition.BEGIN, TextPosition.BEGIN);

        this.back = new Text("BACK", Position.of(
                Config.WINDOW_WIDTH - padding,
                this.info.getPosition().getY()), Color.WHITE, this.titleFont);
        if (!this.back.init()) {
            return false;
        }
        this.back.setCenter(TextPosition.END, TextPosition.CENTER);

        this.save = new Text("SAVE", Position.of(
                Config.WINDOW_WIDTH - padding,
                this.back.getPosition().getY() + this.back.getSize().getHeight() + padding), Color.WHITE,
                this.titleFont);
        if (!this.save.init()) {
            return false;
        }
        this.save.setCenter(TextPosition.END, TextPosition.BEGIN);

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

    private int getCursorIndex() {
        Position pos = this.getCursorPosition();
        int index = (int) (pos.getY() * Config.SIZE_SPRITE_CANVAS_EDITOR + pos.getX());
        if (index < 0 || index >= this.pixels.length) {
            return 0;
        }
        return index;
    }

    private Position getCursorPosition() {
        int x = (int) (this.cursor.getX() / this.canvasCellSize);
        int y = (int) (this.cursor.getY() / this.canvasCellSize);

        x = Math.clamp(x, 0, Config.SIZE_SPRITE_CANVAS_EDITOR - 1);
        y = Math.clamp(y, 0, Config.SIZE_SPRITE_CANVAS_EDITOR - 1);
        return Position.of(x, y);
    }

    public Color getOpositeColor(char c) {
        Color color = Sprite.charToColor(c);
        return new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
    }

    @Override
    public void update(float dt) {
        float move = Config.SPEED_CURSOR * dt;

        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_UP)) {
            this.cursor.addY(-move);
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_DOWN)) {
            this.cursor.addY(move);
        }

        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_LEFT)) {
            this.cursor.addX(-move);
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_RIGHT)) {
            this.cursor.addX(move);
        }

        for (Entry<Integer, Character> entry : this.keyToColor.entrySet()) {
            if (Galaga.getContext().getInput().isKeyPressed(entry.getKey())) {
                this.selectedColor = entry.getValue();
            }
        }

        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_SPACE)) {
            this.pixels[this.getCursorIndex()] = this.selectedColor;

        }

        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_TAB)) {
            switch (this.option) {
                case EDIT -> {
                    this.option = SpriteEditorOption.BACK;
                    this.back.setColor(Color.ORANGE);
                    this.save.setColor(Color.WHITE);
                }
                case BACK -> {
                    this.option = SpriteEditorOption.SAVE;
                    this.back.setColor(Color.WHITE);
                    this.save.setColor(Color.ORANGE);
                }
                case SAVE -> {
                    this.option = SpriteEditorOption.EDIT;
                    this.back.setColor(Color.WHITE);
                    this.save.setColor(Color.WHITE);
                }
            }
        }

        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_ENTER)) {
            switch (this.option) {
                case BACK -> {
                    Galaga.getContext().getApplication().setCurrentPage(GalagaPage.EDITOR_MENU);
                }
                case SAVE -> {
                    throw new UnsupportedOperationException("Save sprite not implemented yet.");
                }
                case EDIT -> {
                }
            }
        }

        this.cursor.clampX(0, this.canvasSize.getWidth() - this.canvasCellSize / 2);
        this.cursor.clampY(0, this.canvasSize.getHeight() - this.canvasCellSize / 2);
    }

    @Override
    public void draw() {
        Galaga.getContext().getRenderer().drawGrid(this.canvasPosition, this.canvasSize, this.canvasCellSize,
                Color.WHITE);

        for (int y = 0; y < Config.SIZE_SPRITE_CANVAS_EDITOR; y++) {
            for (int x = 0; x < Config.SIZE_SPRITE_CANVAS_EDITOR; x++) {
                char c = this.pixels[y * Config.SIZE_SPRITE_CANVAS_EDITOR + x];
                Galaga.getContext().getRenderer().drawRect(
                        Position.of(
                                this.canvasPosition.getX() + x * this.canvasCellSize + 1,
                                this.canvasPosition.getY() + y * this.canvasCellSize + 1),
                        Size.of(this.canvasCellSize - 1, this.canvasCellSize - 1),
                        Sprite.charToColor(c));
            }
        }
        Galaga.getContext().getRenderer().drawRectOutline(
                this.getCursorPosition().multiply(this.canvasCellSize).add(this.canvasPosition),
                Size.of(this.canvasCellSize, this.canvasCellSize),
                4,
                this.selectedColor == 'N' ? this.getOpositeColor(this.pixels[this.getCursorIndex()])
                        : Sprite.charToColor(this.selectedColor));

        if (this.selectedColor == 'N') {
            Galaga.getContext().getRenderer().drawLine(
                    Position.of(
                            this.canvasPosition.getX()
                                    + (int) (this.cursor.getX() / this.canvasCellSize) * this.canvasCellSize,
                            this.canvasPosition.getY()
                                    + (int) (this.cursor.getY() / this.canvasCellSize) * this.canvasCellSize),
                    Position.of(
                            this.canvasPosition.getX()
                                    + (int) (this.cursor.getX() / this.canvasCellSize + 1) * this.canvasCellSize,
                            this.canvasPosition.getY()
                                    + (int) (this.cursor.getY() / this.canvasCellSize + 1) * this.canvasCellSize),
                    this.getOpositeColor(this.pixels[this.getCursorIndex()]),
                    4.f);

            Galaga.getContext().getRenderer().drawLine(
                    Position.of(
                            this.canvasPosition.getX()
                                    + (int) (this.cursor.getX() / this.canvasCellSize + 1) * this.canvasCellSize,
                            this.canvasPosition.getY()
                                    + (int) (this.cursor.getY() / this.canvasCellSize) * this.canvasCellSize),

                    Position.of(
                            this.canvasPosition.getX()
                                    + (int) (this.cursor.getX() / this.canvasCellSize) * this.canvasCellSize,
                            this.canvasPosition.getY()
                                    + (int) (this.cursor.getY() / this.canvasCellSize + 1) * this.canvasCellSize),
                    this.getOpositeColor(this.pixels[this.getCursorIndex()]),
                    4.f);
        }

        if (this.option == SpriteEditorOption.EDIT) {
            Galaga.getContext().getRenderer().drawRectOutline(
                    this.canvasPosition,
                    this.canvasSize,
                    2,
                    Color.ORANGE);
        }

        this.info.draw();
        this.back.draw();
        this.save.draw();
    }

}
