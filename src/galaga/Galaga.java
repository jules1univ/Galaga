package galaga;

import engine.AppContext;
import engine.Application;
import engine.elements.page.Page;
import engine.resource.Resource;
import engine.resource.ResourceManager;
import engine.resource.ResourceVariant;
import engine.resource.font.FontResource;
import engine.resource.sound.SoundResource;
import engine.resource.sprite.SpriteResource;
import engine.utils.logger.Log;
import galaga.level.LevelResource;
import galaga.pages.GalagaPage;
import galaga.pages.game.Game;
import galaga.pages.loading.Loading;
import galaga.pages.menu.Menu;
import galaga.score.ScoreResource;
import java.awt.event.KeyEvent;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.management.ManagementFactory;

public class Galaga extends Application<GalagaPage> {

    @SuppressWarnings("unchecked")
    public static AppContext<State, GalagaPage> getContext() {
        return Application.getContext();
    }

    @Retention(java.lang.annotation.RetentionPolicy.SOURCE)
    @Target(java.lang.annotation.ElementType.METHOD)
    private @interface RequiresJava {
        int value();
    }

    @RequiresJava(21)
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

        rm.add(Config.SOUNDS, SoundResource.NAME, (ResourceVariant variant, Resource<?> rawRes) -> {
            SoundResource sound = (SoundResource) rawRes;
            if (GalagaSound.start_music.toString().equals(sound.getAlias().getName())) {
                sound.getData().play(0.1f);
            }
        });

        rm.add(Config.SPRITE_MEDAL, SpriteResource.NAME, (ResourceVariant variant, Resource<?> rawRes) -> {
            SpriteResource medal = (SpriteResource) rawRes;
            getContext().getFrame().setIconImage(medal.getData().getImage());
        });
        rm.add(Config.SPRITE_LOGO, SpriteResource.NAME);
        rm.add(Config.SPRITES_SHIP, SpriteResource.NAME);
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

    private float totalDelta = 0f;
    private float averageDelta = 0f;
    private int frameCount = 0;

    @Override
    protected void update(float dt) {
        if (getContext().getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            this.stop();
        }

        if (Application.DEBUG_MODE) {
            this.totalDelta += dt;
            this.frameCount++;
            this.averageDelta = this.totalDelta / this.frameCount;

            if (this.frameCount > 30) {
                if (dt > averageDelta * 3.0f) {
                    Log.warning("Freeze detected (dt: " + dt + "s, average: " + averageDelta + "s)");
                }
            }

        }
        super.update(dt);
    }

}