package galaga.pages.multiplayer.lobby;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.utils.logger.Log;
import galaga.Galaga;
import galaga.GalagaPage;
import galaga.net.client.GalagaClient;

public class MultiplayerLobby extends Page<GalagaPage> {

    private GalagaClient client;

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
        if(this.client != null) {
            this.client.stop();
        }
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

        this.client = new GalagaClient(Galaga.net);

        String[] parts = server.split(":");
        if (parts.length != 2) {
            Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MULTIPLAYER_MENU, username, server);
            return;
        }

        String host = parts[0];
        int port;
        try {
            port = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MULTIPLAYER_MENU, username, server);
            return;
        }

        boolean connected = this.client.start(host, port);
        if (!connected) {
            Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MULTIPLAYER_MENU, username, server);
            return;
        }

        Log.message("Connected to multiplayer server at " + server);
    }

    @Override
    public void update(float dt) {
        
    }

    @Override
    public void draw() {
    }

}
