package engine;

import engine.graphics.Renderer;
import engine.input.InputKeyListener;
import engine.resource.ResourceManager;
import engine.resource.font.FontResource;
import engine.resource.sound.SoundResource;
import engine.resource.sprite.SpriteResource;
import engine.utils.ClipboardManager;

public final class AppContext<GameState, T extends Enum<T>> {
    private final AppFrame frame;
    private final Application<T> application;
    private final Renderer renderer;
    private final InputKeyListener input;
    private final ResourceManager resource;

    private GameState state;

    public AppContext(Application<T> app) {
        this.application = app;
        this.frame = new AppFrame(app);
        this.renderer = this.frame.getRenderer();
        this.input = this.frame.getInput();

        this.resource = new ResourceManager();
        this.resource.register(FontResource.NAME,FontResource.class);
        this.resource.register(SpriteResource.NAME,SpriteResource.class);
        this.resource.register(SoundResource.NAME, SoundResource.class);
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public AppFrame getFrame() {
        return this.frame;
    }

    public Application<T> getApplication() {
        return this.application;
    }

    
    public ClipboardManager getClipboard() {
        return this.frame.getClipboard();
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
