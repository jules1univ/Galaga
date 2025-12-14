package engine;

import engine.graphics.Renderer;
import engine.utils.Time;
import engine.utils.logger.Log;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;

public final class AppCanvas extends Canvas implements Runnable {

    private Thread thread;
    private final Application<?> app;
    private final Renderer renderer;

    private volatile boolean running = false;

    public AppCanvas(Application<?> app) {
        super();
        this.app = app;
        this.renderer = new Renderer();

        this.setSize(app.getWidth(), app.getHeight());
        this.setPreferredSize(new Dimension(app.getWidth(), app.getHeight()));
        this.setFocusable(true);
        this.setIgnoreRepaint(true);

        this.requestFocus();
        this.requestFocusInWindow();
    }

    public Renderer getRenderer() {
        return this.renderer;
    }

    public synchronized void start() {
        if (this.running) {
            return;
        }
        this.running = true;
        this.thread = new Thread(this);
        this.thread.start();
    }

    public synchronized void stop() {
        if (!this.running || this.thread == null) {
            return;
        }

        Log.message("Application is closing");
        this.running = false;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        this.createBufferStrategy(3);
    }

    @Override
    public void run() {
        BufferStrategy strategy = getBufferStrategy();
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
        this.renderer.set(g);

        if (!this.app.init()) {
            Log.error("Application failed to initialize");
            this.stop();
            return;
        }

        Log.message("Application started successfully");
        Time.reset();

        while (this.running) {
            do {
                do {
                    Time.update();

                    g = (Graphics2D) strategy.getDrawGraphics();
                    if (g == null) {
                        Log.error("Application graphics context is null");
                        this.stop();
                        return;
                    }
                    this.renderer.set(g);

                    try {
                        this.app.update(Time.getDeltaTime());

                        this.renderer.begin();
                        this.app.draw();
                        this.renderer.end();
                    } catch (Exception e) {
                        Log.error("Application encountered an error during cycle: " + e.getMessage());
                        this.stop();
                        return;
                    }

                } while (strategy.contentsRestored());

                strategy.show();
            } while (strategy.contentsLost());

            Toolkit.getDefaultToolkit().sync();
        }
    }
}
