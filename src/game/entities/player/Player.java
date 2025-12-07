package game.entities.player;

import engine.elements.entity.SpriteEntity;
import engine.utils.Position;
import game.Config;
import game.Galaga;
import java.awt.event.KeyEvent;

public final class Player extends SpriteEntity {

    private int life;
    private int score;
    private int medals;

    public Player() {
        super();      
        this.angle = 0.f;
        this.scale = Config.SPRITE_SCALE_DEFAULT;
    
    
        this.life = Config.PLAYER_INITIAL_LIFE;
        this.medals = 0;
        this.score = 0;  
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
        this.sprite = Galaga.getContext().getResource().get(Config.SPRITE_SHIP);
        if (this.sprite == null) {
            return false;
        }
        this.size = this.sprite.getSize();
        this.position = Position.of(
                (Galaga.getContext().getFrame().getWidth() - this.getScaledSize().getWidth()) / 2,
                Galaga.getContext().getFrame().getHeight() - this.getScaledSize().getHeight() - Config.HEIGHT_FUD
        );

        // sprite will be recentered by default so no need to adjust position here
        this.position.addX(this.getScaledSize().getWidth() / 2);
        this.position.addY(this.getScaledSize().getHeight() / 2);
        
        return true;
    }

    @Override
    public void update(double dt) {
        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_LEFT)) {
            this.position.addX(-Config.SPEED_PLAYER * (float)dt);
        }

        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_RIGHT)) {
            this.position.addX(Config.SPEED_PLAYER * (float)dt);
        }

        this.position.clampX(
                this.size.getWidth(),
                Galaga.getContext().getFrame().getWidth() - this.size.getWidth()
        );

        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_SPACE)) {
            Galaga.getContext().getState().bullets.shoot(this);
        }
    }

    @Override
    public void draw() {
        super.draw();
    }

}
