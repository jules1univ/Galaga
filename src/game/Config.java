package game;

import engine.resource.ResourceAlias;
import engine.resource.ResourceVariant;
import engine.utils.Position;
import game.entities.enemies.EnemyType;
import java.util.List;

public class Config {
        public static final int WINDOW_WIDTH = 700;
        public static final int WINDOW_HEIGHT = 700;
        public static final String WINDOW_TITLE = "Galaga - @jules1univ";

        public static final int PLAYER_INITIAL_LIFE = 3;

        public static final float SPRITE_SCALE_DEFAULT = 3.f;
        public static final float SPRITE_SCALE_ICON = 1.5f;

        public static final float HEIGHT_HUD = 50.f;
        public static final float HEIGHT_FUD = 50.f;

        public static final long SPEED_LOADING = 0;
        public static final float SPEED_PLAYER = 600.f;
        public static final float SPEED_STAR = 200.0f;
        public static final float SPEED_ENEMY_FACTOR = 1000.f;
        public static final float SPEED_ENEMY_UNLOCK_DELAY = 5.f;
        public static final float SPEED_ENEMY_ENTER_DELAY = 0.1f;

        public static final float TIME_BLINKSTAR_MAX = 10.0f;
        public static final float TIME_BLINKSTAR_MIN = 3.0f;

        public static final int SIZE_SKY_GRID = 50;
        public static final int SIZE_FONT_XLARGE = 48;
        public static final int SIZE_FONT_LARGE = 32;
        public static final int SIZE_FONT_TEXT = 24;

        public static final int POSITION_ENEMY_INDEX_NOTSET = -1;
        public static final float POSITION_LOCK_THRESHOLD = 1.f;
        public static final Position POSITION_ENEMY_LEFT = Position.of(0.f, 600.f);
        public static final Position POSITION_ENEMY_RIGHT = Position.of(700.f, 600.f);

        public static final String VARIANT_FONT_TEXT = "text";
        public static final String VARIANT_FONT_LARGE = "large";
        public static final String VARIANT_FONT_XLARGE = "xlarge";

        public static final List<ResourceAlias> DEFAULT_FONT = ResourceAlias.file(
                        "font",
                        "resources/fonts/default.ttf",
                        "https://st.1001fonts.net/download/font/bytebounce.medium.ttf").variant(ResourceVariant.of(Config.VARIANT_FONT_TEXT, Config.SIZE_FONT_TEXT),
                                        ResourceVariant.of(Config.VARIANT_FONT_LARGE, Config.SIZE_FONT_LARGE),
                                        ResourceVariant.of(Config.VARIANT_FONT_XLARGE, Config.SIZE_FONT_XLARGE));

        public static final ResourceAlias SHIP_SPRITE = ResourceAlias.file(
                        "ship",
                        "resources/sprites/ship.spr",
                        "");

        public static final ResourceAlias MEDAL_SPRITE = ResourceAlias.file(
                        "medal",
                        "resources/sprites/medal.spr",
                        "");

        public static final List<ResourceAlias> ENEMY_SPRITES = ResourceAlias.folder(EnemyType.class,
                        "resources/sprites/%s.spr", "%s");

        public static final ResourceAlias LEVEL_1 = ResourceAlias.file(
                        "level1",
                        "resources/levels/level1.lvl",
                        "");

        public static final ResourceAlias LEVEL_2 = ResourceAlias.file(
                        "level2",
                        "resources/levels/level2.lvl",
                        "");

}
