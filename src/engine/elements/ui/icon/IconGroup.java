package engine.elements.ui.icon;

import java.util.List;

import engine.elements.ui.UIElement;
import engine.utils.Position;

public final class IconGroup extends UIElement {

    private int margin;
    private float parentWidth;
    private boolean alignLeft;
    private List<Icon> icons;

    public IconGroup(List<Icon> icons, float parentWidth, boolean alignLeft, int margin) {
        super();
        this.icons = icons;

        this.parentWidth = parentWidth;
        this.margin = margin;
        this.alignLeft = alignLeft;
    }

    public void setIcons(List<Icon> icons) {
        this.icons = icons;
        this.updatePosition();
    }

    public List<Icon> getIcons() {
        return this.icons;
    }

    private void updatePosition() {
        if (this.icons.isEmpty()) {
            return;
        }

        int space = (int) this.icons.get(0).getSize().getWidth();
        for (Icon icon : this.icons) {
            icon.setPosition(Position.of(
                Math.abs((this.alignLeft ? 0 : this.parentWidth) - (this.position.getX() + icon.getSize().getWidth() + space)),
                this.position.getY() + icon.getSize().getHeight() / 2
            ));
            space += icon.getSize().getWidth() + this.margin;
        }
    }

    @Override
    public void setPosition(Position position) {
        super.setPosition(position);
        this.updatePosition();
    }

    @Override
    public boolean init() {
        this.updatePosition();
        return true;
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void draw() {
        for (Icon icon : this.icons) {
            icon.draw();
        }
    }

}
