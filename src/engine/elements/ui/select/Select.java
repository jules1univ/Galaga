package engine.elements.ui.select;

import java.awt.Color;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.utils.Position;

public abstract class Select<T extends UIElement> extends UIElement {

    private Position arrowLeft;
    private Position arrowRight;
    private boolean showArrows;

    protected T[] options;
    protected int index;

    protected T element;
    protected Color color;

    public Select(T[] options, int defaultIndex, boolean showArrows, Color color) {
        super();

        this.index = defaultIndex;
        this.showArrows = showArrows;
        this.color = color;

        this.options = options;
        this.element = this.options[this.index];
    }

    public Select(int defaultIndex, boolean showArrows, Color color) {
        super();
        this.index = defaultIndex;
        this.showArrows = showArrows;
        this.color = color;
    }

    @Override
    public void setPosition(Position position) {
        super.setPosition(position);
        for (T el : this.options) {
            el.setPosition(position);
        }
        this.updateArrowPosition();
    }

    private void updateArrowPosition() {
        int maxWidth = 0;
        for (T el : this.options) {
            int width = el.getSize().getIntWidth();
            if (width > maxWidth) {
                maxWidth = width;
            }
        }

        this.arrowLeft = Position.of(
                this.element.getPosition().getX() - maxWidth / 2,
                this.element.getPosition().getY());

        this.arrowRight = Position.of(
                this.element.getPosition().getX() + maxWidth/2 + maxWidth/8,
                this.element.getPosition().getY());
    }

    public void toogleArrows() {
        this.showArrows = !this.showArrows;
    }

    public void next() {
        this.index = (this.index + 1) % this.options.length;
        this.element = this.options[this.index];
    }

    public void prev() {
        this.index = (this.index - 1 + this.options.length) % this.options.length;
        this.element = this.options[this.index];
    }

    @Override
    public boolean init() {
        for (T el : this.options) {
            if (!el.init()) {
                return false;
            }
        }
        this.updateArrowPosition();
        return true;
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void draw() {
        this.element.draw();
        if (!this.showArrows) {
            return;
        }

        Application.getContext().getRenderer().drawText(
                "<",
                this.arrowLeft,
                this.color);
        Application.getContext().getRenderer().drawText(
                ">",
                this.arrowRight,
                this.color);
    }

}
