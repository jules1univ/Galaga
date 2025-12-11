package engine;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.utils.logger.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class Application<T extends Enum<T>> {

    public static boolean DEBUG_MODE = false;
    private static AppContext<?, ?> context;

    private Page<T> currentPage;
    private Page<T> nextPage;
    private final Map<Enum<T>, Class<? extends Page<T>>> pages = new HashMap<>();

    protected final int width;
    protected final int height;
    protected String title;

    @SuppressWarnings("unchecked")
    public static <State, T extends Enum<T>> AppContext<State, T> getContext() {
        return (AppContext<State, T>) context;
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

    public final Page<T> getCurrentPage() {
        return this.currentPage;
    }

    public final boolean setCurrentPage(T id) {
        try {
            this.nextPage = this.pages.get(id).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            return false;
        }
        if (this.currentPage != null) {
            if(!this.currentPage.onDeactivate()) {
                Log.error("Failed to deactivate page: " + this.currentPage.getClass().getName());
                this.stop();
                return false;
            }
        } else {
            this.currentPage = this.nextPage;
            this.nextPage = null;
            if(!this.currentPage.onActivate()) {
                Log.error("Failed to activate page: " + this.currentPage.getClass().getName());
                this.stop();
                return false;
            }
        }
        return true;
    }

    protected final <PageObj extends Page<T>> void registerPage(T id, Class<PageObj> pageClass) {
        this.pages.put(id, pageClass);
    }

    protected abstract boolean init();

    protected void update(float dt) {
        if (this.nextPage != null && this.currentPage.getState() == PageState.INACTIVE) {
            this.currentPage = this.nextPage;
            this.nextPage = null;
            if(!this.currentPage.onActivate()) {
                Log.error("Failed to activate page: " + this.currentPage.getClass().getName());
                this.stop();
                return;
            }
        }
        this.currentPage.update(dt);
    }

    protected final void draw() {
        this.currentPage.draw();
    }
}
