package game.entities.ui;

import java.awt.Color;

import engine.entity.Entity;
import engine.utils.Time;
import game.Config;
import game.Galaga;

public class HUD extends Entity {


    private Text fps;
    private Text score;
    private Text bestScore;

    public HUD() {

    }

    @Override
    public boolean init() {
        this.height = Config.HUD_HEIGHT;
        this.width = Galaga.getContext().getFrame().getWidth();
        this.x = 0;
        this.y = this.height/2;

        this.fps = new Text("FPS: 0", this.width, this.height/2, Config.TEXT_FONT_SIZE, Color.WHITE);
        this.fps.setCenter(TextPosition.END, TextPosition.CENTER);


        this.score = new Text("SCORE: 0", this.width/2, this.height/2, Config.TEXT_FONT_SIZE, Color.WHITE);
        this.score.setCenter(TextPosition.CENTER, TextPosition.CENTER);

        this.bestScore = new Text("BEST: 0", 0, this.height/2, Config.TEXT_FONT_SIZE, Color.WHITE);
        this.bestScore.setCenter(TextPosition.BEGIN, TextPosition.CENTER);

        return true;
    }

    @Override
    public void update(double dt) {
        this.fps.setText(String.format("FPS: %.2f", Time.getFrameRate()));
        this.score.setText(String.format("SCORE: %d", Galaga.getContext().getState().player.getScore()));
        this.bestScore.setText(String.format("BEST: %d", Galaga.getContext().getState().bestScore));
    }

    @Override
    public void draw() {
        this.fps.draw();
        this.score.draw();
        this.bestScore.draw();
    }

}
