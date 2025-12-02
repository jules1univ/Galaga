package game.entities.ui;

import java.awt.Color;


import engine.elements.ui.UIElement;
import engine.elements.ui.text.Text;
import engine.elements.ui.text.TextPosition;
import engine.utils.Position;
import engine.utils.Size;
import engine.utils.Time;
import game.Config;
import game.Galaga;

public class HUD extends UIElement {

    private Text fps;
    private Text score;
    private Text bestScore;

    public HUD() {

    }

    @Override
    public boolean init() {
        this.size = Size.of(Galaga.getContext().getFrame().getWidth(), Config.HEIGHT_HUD);
        this.position = Position.of(0, this.size.getHeight() / 2);

        this.fps = new Text("FPS: 0", Position.of(this.size.getWidth(), this.size.getHeight()/2), Config.SIZE_FONT_TEXT, Color.WHITE);
        this.fps.setCenter(TextPosition.END, TextPosition.CENTER);

        this.score = new Text("SCORE: 0", Position.of(Size.half(this.size)), Config.SIZE_FONT_TEXT, Color.WHITE);
        this.score.setCenter(TextPosition.CENTER, TextPosition.CENTER);

        this.bestScore = new Text("BEST: 0", Position.of(0, this.size.getHeight()/2), Config.SIZE_FONT_TEXT, Color.WHITE);
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
