package game.entities.player;

import java.awt.event.KeyEvent;

import engine.entity.SpriteEntity;
import game.Config;
import game.Galaga;

public class Player extends SpriteEntity {

    private int life;
    private int score;
    private int medals;

    public Player() {
        super();
        this.life = Config.PLAYER_INITIAL_LIFE;
        this.scale = Config.DEFAULT_SPRITE_SCALE;

        // TODO: add medals when level is completed
        this.medals = 0;

        // TODO: when the method player.destroyEnemy(Enemy) is called add score
        this.score = 0;
        
        this.angle = 0.f;

        // TODO: create a ship animation when a new level begin
    }

    public int getLife() {
        return this.life;
    }

    public int getScore() {
        return this.score;
    }

    public int getMedals() {
        return this.medals;
    }
    
    @Override
    public boolean init() {
        this.sprite = this.loadFromSprite(Config.SHIP_SPRITE_NAME, Config.SHIP_PATH);

        this.x = (Galaga.getContext().getFrame().getWidth() - this.sprite.getWidth()) / 2;
        this.y = Galaga.getContext().getFrame().getHeight() - this.sprite.getHeight() - Config.FUD_HEIGHT;

        return this.sprite != null;
    }

    @Override
    public void update(double dt) {
        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_LEFT)) {
            this.x -= Config.PLAYER_SPEED * dt;
        }

        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_RIGHT)) {
            this.x += Config.PLAYER_SPEED * dt;
        }

        this.x = Math.clamp(this.x, this.sprite.getWidth(),
                Galaga.getContext().getFrame().getWidth() - this.sprite.getWidth());

        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_SPACE)) {
            // TODO: shoot bullets
        }
    }

    @Override
    public void draw() {
        super.draw();
    }

}
