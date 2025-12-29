package galaga.net.server;

import engine.network.NetObject;
import engine.network.objects.form.NetForm;
import engine.network.server.ClientConnection;
import engine.network.server.Server;
import engine.utils.Args;
import engine.utils.ini.Ini;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.net.GalagaNetState;

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
            Optional<String> input = Log.input(">");
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
                default -> Log.message("Unknown command: '%s'", input);
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


    @SuppressWarnings("unchecked")
    @Override
    protected void onClientReceive(ClientConnection client, NetObject obj) {
        Log.message("Server receive from client %s : (%s) %s", client.getSocket().getRemoteSocketAddress(),
                obj.getClass().getSimpleName(), obj.toString());

        if (!this.players.containsKey(client)) {
            Log.warning("Server received data from unknown client: %s", client.getSocket().getRemoteSocketAddress());
            client.stop();
            return;
        }

        NetPlayerData player = this.players.get(client);
        if (player == null) {
            Log.warning("Server has null player data for client: %s", client.getSocket().getRemoteSocketAddress());
            client.stop();
            return;
        }

        if (obj instanceof NetForm form) {
            if(form.getState().getDeclaringClass() != GalagaNetState.class) {
                Log.warning("Server received invalid form state from client: %s", client.getSocket().getRemoteSocketAddress());
                return;
            }
            this.handleForm(client, player, form);
        }
    }

    
    private void handleForm(ClientConnection client, NetPlayerData player, NetForm<GalagaNetState> form) {
       
    }

    @Override
    protected void onClientConnected(ClientConnection client) {
        if (this.players.size() >= this.maxPlayers) {
            Log.message("Server rejected client (server full): %s", client.getSocket().getRemoteSocketAddress());
            client.stop();
            return;
        }

        Log.message("Server new client connected: %s", client.getSocket().getRemoteSocketAddress());
        this.players.put(client, new NetPlayerData());
    }

    @Override
    protected void onClientDisconnected(ClientConnection client) {
        Log.message("Server client disconnected: %s", client.getSocket().getRemoteSocketAddress());

        this.players.remove(client);
    }

}
