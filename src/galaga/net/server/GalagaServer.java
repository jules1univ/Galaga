package galaga.net.server;

import engine.network.NetObject;
import engine.network.objects.form.NetForm;
import engine.network.objects.form.NetFormAction;
import engine.network.server.ClientConnection;
import engine.network.server.Server;
import engine.utils.Args;
import engine.utils.ini.Ini;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.net.shared.NetPlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GalagaServer extends Server {

    private final Map<ClientConnection, NetPlayerData> players = new HashMap<>();
    private int maxPlayers = Config.NET_SERVER_MAX_PLAYERS;

    public GalagaServer() {
        super(false, Config.NET_TICKRATE);
    }

    public void launch(Args args) {
        int port = args.getInt("port", Config.NET_SERVER_PORT);

        String configPath = args.get("config", null);
        if (configPath != null) {
            Ini config = Ini.load(configPath);
            if (config == null) {
                Log.error("Server config loading failed from path: %s", configPath);
                return;
            }

            Optional<Integer> newPort = config.getVariable("server", "port").asInt();
            if (newPort.isPresent()) {
                port = newPort.get();
                if (port < 1 || port > 65535) {
                    Log.error("Server config has invalid port number: %d", port);
                    return;
                }
            }

            Optional<Integer> newMaxPlayers = config.getVariable("server", "max_players").asInt();
            if (newMaxPlayers.isPresent()) {
                this.maxPlayers = newMaxPlayers.get();
                if (this.maxPlayers < 1 || this.maxPlayers > 1000) {
                    Log.error("Server config has invalid max players number: %d", this.maxPlayers);
                    return;
                }
            }
        }

        if (!this.start(port)) {
            Log.error("Server failed to start on port %d", port);
            return;
        }

        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.error("Server launch interrupted: %s", e.getMessage());
                return;
            }
        } while (!this.isActive());

        while (this.isActive()) {
            Optional<String> input = Log.input("");
            if (input.isEmpty()) {
                Log.message("Server stopping...");
                this.stop();
                break;
            }

            switch (input.get().trim().toLowerCase()) {
                case "exit" -> {
                    Log.message("Server stopping...");
                    this.stop();
                }
                case "status" -> {
                    Log.message("Server Status: %d/%d players connected.", this.players.size(), this.maxPlayers);
                }
                default -> {}
            }
        }
    }

    @Override
    protected void onActivate() {
        Log.message("Server started on %s", this.serverSocket.getLocalSocketAddress());
    }

    @Override
    protected void onDeactivate() {
    }

    @Override
    protected void onTick() {
    }

    @Override
    protected void onClientReceive(ClientConnection client, NetObject obj) {
        Log.message("Server receive from client %s : (%s) %s", client.getAddress(),
                obj.getClass().getSimpleName(), obj.toString());

        if (obj instanceof NetForm form) {
            this.handleForm(client, form);
        }
    }

    private void handleForm(ClientConnection client, NetForm form) {
        if (form.getAction() == NetFormAction.RESPONSE) {
            if(form.isResourceId(NetPlayerData.class))
            {
                if (!form.hasField("data")) {
                    return;
                }
                NetPlayerData newPlayerData = form.getFieldAs("data", NetPlayerData.class);
                if (newPlayerData == null) {
                    return;
                }

                this.players.put(client, newPlayerData);
                return;
            }
            return;
        }

        if(form.getAction() == NetFormAction.REQ_UPDATE) {
            if(form.isResourceId(NetPlayerData.class))
            {
                if (!form.hasField("data")) {
                    return;
                }
                NetPlayerData newPlayerData = form.getFieldAs("data", NetPlayerData.class);
                if (newPlayerData == null) {
                    return;
                }

                this.players.put(client, newPlayerData);
                return;
            }
            return;
        }
    }

    @Override
    protected void onClientConnected(ClientConnection client) {
        if (this.players.size() >= this.maxPlayers) {
            Log.message("Server rejected client (server full): %s", client.getAddress());
            client.stop();
            return;
        }

        Log.message("Server new client connected: %s", client.getAddress());
        client.send(NetForm.read(NetPlayerData.class));
    }

    @Override
    protected void onClientDisconnected(ClientConnection client) {
        Log.message("Server client disconnected: %s", client.getAddress());

        this.players.remove(client);
    }

}
