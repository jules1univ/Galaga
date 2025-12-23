package engine.elements.page;

import engine.elements.VisualElement;

public abstract class Page<T extends Enum<T>> extends VisualElement {

    protected final T id;
    protected PageState state;

    public Page(T id) {
        this.id = id;
        this.state = PageState.INACTIVE;
    }

    public T getId() {
        return this.id;
    }

    public PageState getState() {
        return this.state;
    }

    public abstract boolean onActivate();
     
    public abstract boolean onDeactivate();

    public abstract void onReceiveArgs(Object... args);

    public final boolean init()
    {
        throw new UnsupportedOperationException("Page.init should be called from subclasses");
    }

}
