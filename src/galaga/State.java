package galaga;

import galaga.entities.bullet.BulletManager;
import galaga.entities.player.Player;
import galaga.level.Level;

public class State {
    public int score;

    public Level level;
    public Player player;
    public BulletManager bullets;
}
