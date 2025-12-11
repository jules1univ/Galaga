package galaga;

import java.awt.event.KeyEvent;
import java.lang.management.ManagementFactory;

import engine.AppContext;
import engine.Application;
import engine.elements.page.Page;
import engine.graphics.font.FontResource;
import engine.graphics.sprite.SpriteResource;
import engine.resource.Resource;
import engine.resource.ResourceManager;
import engine.resource.ResourceVariant;

import galaga.level.LevelResource;
import galaga.pages.GalagaPage;
import galaga.pages.game.Game;
import galaga.pages.loading.Loading;
import galaga.pages.menu.Menu;
import galaga.score.ScoreResource;

public class Galaga extends Application<GalagaPage> {

    @SuppressWarnings("unchecked")
    public static AppContext<State, GalagaPage> getContext() {
        return Application.getContext();
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equalsIgnoreCase("--debug")) {
            Application.DEBUG_MODE = true;
        }
        if (ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0) {
            Application.DEBUG_MODE = true;
        }

        Galaga game = new Galaga();
        game.start();
    }

    public Galaga() {
        super(Config.WINDOW_TITLE, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        getContext().setState(new State());
    }

    private boolean load() {
        this.registerPage(GalagaPage.MENU, Menu.class);
        this.registerPage(GalagaPage.GAME, Game.class);
        this.setCurrentPage(GalagaPage.MENU);
        return true;
    }

    @Override
    protected boolean init() {
        this.registerPage(GalagaPage.LOADING, Loading.class);
        this.setCurrentPage(GalagaPage.LOADING);

        ResourceManager rm = getContext().getResource();
        rm.register(LevelResource.NAME, LevelResource.class);
        rm.register(ScoreResource.NAME, ScoreResource.class);

        rm.add(Config.FONTS, FontResource.NAME, (ResourceVariant variant, Resource<?> rawRes) -> {
            if (variant != null && variant.getName().equals(Config.VARIANT_FONT_LARGE)) {
                FontResource font = (FontResource) rawRes;
                Page<?> page = this.getCurrentPage();
                if (page instanceof Loading loadingPage) {
                    loadingPage.setFont(font.getData());
                }
            }
        });

        rm.add(Config.SPRITE_SHIP, SpriteResource.NAME);
        rm.add(Config.SPRITE_MEDAL, SpriteResource.NAME, (ResourceVariant variant, Resource<?> rawRes) -> {
            SpriteResource medal = (SpriteResource) rawRes;
            getContext().getFrame().setIconImage(medal.getData().getImage());
        });
        rm.add(Config.SPRITES_ENEMY, SpriteResource.NAME);

        rm.add(Config.LEVELS, LevelResource.NAME);
        rm.add(Config.BEST_SCORE, ScoreResource.NAME);

        rm.load(() -> {
            if (!this.load()) {
                this.stop();
            }
        }, Application.DEBUG_MODE ? 0 : Config.SPEED_LOADING);
        return true;
    }

    @Override
    protected void update(float dt) {
        if (getContext().getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            this.stop();
        }
        super.update(dt);
    }

}