package game.entities.ui;

import java.awt.Color;

import engine.entity.Entity;
import engine.utils.Time;
import game.Config;
import game.Galaga;

public class HUD extends Entity {

    public HUD() {

    }

    @Override
    public boolean init() {

        this.height = Config.HUD_HEIGHT;
        this.width = Galaga.getContext().getFrame().getWidth();

        this.x = 0;
        this.y = 0;
        return true;
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void draw() {
        // TODO: load HUD elements position in init 
        int fpsX = (int)this.x + 20;
        int fpsY = (int)(this.y + this.height/2);
        Galaga.getContext().getRenderer().drawText(String.format("FPS: %.2f", Time.getFrameRate()), fpsX, fpsY, Color.WHITE);

    }

}
