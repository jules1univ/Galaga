package galaga;

import engine.graphics.sprite.Sprite;
import galaga.entities.bullet.BulletManager;
import galaga.entities.player.Player;
import galaga.level.Level;

public class State {
    public Sprite shipSkin;
    
    public Level level;
    public Player player;
    public BulletManager bullets;
}
