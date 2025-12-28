package engine.network.server;

import engine.network.NetObject;
import engine.utils.logger.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.LockSupport;

public abstract class Server {
    private final CopyOnWriteArrayList<ClientConnection> clients = new CopyOnWriteArrayList<>();

    private Thread handleThread;
    private Thread updateThread;

    private volatile boolean active;

    private final boolean noMainThread;
    private final double tick;

    protected ServerSocket serverSocket;

    public Server(boolean noMainThread, float tickRate) {
        this.noMainThread = noMainThread;
        this.tick = 1.0f / tickRate;
    }

    public boolean start(int port) {
        try {
            this.serverSocket = new ServerSocket(port);

            this.active = true;

            this.handleThread = new Thread(this::handleClient);
            this.handleThread.start();

            if (this.noMainThread) {
                this.update();
            } else {
                this.updateThread = new Thread(this::update);
                this.updateThread.start();
            }

            return true;
        } catch (IOException e) {
            Log.error("Net Server failed to start on port %d: %s", port, e.getMessage());
            return false;
        }
    }

    public void stop() {
        this.active = false;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            Log.error("Net Server failed to close: %s", e.getMessage());
        }

        for (ClientConnection client : this.clients) {
            client.stop();
        }

        this.onDeactivate();
    }

    private void update() {
        double lastTime = System.nanoTime() / 1_000_000_000.0;
        double accumulator = 0.0;
        while (this.active) {
            double currentTime = System.nanoTime() / 1_000_000_000.0;
            double deltaTime = currentTime - lastTime;

            lastTime = currentTime;
            accumulator += deltaTime;
            while (accumulator >= this.tick) {
                accumulator -= this.tick;
                this.onTick();
            }

            synchronized (this.clients) {
                Iterator<ClientConnection> clientIt = this.clients.iterator();
                while (clientIt.hasNext()) {
                    ClientConnection client = clientIt.next();
                    if (!client.isActive()) {
                        clientIt.remove();
                    }
                }
            }

            LockSupport.parkNanos(1_000_000);
        }
    }

    private void handleClient() {
        this.onActivate();

        try {
            while (this.active) {
                ClientConnection client = new ClientConnection(this::onClientConnected, this::onClientDisconnected,
                        this::onClientReceive);
                client.start(this.serverSocket.accept());

                this.clients.add(client);
            }
        } catch (IOException e) {
            Log.error("Net Server failed to accept client connection: %s", e.getMessage());
        }

    }

    protected final void sendAll(NetObject obj) {
        synchronized (clients) {
            Iterator<ClientConnection> clientIt = this.clients.iterator();
            while (clientIt.hasNext()) {
                ClientConnection client = clientIt.next();
                if (!client.send(obj) || !client.isActive()) {
                    clientIt.remove();
                }
            }
        }
    }

    protected abstract void onActivate();

    protected abstract void onDeactivate();

    protected abstract void onTick();

    protected abstract void onClientReceive(ClientConnection client, NetObject obj);

    protected abstract void onClientConnected(ClientConnection client);

    protected abstract void onClientDisconnected(ClientConnection client);

}
