package engine;

import engine.utils.Time;
import engine.utils.logger.Log;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public final class AppPanel extends JPanel implements Runnable {

    private Thread thread;
    private final Application app;

    private volatile boolean running = false;

    public AppPanel(Application app) {
        super();
        this.app = app;


        this.setPreferredSize(new Dimension(app.getWidth(), app.getHeight()));
        this.setFocusable(true);
        
        // handled manually in the renderer
        this.setDoubleBuffered(false);
    }

    public void start() {
        if (this.running) {
            return;
        }
        this.running = true;
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void stop() {
        if (!this.running || this.thread == null) {
            return;
        }

        Log.message("Application is closing");
        this.running = false;
    }

    @Override
    public void run() {
        if (!this.app.init()) {
            Log.error("Application failed to initialize");
            this.stop();
            return;
        }

        Log.message("Application started successfully");
        Time.reset();
        while (this.running) {
            Time.update();

            this.app.update(Time.getDeltaTime());

            Application.getContext().getRenderer().begin();
            Application.getContext().getRenderer().clear(Color.BLACK);
            this.app.draw();
            Application.getContext().getRenderer().end();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
