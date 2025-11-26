package engine;

public abstract class Application {
    protected AppContext ctx;
    protected AppFrame frame;
    protected AppPanel panel;

    protected final int width;
    protected final int height;
    protected String title;

    public Application(String title, int width, int height) {
        this.width = width;
        this.height = height;
        this.title = title;

        this.frame = new AppFrame(this);
        this.panel = new AppPanel(this);

        this.ctx = new AppContext(this.frame);

        this.frame.setPanel(this.panel);
        this.ctx.input.attachListeners(this.panel);
    }

    protected final void setTitle(String title) {
        this.title = title;
        frame.setTitle(title);
    }

    public final String getTitle() {
        return this.title;
    }

    public final int getWidth() {
        return this.width;
    }

    public final int getHeight() {
        return this.height;
    }

    public final void start() {
        this.panel.start();
    }

    public final void stop() {
        // TODO: fix stop
        // this.panel.stop();

        this.frame.dispose();
        System.exit(0);
    }

    protected abstract boolean init();

    protected abstract void update(double dt);

    protected abstract void draw();
}
