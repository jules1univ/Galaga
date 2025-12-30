package engine.elements.ui.select;

import engine.elements.ui.Alignment;
import engine.elements.ui.text.Text;
import engine.utils.Position;
import java.awt.Color;
import java.awt.Font;

public final class TextSelect extends Select<Text> {

    public TextSelect(String[] options, int defaultIndex, boolean showArrows, Color color, Font font) {
        super(defaultIndex, showArrows, color, font);
        Text[] textOptions = new Text[options.length];
        for (int i = 0; i < options.length; i++) {
            textOptions[i] = new Text(
                options[i],
                Position.zero(),
                color,
                font
            );
            textOptions[i].setCenter(Alignment.CENTER, Alignment.CENTER);
        }
        this.options = textOptions;
        this.element = this.options[this.index];
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        for (Text text : this.options) {
            text.setFont(font);
        }
    }

} 
