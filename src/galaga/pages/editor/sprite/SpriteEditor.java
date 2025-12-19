package galaga.pages.editor.sprite;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Map;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.text.Text;
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

    private Text back;
    private Text save;
    private Text infoColor;
    

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
            Map.entry(KeyEvent.VK_MINUS, 'D'),
            Map.entry(KeyEvent.VK_DELETE, 'N'));

    public SpriteEditor() {
        super(GalagaPage.EDITOR_SPRITE);
    }

    @Override
    public boolean onActivate() {
        int margin = 50;
        int marginLeft = 200;

        this.canvasSize = Size.of(Config.SIZE_SPRITE_CANVAS_EDITOR * this.canvasCellSize + 1,
                Config.SIZE_SPRITE_CANVAS_EDITOR * this.canvasCellSize + 1);
        this.canvasPosition = Position.of(marginLeft, margin);

        for (int i = 0; i < this.pixels.length; i++) {
            this.pixels[i] = 'N';
        }

        return true;
    }

    @Override
    public boolean onDeactivate() {
        this.state = PageState.INACTIVE;
        return true;
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

        for (var entry : this.keyToColor.entrySet()) {
            if (Galaga.getContext().getInput().isKeyPressed(entry.getKey())) {
                this.selectedColor = entry.getValue();
            }
        }

        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_SPACE)) {
            this.pixels[this.getCursorIndex()] = this.selectedColor;

        }

        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_ENTER)) {
            // switch ui element
            /*
             * 
             * Colorgrid Canvas
             * X X......
             * X ....X..
             * X ......X
             * 
             * BACK SAVE
             */

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

    }

}
