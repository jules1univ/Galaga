package galaga.net.server;

import engine.network.NetObject;
import engine.network.server.ClientConnection;
import engine.network.server.Server;
import engine.utils.Args;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.net.objects.request.NetRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GalagaServer extends Server {

    private final Map<ClientConnection, NetPlayerData> players = new HashMap<>();
    private final int maxPlayers = 4; // TODO: load from server config

    public GalagaServer() {
        super(false, Config.NET_TICKRATE);
    }

    public void launch(Args args) {
        int port = args.getInt("port", Config.NET_SERVER_PORT);
        String config = args.get("config", null);
        if (config != null) {
            // 
        }

        if(!this.start(port)) {
            Log.error("Server failed to start on port %d", port);
            return;
        }

        do{
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.error("Server launch interrupted: %s", e.getMessage());
                return;
            }
        }while(!this.isActive());

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
        Log.message("Net Server started on %s", this.serverSocket.getLocalSocketAddress());
    }

    @Override
    protected void onDeactivate() {
    }

    @Override
    protected void onTick() {
    }

    private void handleRequest(ClientConnection client, NetPlayerData player, NetRequest req) {
        switch (req.getType()) {
            case JOIN -> {
                NetObject obj = player.onJoinRequest(req);
                if (obj != null) {
                    client.send(obj);
                }
            }
            default -> {
            }
        }
    }

    @Override
    protected void onClientReceive(ClientConnection client, NetObject obj) {
        Log.message("Net Server receive from client %s : (%s) %s", client.getSocket().getRemoteSocketAddress(),
                obj.getClass().getSimpleName(), obj.toString());

        if (!this.players.containsKey(client)) {
            Log.warning("Received data from unregistered client: %s", client.getSocket().getRemoteSocketAddress());
            client.stop();
            return;
        }

        NetPlayerData player = this.players.get(client);
        if (player == null) {
            Log.warning("Player data is null for client: %s", client.getSocket().getRemoteSocketAddress());
            client.stop();
            return;
        }

        if (obj instanceof NetRequest req) {
            this.handleRequest(client, player, req);
        }

    }

    @Override
    protected void onClientConnected(ClientConnection client) {
        if (this.players.size() >= this.maxPlayers) {
            Log.message("Net Server rejected client (server full): %s", client.getSocket().getRemoteSocketAddress());
            client.stop();
            return;
        }

        Log.message("Net Server new client connected: %s", client.getSocket().getRemoteSocketAddress());
        this.players.put(client, new NetPlayerData());
    }

    @Override
    protected void onClientDisconnected(ClientConnection client) {
        Log.message("Net Server client disconnected: %s", client.getSocket().getRemoteSocketAddress());

        this.players.remove(client);
    }

}
