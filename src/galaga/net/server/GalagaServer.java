package galaga.net.server;

import engine.network.NetObject;
import engine.network.NetworkManager;
import engine.network.server.ClientConnection;
import engine.network.server.Server;
import engine.utils.logger.Log;

public class GalagaServer extends Server {

    public GalagaServer(NetworkManager netm) {
        super(netm, true);
    }

    @Override
    protected void onActivate() {
        Log.message("Net Server started on " + this.serverSocket.getLocalSocketAddress());
    }

    @Override
    protected void onDeactivate() {
    }

    @Override
    protected void onClientReceive(ClientConnection client, NetObject obj) {
        Log.message("Net Server receive from client " + client.getSocket().getRemoteSocketAddress() +
                ": " + "(" + obj.getClass().getSimpleName() + ")" + obj.toString());
    }

    @Override
    protected void onClientConnected(ClientConnection client) {
        Log.message("Net Server new client connected: " + client.getSocket().getRemoteSocketAddress());
    }

    @Override
    protected void onClientDisconnected(ClientConnection client) {
        Log.message("Net Server client disconnected: " + client.getSocket().getRemoteSocketAddress());
    }

}
