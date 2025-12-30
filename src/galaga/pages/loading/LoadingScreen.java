package galaga.pages.loading;

import java.util.Arrays;
import java.util.List;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.Alignment;
import engine.elements.ui.loading.Loading;
import engine.elements.ui.text.Text;
import engine.graphics.Renderer;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Galaga;
import galaga.GalagaPage;
import galaga.Config;

public class LoadingScreen extends Page<GalagaPage> {

    private Text text;
    private Loading loading;

    public LoadingScreen() {
        super(GalagaPage.LOADING);
    }

    @Override
    public boolean onActivate() {
        List<Font> defaultFonts = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts());
        if (defaultFonts.isEmpty()) {
            return false;
        }

        Font defaultFont = defaultFonts.get(0);
        for (Font font : defaultFonts) {
            if (font.getFontName().equalsIgnoreCase("arial")) {
                defaultFont = font.deriveFont(Config.SIZE_FONT_TEXT);
                break;
            }
        }

        int padding = 5;
        this.loading = new Loading(
                Position.of(Config.WINDOW_WIDTH / 2.f, Config.WINDOW_HEIGHT / 2.f),
                Size.of(Config.WINDOW_WIDTH * 0.6f, Config.WINDOW_HEIGHT * 0.05f),
                padding, Color.LIGHT_GRAY, defaultFont);
        if (!this.loading.init()) {
            return false;
        }
        this.loading.setCenter(Alignment.CENTER, Alignment.CENTER);

        this.text = new Text("Loading", Position.of(
                Config.WINDOW_WIDTH / 2.f,
                Config.WINDOW_HEIGHT / 2.f - this.loading.getSize().getHeight() + padding * 2), Color.WHITE,
                defaultFont);
        if (!this.text.init()) {
            return false;
        }
        this.text.setCenter(Alignment.CENTER, Alignment.BEGIN);
        this.state = PageState.ACTIVE;
        return true;
    }

    @Override
    public boolean onDeactivate() {
        this.state = PageState.INACTIVE;
        return true;
    }

    @Override
    public void onReceiveArgs(Object... args) {
    }

    public void setFont(Font font) {
        this.loading.setFont(font);
        this.text.setFont(font);
    }

    @Override
    public void update(float dt) {
        this.text.setText(
                String.format(
                        "Loading (%s)",
                        Galaga.getContext().getResource().getLoadingStatus()));
        this.loading.setPercent(Galaga.getContext().getResource().getLoadingProgress());

    }

    @Override
    public void draw(Renderer renderer) {
        this.text.draw(renderer);
        this.loading.draw(renderer);
    }

}
