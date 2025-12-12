package galaga;

import engine.graphics.sprite.Sprite;
import galaga.entities.bullet.BulletManager;
import galaga.entities.player.Player;
import galaga.level.Level;

public class State {
    public Sprite shipSkin = null;
    public Level level = null;
    public Player player = null;
    public BulletManager bullets = null;
}
