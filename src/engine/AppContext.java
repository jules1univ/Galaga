package engine;

import engine.entity.EntityManager;
import engine.graphics.Renderer;
import engine.graphics.sprite.SpriteManager;
import engine.input.InputManager;

public final class AppContext {
    public AppFrame frame;
    public Renderer renderer;
    public InputManager input;
    public EntityManager entityManager;
    public SpriteManager spriteManager;

    public AppContext(AppFrame frame) {
        this.frame = frame;
        this.renderer = new Renderer(frame);
        this.input = new InputManager();
        this.entityManager = EntityManager.getInstance();
        this.spriteManager = SpriteManager.getInstance();
    }

}
