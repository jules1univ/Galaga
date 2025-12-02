package engine.elements.ui;

import engine.elements.VisualElement;
import engine.utils.Position;

public abstract class UIElement extends VisualElement{

    public UIElement() {
    }

    public void setPosition(Position position) {
        this.position = position.copy();
    }
}
