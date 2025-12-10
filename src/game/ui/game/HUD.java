package game.ui.game;

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

        this.textFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_TEXT);

        int margin = 20;
        this.fps = new Text("", Position.of(this.size.getWidth() - margin, this.size.getHeight()),
                Color.WHITE, this.textFont);
        
        // hack fix to avoid resizing when fps change
        this.fps.setFixSize("FPS: 0.0000", true);
        this.fps.setCenter(TextPosition.END, TextPosition.CENTER);

        this.score = new Text("SCORE: 0", Position.of(this.size.getWidth()/2, this.size.getHeight()), Color.WHITE, this.textFont);
        this.score.setCenter(TextPosition.CENTER, TextPosition.CENTER);

        this.bestScore = new Text("BEST: 0", Position.of(margin, this.size.getHeight()), Color.WHITE, this.textFont);
        this.bestScore.setCenter(TextPosition.BEGIN, TextPosition.CENTER);

        return true;
    }

    @Override
    public void update(float dt) {
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
