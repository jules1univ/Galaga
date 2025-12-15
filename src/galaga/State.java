package galaga;

import engine.graphics.sprite.Sprite;
import galaga.entities.bullet.BulletManager;
import galaga.entities.particles.ParticlesManager;
import galaga.entities.player.Player;
import galaga.level.LevelManager;

public class State {
    public Sprite shipSkin = null;
    public Player player = null;

    public LevelManager level = null;
    public BulletManager bullets = null;
    public ParticlesManager particles = null;
}
