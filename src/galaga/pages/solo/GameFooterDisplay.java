package galaga.pages.solo;

import engine.elements.ui.UIElement;
import engine.elements.ui.icon.Icon;
import engine.elements.ui.icon.IconGroup;
import engine.graphics.Renderer;
import engine.graphics.sprite.Sprite;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import java.util.ArrayList;
import java.util.List;

public class GameFooterDisplay extends UIElement {

    private IconGroup lifeIcons;
    private IconGroup medalIcons;

    private Sprite ship;
    private Sprite medal;

    public GameFooterDisplay() {

    }

    private List<Icon> createArrayIcon(Sprite sprite, int length) {
        List<Icon> icons = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Icon icon = new Icon(sprite, Config.SPRITE_SCALE_ICON);
            icon.init();
            icons.add(icon);
        }
        return icons;
    }

    @Override
    public boolean init() {
        this.size = Size.of(Galaga.getContext().getFrame().getWidth(), Config.HEIGHT_FUD);
        this.position = Position.of(0, Galaga.getContext().getFrame().getHeight() - this.size.getHeight());

        this.ship = Galaga.getContext().getState().shipSkin;
        if(this.ship == null) {
            return false;
        }
        this.lifeIcons = new IconGroup(new ArrayList<>(), this.size.getWidth(), true,
                this.ship.getSize().getIntWidth() / 2);
        this.lifeIcons.setPosition(this.position);

        if (!this.lifeIcons.init()) {
            return false;
        }

        this.medal = Galaga.getContext().getResource().get(Config.SPRITE_MEDAL);
        if(this.medal == null) {
            return false;
        }
        this.medalIcons = new IconGroup(new ArrayList<>(), this.size.getWidth(), false,
                this.medal.getSize().getIntWidth());
        this.medalIcons.setPosition(this.position);

        return this.medalIcons.init();
    }

    @Override
    public void update(float dt) {
        int lifes = Galaga.getContext().getState().player.getLife();
        if (this.lifeIcons.getIcons().size() != lifes) {
            this.lifeIcons.setIcons(createArrayIcon(this.ship, lifes));
        }

        int medals = Galaga.getContext().getState().player.getMedals() ;
        if (this.medalIcons.getIcons().size() != medals) {
            this.medalIcons.setIcons(createArrayIcon(this.medal, medals));
        }
    }

    @Override
    public void draw(Renderer renderer) {
        this.lifeIcons.draw(renderer);
        this.medalIcons.draw(renderer);
    }

}
