package engine.elements.ui.select;

import java.awt.Color;

import engine.elements.ui.icon.Icon;

public final class IconSelect extends Select<Icon> {

    public IconSelect(Icon[] options, int defaultIndex, boolean showArrows, Color color) {
        super(options, defaultIndex, showArrows, color);
    }

}
