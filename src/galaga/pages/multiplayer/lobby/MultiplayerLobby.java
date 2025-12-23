package galaga.pages.multiplayer.lobby;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.utils.logger.Log;
import galaga.GalagaPage;

public class MultiplayerLobby extends Page<GalagaPage> {

    public MultiplayerLobby() {
        super(GalagaPage.MULTIPLAYER_LOBBY);
    }

    @Override
    public boolean onActivate() {

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
        if (args == null || args.length != 2) {
            return;
        }

        String username = (String) args[0];
        String server = (String) args[1];

        Log.message("Multiplayer Lobby opened for user " + username + " on server " + server);
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void draw() {
    }

}
