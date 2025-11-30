package engine.ui.icon;

import java.util.List;
import engine.ui.UIElement;

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

        int space = (int) this.icons.get(0).getWidth();
        for (Icon icon : this.icons) {
            float x = Math.abs((this.alignLeft ? 0 : this.parentWidth) - (this.x + icon.getWidth() + space));
            float y = this.y + icon.getHeight() / 2;

            icon.setPosition(x, y);
            space += icon.getWidth() + this.margin;
        }
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
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
