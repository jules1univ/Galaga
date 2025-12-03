package engine.elements.ui.select;

import engine.elements.ui.icon.Icon;
import java.awt.Color;
import java.awt.Font;

public final class IconSelect extends Select<Icon> {

    public IconSelect(Icon[] options, int defaultIndex, boolean showArrows, Color color, Font font) {
        super(options, defaultIndex, showArrows, color, font);
    }

}
