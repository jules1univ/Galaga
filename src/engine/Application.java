package engine;

public abstract class Application {
    private static AppContext<?> context;

    protected AppFrame frame;
    protected AppPanel panel;

    protected final int width;
    protected final int height;
    protected String title;

    @SuppressWarnings("unchecked")
    public static <State> AppContext<State> getContext() {
        return (AppContext<State>) context;
    }

    public Application(String title, int width, int height) {
        this.width = width;
        this.height = height;
        this.title = title;

        this.frame = new AppFrame(this);
        this.panel = new AppPanel(this);

        context = new AppContext<>(this.frame);

        this.frame.setPanel(this.panel);
        context.getInput().attachListeners(this.panel);
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
        this.panel.stop();
        this.frame.dispose();
    }

    protected abstract boolean init();

    protected abstract void update(double dt);

    protected abstract void draw();
}
