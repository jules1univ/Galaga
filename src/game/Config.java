package game;

import engine.resource.ResourceAlias;
import engine.resource.ResourceVariant;
import engine.utils.Position;
import engine.utils.Size;
import game.entities.enemies.EnemyType;
import java.awt.Color;
import java.util.List;

public class Config {

    public static final int WINDOW_WIDTH = 700;
    public static final int WINDOW_HEIGHT = 700;
    public static final String WINDOW_TITLE = "Galaga - @jules1univ";

    public static final int PLAYER_INITIAL_LIFE = 3;

    public static final float SPRITE_SCALE_DEFAULT = 2.5f;
    public static final float SPRITE_SCALE_ICON = 1.5f;

    public static final float HEIGHT_HUD = 50.f;
    public static final float HEIGHT_FUD = 50.f;

    public static final long SPEED_LOADING = 0;
    public static final float SPEED_PLAYER = 600.f;
    public static final float SPEED_STAR = 200.0f;
    public static final float SPEED_BULLET = 500.f;
    public static final float SPEED_ENEMY_FACTOR = 500.f;

    public static final float DELAY_SHOOT_PLAYER = .5f;
    public static final float DELAY_ENEMY_ENTER = 0.2f;
    public static final float DELAY_ENEMY_COOLDOWN_FACTOR_ATTACK = 0.01f;
    public static final float DELAY_ENEMY_COOLDOWN_FACTOR_MISSILE = 0.001f;


    public static final float TIME_BLINKSTAR_MAX = 10.0f;
    public static final float TIME_BLINKSTAR_MIN = 3.0f;

    public static final int SIZE_SKY_GRID = 50;
    public static final int SIZE_FONT_XLARGE = 48;
    public static final int SIZE_FONT_LARGE = 32;
    public static final int SIZE_FONT_TEXT = 24;
    public static final Size SIZE_PARTICLE = Size.of(3.f, 3.f);

    public static final float POSITION_LOCK_THRESHOLD = 1.f;
    public static final float POSITION_NEAR_THRESHOLD = 10.f;

    public static final Position POSITION_ENEMY_LEFT = Position.of(-100.f, 600.f);
    public static final Position POSITION_ENEMY_RIGHT = Position.of(800.f, 600.f);
    public static final List<Position> POSITION_ZIG_ZAG = List.of(
            Position.of(20.f, 350.f),
            Position.of(680.f, 400.f),
            Position.of(20.f, 450.f),
            Position.of(680.f, 500.f),
            Position.of(20.f, 550.f),
            Position.of(680.f, 600.f)
    );

    public static final Color COLOR_BULLET = new Color(255, 0, 0);

    public static final String VARIANT_FONT_TEXT = "text";
    public static final String VARIANT_FONT_LARGE = "large";
    public static final String VARIANT_FONT_XLARGE = "xlarge";

    public static final ResourceAlias SPRITE_SHIP = ResourceAlias.file(
            "ship",
            "resources/sprites/ship.spr",
            "https://raw.githubusercontent.com/jules1univ/Galaga/refs/heads/master/resources/sprites/ship.spr");

    public static final ResourceAlias SPRITE_MEDAL = ResourceAlias.file(
            "medal",
            "resources/sprites/medal.spr",
            "https://raw.githubusercontent.com/jules1univ/Galaga/refs/heads/master/resources/sprites/medal.spr");

    public static final List<ResourceAlias> SPRITES_ENEMY = ResourceAlias.folder(EnemyType.class,
            "resources/sprites/%s.spr", "https://raw.githubusercontent.com/jules1univ/Galaga/refs/heads/master/resources/sprites/%s.spr");

    public static final List<ResourceAlias> FONTS = ResourceAlias.file(
            "font",
            "resources/fonts/default.ttf",
            "https://st.1001fonts.net/download/font/bytebounce.medium.ttf").variant(ResourceVariant.of(Config.VARIANT_FONT_TEXT, Config.SIZE_FONT_TEXT),
                    ResourceVariant.of(Config.VARIANT_FONT_LARGE, Config.SIZE_FONT_LARGE),
                    ResourceVariant.of(Config.VARIANT_FONT_XLARGE, Config.SIZE_FONT_XLARGE));

    public static final List<ResourceAlias> LEVELS = ResourceAlias.folder(
            "level%d",1, 2,
            "resources/levels/%s.lvl",
            "https://raw.githubusercontent.com/jules1univ/Galaga/refs/heads/master/resources/levels/%s.lvl");

}
