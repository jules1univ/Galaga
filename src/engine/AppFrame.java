package engine;

import engine.graphics.Renderer;
import engine.input.InputKeyListener;
import java.awt.Dimension;
import javax.swing.JFrame;

public final class AppFrame extends JFrame {
    private final AppPanel panel;
    private final Renderer renderer;
    private InputKeyListener input;

    public AppFrame(Application<?> app) {
        super(app.getTitle());

        this.setSize(app.getWidth(), app.getHeight());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(app.getWidth(), app.getHeight()));

        this.setResizable(false);
        this.setFocusTraversalKeysEnabled(false);
        this.requestFocusInWindow();

        this.panel = new AppPanel(app);
        this.panel.addKeyListener(this.input);
        this.setContentPane(this.panel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        this.input = new InputKeyListener();
        this.panel.addKeyListener(this.input);

        this.renderer = new Renderer(this);
    }

    public Renderer getRenderer() {
        return this.renderer;
    }

    public InputKeyListener getInput() {
        return this.input;
    }

    public void start() {
        this.panel.start();
    }

    public void stop() {
        this.panel.stop();
        this.dispose();
    }
}
