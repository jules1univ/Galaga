package engine;

import engine.graphics.Renderer;
import engine.input.InputKeyListener;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

public final class AppFrame extends JFrame {
    private final AppCanvas canvas;
    private final InputKeyListener input;

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

        this.requestFocusInWindow();

        this.input = new InputKeyListener();
        this.canvas.setFocusTraversalKeysEnabled(false);
        this.canvas.addKeyListener(this.input);
        this.canvas.requestFocus();


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.stop();
            }
        });
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
