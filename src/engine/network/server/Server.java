package engine.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import engine.network.NetObject;
import engine.network.NetworkManager;

public abstract class Server<Client extends ClientConnection> {
    protected final NetworkManager netm;

    protected final List<Client> clients = new ArrayList<>();

    protected Thread updateThread;
    protected boolean active;

    protected ServerSocket serverSocket;

    public Server(NetworkManager netm) {
        this.netm = netm;
    }

    public boolean start(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.updateThread = new Thread(this::update);
            this.updateThread.start();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void stop() {
        this.active = false;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
        }

        for (Client client : this.clients) {
            client.stop();
        }
    }

    private void update() {
        try {
            while (this.active) {
                Client client = this.onClientConnected(this.serverSocket.accept());
                client.start();
                this.clients.add(client);
            }
        } catch (IOException e) {
        }
    }

    protected void sendAll(NetObject obj) {
        for (Client client : this.clients) {
            client.send(obj);
        }
    }

    

    protected abstract void onReceive(Client client, NetObject obj);

    protected abstract Client onClientConnected(Socket socket);

    protected abstract void onClientDisconnected(Client client);

}
