package engine;

public abstract class Application {
    public static boolean DEBUG_MODE = false;
    private static AppContext<?> context;

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

        context = new AppContext<>(this);
    }

    protected final void setTitle(String title) {
        this.title = title;
        getContext().getFrame().setTitle(title);
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
        getContext().getFrame().start();
    }

    public final void stop() {
        getContext().getFrame().stop();
    }

    protected abstract boolean init();

    protected abstract void update(double dt);

    protected abstract void draw();
}
