package galaga.pages.loading;

import java.util.Arrays;
import java.util.List;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.text.Text;
import engine.elements.ui.text.TextPosition;
import engine.utils.Position;

import galaga.Galaga;
import galaga.pages.GalagaPage;
import galaga.Config;

public class Loading extends Page<GalagaPage> {

    private Text text;

    public Loading() {
        super(GalagaPage.LOADING);
    }

    @Override
    public boolean onActivate() {
        List<Font> defaultFonts = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts());
        if (defaultFonts.isEmpty()) {
            return false;
        }

        Font defaultFont = defaultFonts
                .stream()
                .filter(font -> font.getFontName().equalsIgnoreCase("arial"))
                .findFirst()
                .orElse(defaultFonts.get(0))
                .deriveFont(Config.SIZE_FONT_TEXT);

        this.text = new Text("Loading", Position.of(
                Galaga.getContext().getFrame().getWidth() / 2,
                Galaga.getContext().getFrame().getHeight() / 2), Color.WHITE, defaultFont);
        this.text.setCenter(TextPosition.CENTER, TextPosition.CENTER);
        this.state = PageState.ACTIVE;
        return true;
    }

    @Override
    public boolean onDeactivate() {
        this.state = PageState.INACTIVE;
        return true;
    }

    public void setFont(Font font) {
        this.text.setFont(font);
    }

    @Override
    public void update(float dt) {
        this.text.setText(
                String.format(
                        "Loading... %.2f%% (%s)",
                        Galaga.getContext().getResource().getProgress() * 100.0f,
                        Galaga.getContext().getResource().getStatus()));
    }

    @Override
    public void draw() {
        this.text.draw();
    }

}
