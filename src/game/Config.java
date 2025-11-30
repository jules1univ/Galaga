package game;

public class Config {
    public static final String LEVEL_1_PATH = ".\\resources\\levels\\level1.lvl";
    public static final String LEVEL_2_PATH = ".\\resources\\levels\\level2.lvl";

    public static final String SHIP_SPRITE_NAME = "ship";
    public static final String SHIP_PATH = ".\\resources\\sprites\\ship.spr";

    public static final String MEDAL_SPRITE_NAME = "medal";
    public static final String MEDAL_PATH = ".\\resources\\sprites\\medal.spr";

    public static final String ENEMY_BASE_PATH = ".\\resources\\sprites\\%s.spr";
    public static final String LEVEL_BASE_PATH = ".\\resources\\levels\\level%d.lvl";

    public static final float DEFAULT_SPRITE_SCALE = 3.f;
    public static final float DEFAULT_SPRITE_ICON_DOWNSCALE_FACTOR = .7f;
    public static final int DEFAULT_SKY_GRID_SIZE = 60;

    public static final int WINDOW_WIDTH = 700;
    public static final int WINDOW_HEIGHT = 700;
    public static final String WINDOW_TITLE = "Galaga - @jules1univ";

    public static final float HUD_HEIGHT = 50.f;
    public static final float FUD_HEIGHT = 50.f;

    public static final float ENEMY_START_LEFT_X_OFFSET = 50.f;
    public static final float ENEMY_START_LEFT_Y_OFFSET = 600.f;

    public static final float ENEMY_START_RIGHT_X_OFFSET = 600.f;
    public static final float ENEMY_START_RIGHT_Y_OFFSET = 600.f;

    public static final float PLAYER_SPEED = 600.f;
    public static final int PLAYER_INITIAL_LIFE = 3;

    public static final float STAR_MAX_BLINK_DELAY = 10.0f;
    public static final float STAR_MIN_BLINK_DELAY = 3.0f;
    public static final float STAR_MOVE_SPEED = 200.0f;

    public static final String FONT_URL = "https://st.1001fonts.net/download/font/bytebounce.medium.ttf";
    public static final String FONT_DEFAULT = "Consolas";

    public static final String TITLE_FONT_ALIAS = "title";
    public static final int TITLE_FONT_SIZE = 72;

    public static final String DEFAULT_FONT_ALIAS = "default";
    public static final int DEFAULT_FONT_SIZE = 24;

}
