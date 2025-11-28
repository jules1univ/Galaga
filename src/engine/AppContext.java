package engine;

import engine.graphics.Renderer;
import engine.input.InputKeyListener;

public final class AppContext<GameState> {
    private final AppFrame frame;
    private final Renderer renderer;
    private final InputKeyListener input;
    private GameState state;

    public AppContext(AppFrame frame) {
        this.frame = frame;
        this.renderer = new Renderer(frame);
        this.input = new InputKeyListener();
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
