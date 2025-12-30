package engine.elements.ui.select;

import engine.elements.ui.Alignment;
import engine.elements.ui.text.Text;
import engine.utils.Position;
import java.awt.Color;
import java.awt.Font;

public final class TextSelectEnum<T extends Enum<T>> extends Select<Text> {

    private final Class<T> enumClass;

    public TextSelectEnum(Class<T> options, int defaultIndex, boolean showArrows, Color color, Font font) {
        super(defaultIndex, showArrows, color, font);
        
        this.enumClass = options;
        int len = options.getEnumConstants().length;
        
        Text[] textOptions = new Text[len];
        for (int i = 0; i < len; i++) {
            textOptions[i] = new Text(
                options.getEnumConstants()[i].name().replace("_", " "),
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

    public T getSelectedOption()
    {
        String text = this.getSelected().getText().replace(" ", "_");
        return Enum.valueOf(this.enumClass, text);
    }

} 
