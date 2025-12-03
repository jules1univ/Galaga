package engine.elements.ui.select;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.utils.Position;
import engine.utils.Size;
import java.awt.Color;
import java.awt.Font;

public abstract class Select<T extends UIElement> extends UIElement {

    private Position arrowLeft;
    private Position arrowRight;
    private boolean showArrows;

    protected T[] options;
    protected int index;

    protected Font font;
    protected T element;
    protected Color color;

    public Select(T[] options, int defaultIndex, boolean showArrows, Color color, Font font) {
        super();

        this.index = defaultIndex;
        this.showArrows = showArrows;
        this.color = color;
        this.font = font;

        this.options = options;
        this.element = this.options[this.index];
    }

    public Select(int defaultIndex, boolean showArrows, Color color, Font font) {
        super();
        this.index = defaultIndex;
        this.showArrows = showArrows;
        this.color = color;
        this.font = font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setColor(Color color) {
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
        int margin = 10;
        Size arrowSize = Application.getContext().getRenderer().getTextSize("<", this.font);
        Size sizeMax = Size.zero();
        for (T el : this.options) {
            Size elsize = el.getSize();
            if (elsize.getIntWidth() > sizeMax.getIntWidth()) {
                sizeMax = elsize;
            }
        }

        this.arrowLeft = Position.of(
                this.position.getX() - sizeMax.getWidth()/2 - arrowSize.getWidth() - margin,
                this.position.getY() + sizeMax.getHeight()/2);

        this.arrowRight = Position.of(
                this.position.getX() + sizeMax.getWidth()/2 + margin,
                this.position.getY() + sizeMax.getHeight()/2);
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
                this.color, this.font);
        Application.getContext().getRenderer().drawText(
                ">",
                this.arrowRight,
                this.color, this.font);
    }

}
