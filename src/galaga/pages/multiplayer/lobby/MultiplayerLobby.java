package galaga.pages.multiplayer.lobby;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.text.Text;
import engine.elements.ui.text.TextPosition;
import engine.graphics.Renderer;
import engine.utils.Position;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;
import galaga.net.client.GalagaClient;
import java.awt.Color;
import java.awt.Font;

public class MultiplayerLobby extends Page<GalagaPage> {

    private GalagaClient client;
    private String username;
    private String serverAddress;

    private MultiplayerLobbyState lobbyState = MultiplayerLobbyState.WAITING_CONNECT;

    private Font titleFont;
    private Text status;

    private float redirectTimer = -1.f;

    public MultiplayerLobby() {
        super(GalagaPage.MULTIPLAYER_LOBBY);
    }

    private void updateStatus() {
        switch (this.lobbyState) {
            case WAITING_CONNECT -> this.status.setText("Connecting to server...");
            case CONNECTED -> this.status.setText("Connected! Waiting for other players...");
            case NOT_CONNECTED -> this.status.setText("Failed to connect to server.");
            case WAITING_PLAYERS -> this.status.setText("All players connected! Starting soon...");
            case STARTING_GAME -> this.status.setText("Starting game...");
        }
    }

    private void setStatusFailConnect() {
        this.lobbyState = MultiplayerLobbyState.NOT_CONNECTED;
        this.redirectTimer = 5.f;

        this.updateStatus();
    }

    @Override
    public boolean onActivate() {

        this.titleFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_LARGE);
        if (this.titleFont == null) {
            return false;
        }

        this.status = new Text("", Position.of(Config.WINDOW_WIDTH / 2.f, Config.WINDOW_HEIGHT / 2.f), Color.WHITE,
                this.titleFont);
        if (!this.status.init()) {
            return false;
        }
        this.status.setCenter(TextPosition.CENTER, TextPosition.CENTER);

        this.updateStatus();
        this.state = PageState.ACTIVE;
        return true;
    }

    @Override
    public boolean onDeactivate() {
        if (this.client != null) {
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

        this.username = (String) args[0];
        this.serverAddress = (String) args[1];

        this.client = new GalagaClient();

        String[] parts = this.serverAddress.split(":");
        if (parts.length != 2) {
            this.setStatusFailConnect();
            return;
        }

        String host = parts[0];
        int port;
        try {
            port = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            this.setStatusFailConnect();
            return;
        }

        boolean connected = this.client.start(host, port);
        if (!connected) {
            this.setStatusFailConnect();
            return;
        }

        this.lobbyState = MultiplayerLobbyState.CONNECTED;
        this.updateStatus();
    }

    @Override
    public void update(float dt) {

        if (this.lobbyState == MultiplayerLobbyState.NOT_CONNECTED) {
            this.redirectTimer -= dt;
            if (this.redirectTimer <= 0.f) {
                Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MULTIPLAYER_MENU, this.username, this.serverAddress);
            }
            return;
        }

        if(!this.client.isActive() && this.lobbyState != MultiplayerLobbyState.NOT_CONNECTED) {
            this.setStatusFailConnect();
            // return;
        }

    }

    @Override
    public void draw(Renderer renderer) {
        this.status.draw(renderer);
    }

}
