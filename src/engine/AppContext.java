package engine;

import engine.graphics.Renderer;
import engine.input.InputKeyListener;

public final class AppContext<GameState> {
    private final AppFrame frame;
    private final Renderer renderer;
    private final InputKeyListener input;

    private GameState state;

    public AppContext(Application app) {
        this.frame = new AppFrame(app);
        this.renderer = this.frame.getRenderer();
        this.input = this.frame.getInput();
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

    public GameState getState() {
        return this.state;
    }

}
