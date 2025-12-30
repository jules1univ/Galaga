package engine;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.graphics.Renderer;
import engine.network.NetworkManager;
import engine.network.objects.form.NetForm;
import engine.network.objects.primitives.NetBool;
import engine.network.objects.primitives.NetFloat;
import engine.network.objects.primitives.NetInt;
import engine.network.objects.primitives.NetNull;
import engine.network.objects.primitives.NetString;
import engine.utils.Position;
import engine.utils.Size;
import engine.utils.logger.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Application<PageId extends Enum<PageId>> {

    public static boolean DEBUG_MODE = false;
    private static AppContext<?, ?> context;

    private Page<PageId> currentPage;
    private Page<PageId> nextPage;
    private Object[] pageArgs;
    private final Map<Enum<PageId>, Class<? extends Page<PageId>>> pages = new HashMap<>();

    protected final int width;
    protected final int height;
    protected String title;

    static {
        NetworkManager.register(List.of(
            NetForm.class,

            NetNull.class,
            NetBool.class,
            NetInt.class,
            NetFloat.class,
            NetString.class,
            
            Position.class,
            Size.class
        ));
    }

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

    public final Size getSize() {
        return Size.of(this.width, this.height);
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
        if(this.currentPage != null) {
            this.currentPage.onDeactivate();
        }
        getContext().getResource().cancel();
        getContext().getFrame().stop();
        this.destroy();
    }

    public final Page<PageId> getCurrentPage() {
        return this.currentPage;
    }

    public final boolean setCurrentPage(PageId id) {
        return this.setCurrentPage(id, (Object[]) null);
    }

    public final boolean setCurrentPage(PageId id, Object... args) {
        this.pageArgs = args;

        try {
            this.nextPage = this.pages.get(id).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            return false;
        }

        if (this.currentPage != null) {
            if (!this.currentPage.onDeactivate()) {
                Log.error("Failed to deactivate page: %s",  this.currentPage.getClass().getName());
                this.stop();
                return false;
            }
        } else {
            this.currentPage = this.nextPage;
            this.nextPage = null;
            if (!this.currentPage.onActivate()) {
                Log.error("Failed to activate page: %s", this.currentPage.getClass().getName());
                this.stop();
                return false;
            }
            this.currentPage.onReceiveArgs(this.pageArgs);
            this.pageArgs = null;
        }
        return true;
    }

    protected final <PageObj extends Page<PageId>> void registerPage(PageId id, Class<PageObj> pageClass) {
        this.pages.put(id, pageClass);
    }

    protected abstract boolean init();

    protected abstract void destroy();

    protected void update(float dt) {
        if (this.nextPage != null && this.currentPage.getState() == PageState.INACTIVE) {
            this.currentPage = this.nextPage;
            this.nextPage = null;
            if (!this.currentPage.onActivate()) {
                Log.error("Failed to activate page: %s" , this.currentPage.getClass().getName());
                this.stop();
                return;
            }
            this.currentPage.onReceiveArgs(this.pageArgs);
            this.pageArgs = null;
        }
        this.currentPage.update(dt);
    }

    protected final void draw(Renderer renderer) {
        this.currentPage.draw(renderer);
    }
}
