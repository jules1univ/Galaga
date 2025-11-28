package game.entities.ui;

import java.awt.Color;
import engine.entity.Entity;
import game.Galaga;

public class Text extends Entity {

    private String text;
    private Color color;
    private int size;
    private float initialY;

    public Text(String text, float x, float y, int size, Color color) {
        this.initialY = y;
        
        this.x = x;
        this.y = y;

        this.size = size;
        this.color = color;
    }

    public void setText(String text) {
        this.text = text;
        this.y = this.initialY - this.size;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public void update(double dt) {}

    @Override
    public void draw() {
        Galaga.getContext().getRenderer().drawText(this.text, (int)this.x, (int)this.y, this.color);
    }

}
