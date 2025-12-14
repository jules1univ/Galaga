package engine;

import engine.graphics.Renderer;
import engine.input.InputKeyListener;

import java.awt.Dimension;

import javax.swing.JFrame;

public final class AppFrame extends JFrame {
    private final AppCanvas canvas;
    private InputKeyListener input;

    public AppFrame(Application<?> app) {
        super(app.getTitle());

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(app.getWidth(), app.getHeight());
        this.setPreferredSize(new Dimension(app.getWidth(), app.getHeight()));
        this.setResizable(false);

        this.canvas = new AppCanvas(app);
        this.add(this.canvas);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        this.input = new InputKeyListener();

        this.setFocusTraversalKeysEnabled(false);
        this.addKeyListener(this.input);
        this.canvas.addKeyListener(this.input);

        this.requestFocusInWindow();
    }

    public InputKeyListener getInput() {
        return this.input;
    }

    public Renderer getRenderer() {
        return this.canvas.getRenderer();
    }

    public void start() {
        this.canvas.start();
    }

    public void stop() {
        this.canvas.stop();
        this.dispose();
    }
}
