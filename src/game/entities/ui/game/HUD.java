package game.entities.ui.game;

import engine.elements.ui.UIElement;
import engine.elements.ui.text.Text;
import engine.elements.ui.text.TextPosition;
import engine.utils.Position;
import engine.utils.Size;
import engine.utils.Time;
import game.Config;
import game.Galaga;
import java.awt.Color;
import java.awt.Font;

public class HUD extends UIElement {

    private Text fps;
    private Text score;
    private Text bestScore;
    private Font textFont;

    public HUD() {

    }

    @Override
    public boolean init() {
        this.size = Size.of(Galaga.getContext().getFrame().getWidth(), Config.HEIGHT_HUD);
        this.position = Position.of(0,  0);

        this.textFont = Galaga.getContext().getResource().get(Config.DEFAULT_FONT, Config.VARIANT_FONT_DEFAULT);

        this.fps = new Text("FPS: 0", Position.of(this.size.getWidth(), 0),
                Color.WHITE, this.textFont);
        this.fps.setCenter(TextPosition.END, TextPosition.END);

        this.score = new Text("SCORE: 0", Position.of(this.size.getWidth()/2, 0), Color.WHITE, this.textFont);
        this.score.setCenter(TextPosition.CENTER, TextPosition.END);

        this.bestScore = new Text("BEST: 0", Position.of(0, 0), Color.WHITE, this.textFont);
        this.bestScore.setCenter(TextPosition.DEFAULT, TextPosition.END);

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
