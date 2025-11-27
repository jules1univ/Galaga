package engine;

import engine.graphics.Renderer;
import engine.input.InputManager;

public final class AppContext<GameState> {
    private final AppFrame frame;
    private final Renderer renderer;
    private final InputManager input;
    private GameState state;

    public AppContext(AppFrame frame) {
        this.frame = frame;
        this.renderer = new Renderer(frame);
        this.input = new InputManager();
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

    public InputManager getInput() {
        return this.input;
    }

    public GameState getState() {
        return this.state;
    }

}
