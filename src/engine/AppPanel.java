package engine;

import engine.utils.Time;

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
        this.setDoubleBuffered(false);
        this.requestFocus();
        this.requestFocusInWindow();
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

        this.running = false;
    }

    @Override
    public void run() {
        if (!this.app.init()) {
            this.stop();
            return;
        }

        Time.reset();
        while (this.running) {
            Time.update();

            this.app.update(Time.getDeltaTime());

            this.app.ctx.renderer.begin();
            this.app.ctx.renderer.clear(Color.BLACK);
            this.app.draw();
            this.app.ctx.renderer.end();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
