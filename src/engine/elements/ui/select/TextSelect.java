package engine.elements.ui.select;

import java.awt.Color;

import engine.elements.ui.text.Text;
import engine.elements.ui.text.TextPosition;
import engine.utils.Position;

public final class TextSelect extends Select<Text> {

    public TextSelect(String[] options, int defaultIndex, boolean showArrows,int size, Color color) {
        super(defaultIndex, showArrows, color);
        Text[] textOptions = new Text[options.length];
        for (int i = 0; i < options.length; i++) {
            textOptions[i] = new Text(
                options[i],
                Position.zero(),
                size,
                color
            );
            textOptions[i].setCenter(TextPosition.CENTER, TextPosition.CENTER);
        }
        this.options = textOptions;
        this.element = this.options[this.index];
    }

} 
