package game;

import engine.Application;
import game.entities.enemies.EnemyBee;
import game.entities.player.Player;

public class Game extends Application {
    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 700;
    public static final int DEFAULT_SPRITE_SCALE = 4;

    public static void main(String[] args) throws Exception {
        Game game = new Game();
        game.start();
    }

    public Game() {
        super("Galaga - @jules1univ", WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    @Override
    protected boolean init() {
        boolean setupEntities = this.ctx.entityManager
                .add(new Player(this.ctx))
                .add(new EnemyBee(this.ctx))
                .init();
        if (!setupEntities) {
            return false;
        }

        return true;
    }

    @Override
    protected void update(double dt) {
        this.ctx.entityManager.update(dt);

        if (this.ctx.input.isKeyDown(java.awt.event.KeyEvent.VK_ESCAPE)) {
            this.stop();
        }
    }

    @Override
    protected void draw() {
        this.ctx.entityManager.draw();
    }

}
