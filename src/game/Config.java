package game;

public class Config {
    public static final String LEVEL_1_PATH = ".\\resources\\levels\\level1.lvl";
    public static final String LEVEL_2_PATH = ".\\resources\\levels\\level2.lvl";

    public static final String SHIP_SPRITE_NAME = "ship";
    public static final String SHIP_PATH = ".\\resources\\sprites\\ship.spr";
    public static final String ENEMY_BASE_PATH = ".\\resources\\sprites\\";

    public static final float DEFAULT_SPRITE_SCALE = 3.f;
    public static final float DEFAULT_SPRITE_ICON_DOWNSCALE_FACTOR = .5f;
    public static final int DEFAULT_SKY_GRID_SIZE = 80;

    public static final int WINDOW_WIDTH = 700;
    public static final int WINDOW_HEIGHT = 700;

    public static final float HUD_HEIGHT = 50.f;
    public static final float FUD_HEIGHT = 50.f;

    public static final float PLAYER_SPEED = 600.f;
    public static final int PLAYER_INITIAL_LIFE = 3;

    public static final float STAR_MAX_BLINK_DELAY = 10.0f;
    public static final float STAR_MIN_BLINK_DELAY = 3.0f;
    public static final float STAR_MOVE_SPEED = 200.0f;

    public static final int TEXT_FONT_SIZE = 18;
    public static final int TITLE_FONT_SIZE = 36;


}
