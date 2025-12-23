package engine.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import engine.network.NetObject;
import engine.network.NetworkManager;
import engine.utils.logger.Log;

public abstract class Server {
    protected final NetworkManager netm;

    protected final List<ClientConnection> clients = new ArrayList<>();

    protected Thread updateThread;
    protected boolean active;
    private final boolean noMainThread;

    protected ServerSocket serverSocket;

    public Server(NetworkManager netm, boolean noMainThread) {
        this.netm = netm;
        this.noMainThread = noMainThread;
    }

    public boolean start(int port) {
        try {
            this.serverSocket = new ServerSocket(port);

            this.active = true;
            if (this.noMainThread) {
                this.update();
                return true;
            }

            this.updateThread = new Thread(this::update);
            this.updateThread.start();
            return true;
        } catch (IOException e) {
            Log.error("Net Server failed to start on port " + port + ": " + e.getMessage());
            return false;
        }
    }

    public void stop() {
        this.active = false;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            Log.error("Net Server failed to close: " + e.getMessage());
        }

        for (ClientConnection client : this.clients) {
            client.stop();
        }

        this.onDeactivate();
    }

    private void update() {
        this.onActivate();

        try {

            while (this.active) {

                ClientConnection client = new ClientConnection(this.netm, this.serverSocket.accept(),
                        this::onClientDisconnected, this::onClientReceive);
                client.start();
                this.clients.add(client);

                this.onClientConnected(client);
            }
        } catch (IOException e) {
            Log.error("Net Server failed to accept client connection: " + e.getMessage());
        }

    }

    protected final void sendAll(NetObject obj) {
        for (ClientConnection client : this.clients) {
            client.send(obj);
        }
    }

    protected abstract void onActivate();

    protected abstract void onDeactivate();

    protected abstract void onClientReceive(ClientConnection client, NetObject obj);

    protected abstract void onClientConnected(ClientConnection client);

    protected abstract void onClientDisconnected(ClientConnection client);

}
