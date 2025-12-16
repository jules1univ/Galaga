package galaga.pages.solo;

import engine.elements.ui.UIElement;
import engine.elements.ui.text.Text;
import engine.elements.ui.text.TextPosition;
import engine.utils.Position;
import engine.utils.Size;
import engine.utils.Time;
import galaga.Config;
import galaga.Galaga;
import galaga.score.Score;

import java.awt.Color;
import java.awt.Font;

public class GameHeaderDisplay extends UIElement {

    private Text fps;
    private Text score;
    private Text bestScore;
    private Font textFont;

    public GameHeaderDisplay() {

    }

    @Override
    public boolean init() {
        this.size = Size.of(Galaga.getContext().getFrame().getWidth(), Config.HEIGHT_HUD);
        this.position = Position.of(0,  0);

        this.textFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_TEXT);

        this.fps = new Text("", Position.of(this.size.getWidth(), this.size.getHeight()/2),
                Color.WHITE, this.textFont);
        
        // hack fix to avoid resizing when fps change
        this.fps.setFixSize("FPS: XXXXX.XX", true);
        this.fps.setCenter(TextPosition.END, TextPosition.BEGIN);

        this.score = new Text("SCORE: 0", Position.of(this.size.getWidth()/2, this.size.getHeight()/2), Color.WHITE, this.textFont);
        this.score.setCenter(TextPosition.CENTER, TextPosition.BEGIN);

        Score score = Galaga.getContext().getResource().get(Config.BEST_SCORE);
        if(score == null) {
            return false;
        }
        this.bestScore = new Text(String.format("BEST: %d", score.getValue()), Position.of(0, this.size.getHeight()/2), Color.RED, this.textFont);
        this.bestScore.setCenter(TextPosition.BEGIN, TextPosition.BEGIN);
        return true;
    }

    @Override
    public void update(float dt) {
        this.fps.setText(String.format("FPS: %.2f", Time.getFrameRate()));
        this.score.setText(String.format("SCORE: %d", Galaga.getContext().getState().player.getScore()));
    }

    @Override
    public void draw() {
        this.fps.draw();
        this.score.draw();
        this.bestScore.draw();
    }

}
