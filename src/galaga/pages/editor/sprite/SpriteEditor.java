package galaga.pages.editor.sprite;

import java.awt.Color;
import java.awt.event.KeyEvent;

import engine.elements.page.Page;
import engine.elements.page.PageState;
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

    private Size paletteSize;
    private Position palettePosition;

    private Position cursor = Position.zero();

    public SpriteEditor() {
        super(GalagaPage.EDITOR_SPRITE);
    }

    @Override
    public boolean onActivate() {
        int margin = 50;
        int marginLeft = 200;

        this.canvasSize = Size.of(Config.SIZE_SPRITE_CANVAS_EDITOR * this.canvasCellSize + 1, Config.SIZE_SPRITE_CANVAS_EDITOR * this.canvasCellSize + 1);
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
                Position.of(
                        this.canvasPosition.getX()
                                + (int) (this.cursor.getX() / this.canvasCellSize) * this.canvasCellSize,
                        this.canvasPosition.getY()
                                + (int) (this.cursor.getY() / this.canvasCellSize) * this.canvasCellSize),
                Size.of(this.canvasCellSize, this.canvasCellSize),
                4,
                Color.RED);
    }

}
