package game;

import game.entities.bullet.BulletManager;
import game.entities.player.Player;
import game.level.Level;

public class State {
    public int bestScore;
    public int score;

    public Level level;
    public Player player;
    public BulletManager bullets;
}
