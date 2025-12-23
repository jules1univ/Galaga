package galaga;

import engine.AppContext;
import engine.Application;
import engine.elements.page.Page;
import engine.network.NetworkManager;
import engine.resource.Resource;
import engine.resource.ResourceManager;
import engine.resource.ResourceVariant;
import engine.resource.font.FontResource;
import engine.resource.sound.Sound;
import engine.resource.sound.SoundResource;
import engine.resource.sprite.SpriteResource;
import engine.utils.Args;
import engine.utils.Position;
import engine.utils.Size;
import galaga.level.LevelResource;
import galaga.net.client.GalagaClient;
import galaga.net.server.GalagaServer;
import galaga.pages.editor.level.LevelEditor;
import galaga.pages.editor.menu.EditorMenu;
import galaga.pages.editor.settings.Settings;
import galaga.pages.editor.sprite.SpriteEditor;
import galaga.pages.loading.Loading;
import galaga.pages.menu.Menu;
import galaga.pages.multiplayer.menu.MultiplayerMenu;
import galaga.pages.solo.GameSolo;
import galaga.score.ScoreResource;
import java.awt.event.KeyEvent;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.management.ManagementFactory;
import java.util.List;

public class Galaga extends Application<GalagaPage> {

    public static NetworkManager netm = NetworkManager.of(List.of(
            Position.class,
            Size.class));

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
    public static void main(String[] cmdArgs) {
        Args args = new Args(cmdArgs);

        boolean debugActive = ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
                .indexOf("-agentlib:jdwp") > 0;
        Application.DEBUG_MODE = debugActive || args.getBool("debug");

        if (args.getBool("server") && args.getInt("port", -1) > 0) {

            int port = args.getInt("port", -1);
            GalagaServer server = new GalagaServer(netm);
            server.start(port);
            return;
        }

        GalagaClient client = new GalagaClient(netm);
        client.start("localhost", 5555);

        while(true){}

        // Galaga game = new Galaga();
        // game.start();
    }

    public Galaga() {
        super(Config.WINDOW_TITLE, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        getContext().setState(new State());
    }

    private boolean load() {
        Sound startSound = getContext().getResource().get(GalagaSound.game_start);
        if (startSound != null) {
            startSound.play();
        }

        this.registerPage(GalagaPage.MAIN_MENU, Menu.class);

        this.registerPage(GalagaPage.GAME_SOLO, GameSolo.class);

        this.registerPage(GalagaPage.MULTIPLAYER_MENU, MultiplayerMenu.class);

        this.registerPage(GalagaPage.EDITOR_MENU, EditorMenu.class);
        this.registerPage(GalagaPage.EDITOR_LEVEL, LevelEditor.class);
        this.registerPage(GalagaPage.EDITOR_SPRITE, SpriteEditor.class);
        this.registerPage(GalagaPage.EDITOR_SETTINGS, Settings.class);

        this.setCurrentPage(GalagaPage.MAIN_MENU);

        return true;
    }

    @Override
    protected void destroy() {
        Sound closeSound = Galaga.getContext().getResource().get(GalagaSound.game_close);
        if (closeSound != null) {
            closeSound.play();
        }
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

        rm.add(Config.SPRITE_MEDAL, SpriteResource.NAME, (ResourceVariant variant, Resource<?> rawRes) -> {
            SpriteResource medal = (SpriteResource) rawRes;
            getContext().getFrame().setIconImage(medal.getData().getImage());
        });
        rm.add(Config.SPRITE_LOGO, SpriteResource.NAME);
        rm.add(Config.SPRITES_SHIP, SpriteResource.NAME);
        rm.add(Config.SPRITES_ENEMY, SpriteResource.NAME);

        rm.add(Config.LEVELS, LevelResource.NAME);
        rm.add(Config.BEST_SCORE, ScoreResource.NAME);

        rm.add(Config.SOUNDS, SoundResource.NAME);

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