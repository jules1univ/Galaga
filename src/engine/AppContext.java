package engine;

import engine.graphics.Renderer;
import engine.graphics.font.FontResource;
import engine.graphics.sprite.SpriteResource;
import engine.input.InputKeyListener;
import engine.resource.ResourceManager;

public final class AppContext<GameState> {
    private final AppFrame frame;
    private final Renderer renderer;
    private final InputKeyListener input;
    private final ResourceManager resource;

    private GameState state;

    public AppContext(Application app) {
        this.frame = new AppFrame(app);
        this.renderer = this.frame.getRenderer();
        this.input = this.frame.getInput();

        this.resource = new ResourceManager();
        this.resource.register("font",FontResource.class);
        this.resource.register("sprite",SpriteResource.class);
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public AppFrame getFrame() {
        return this.frame;
    }

    public Renderer getRenderer() {
        return this.renderer;
    }

    public InputKeyListener getInput() {
        return this.input;
    }

    public ResourceManager getResource() {
        return this.resource;
    }

    public GameState getState() {
        return this.state;
    }

}
