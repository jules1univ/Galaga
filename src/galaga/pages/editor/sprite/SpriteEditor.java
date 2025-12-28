package galaga.pages.editor.sprite;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Map;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.text.Text;
import engine.elements.ui.text.TextPosition;
import engine.elements.ui.textarea.Textarea;
import engine.graphics.Renderer;
import engine.graphics.sprite.Sprite;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;
import galaga.pages.files.FileExplorerArgs;

public class SpriteEditor extends Page<GalagaPage> {

    private static final int SIZE = Config.SIZE_SPRITE_CANVAS_EDITOR;
    private static final int CELL = Config.SIZE_SPRITE_CANVAS_EDITOR_CELL;

    private static final Map<Character, Color> OPPOSITE_COLOR = new HashMap<>();

    static {
        for (char c : new char[] { 'W', 'B', 'R', 'Y', 'G', 'C', 'M', 'O', 'P', 'L' }) {
            Color col = Sprite.charToColor(c);
            OPPOSITE_COLOR.put(
                    c,
                    new Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue()));
        }
    }

    private final char[] pixels = new char[SIZE * SIZE];
    private boolean canvasDirty = true;

    private Size canvasSize;
    private Position canvasPosition;
    private Renderer canvasRenderer;

    private final Position cursor = Position.zero();
    private int cursorCellX;
    private int cursorCellY;
    private int cursorIndex;

    private char selectedColor = 'N';

    private Font titleFont;
    private Font textFont;

    private Text back;
    private Text save;
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

        this.canvasSize = Size.of(SIZE * CELL + 1, SIZE * CELL + 1);
        this.canvasPosition = Position.of(
                (Config.WINDOW_WIDTH - this.canvasSize.getWidth()) / 2f,
                padding);

        for (int i = 0; i < this.pixels.length; i++) {
            this.pixels[i] = 'N';
        }

        this.textFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_TEXT);
        this.titleFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_LARGE);
        if (this.textFont == null || this.titleFont == null) {
            return false;

        }
        this.info = new Textarea(
                "- ARROW KEYS to move this.cursor\n- SPACE to draw\n- KEYS 0-9 to select color\n- DELETE to erase\n- TAB to switch buttons\n- ENTER to confirm",
                Position.of(
                        padding,
                        this.canvasPosition.getY() + this.canvasSize.getHeight() + padding),
                Color.WHITE, this.textFont);
        if (!this.info.init()) {
            return false;

        }
        this.info.setCenter(TextPosition.BEGIN, TextPosition.BEGIN);

        this.back = new Text("BACK",
                Position.of(Config.WINDOW_WIDTH - padding, this.info.getPosition().getY()),
                Color.WHITE, this.titleFont);
        if (!this.back.init()) {
            return false;

        }
        this.back.setCenter(TextPosition.END, TextPosition.CENTER);

        this.save = new Text("SAVE",
                Position.of(Config.WINDOW_WIDTH - padding,
                        this.back.getPosition().getY() + this.back.getSize().getHeight() + padding),
                Color.WHITE, this.titleFont);
        if (!this.save.init()) {
            return false;

        }
        this.save.setCenter(TextPosition.END, TextPosition.BEGIN);

        this.canvasRenderer = Renderer.ofSub(this.canvasSize);
        this.canvasDirty = true;

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
        float move = CELL; // hack fix to make cursor move properly on different framerates

        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_UP)) {
            this.cursor.addY(-move);

        }
        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_DOWN)) {
            this.cursor.addY(move);

        }
        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_LEFT)) {
            this.cursor.addX(-move);

        }
        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_RIGHT)) {
            this.cursor.addX(move);
        }

        for (Entry<Integer, Character> e : this.keyToColor.entrySet()) {
            if (Galaga.getContext().getInput().isKeyPressed(e.getKey())) {
                this.selectedColor = e.getValue();
            }
        }

        this.updateCursor();

        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_SPACE)) {
            this.setPixel(this.cursorIndex, this.selectedColor);
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
                    Galaga.getContext().getApplication().setCurrentPage(GalagaPage.FILE_EXPLORER,
                            FileExplorerArgs.ofSaveMode(Config.PATH_CUSTOM_SHIPS, this.id));
                }
                default -> {
                }
            }
        }

        this.cursor.clampX(0, this.canvasSize.getWidth() - CELL / 2);
        this.cursor.clampY(0, this.canvasSize.getHeight() - CELL / 2);
    }

    private void updateCursor() {
        this.cursorCellX = Math.clamp((int) (this.cursor.getX() / CELL), 0, SIZE - 1);
        this.cursorCellY = Math.clamp((int) (this.cursor.getY() / CELL), 0, SIZE - 1);
        this.cursorIndex = this.cursorCellY * SIZE + this.cursorCellX;
    }

    private void setPixel(int index, char color) {
        if (this.pixels[index] == color) {
            return;
        }
        this.pixels[index] = color;
        this.canvasDirty = true;
    }

    private void rebuildCanvas() {
        this.canvasRenderer.begin();

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                char ch = this.pixels[y * SIZE + x];
                Color color = Sprite.charToColor(ch);
                if (ch == 'N') {
                    color = Color.BLACK;
                }
                this.canvasRenderer.drawRect(
                        Position.of(x * CELL, y * CELL),
                        Size.of(CELL, CELL),
                        color);
            }
        }

        this.canvasRenderer.drawGrid(Position.zero(), this.canvasSize, CELL, Color.WHITE);
        this.canvasRenderer.end();
        this.canvasDirty = false;
    }

    @Override
    public void draw(Renderer renderer) {
        if (this.canvasDirty) {
            this.rebuildCanvas();
        }
        renderer.draw(this.canvasRenderer, this.canvasPosition);

        Position cursorPos = Position.of(
                this.canvasPosition.getX() + this.cursorCellX * CELL,
                this.canvasPosition.getY() + this.cursorCellY * CELL);

        Color cursorColor = this.selectedColor == 'N'
                ? OPPOSITE_COLOR.get(this.pixels[this.cursorIndex])
                : Sprite.charToColor(this.selectedColor);

        Galaga.getContext().getRenderer().drawRectOutline(cursorPos, Size.of(CELL, CELL), 4, cursorColor);

        if (this.option == SpriteEditorOption.EDIT) {
            Galaga.getContext().getRenderer().drawRectOutline(this.canvasPosition, this.canvasSize, 2, Color.ORANGE);
        }

        this.info.draw(renderer);
        this.back.draw(renderer);
        this.save.draw(renderer);
    }

}
