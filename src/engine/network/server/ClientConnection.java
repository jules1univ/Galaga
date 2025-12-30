package engine.network.server;

import engine.network.NetBuffer;
import engine.network.NetObject;
import engine.network.NetworkManager;
import engine.utils.logger.Log;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public final class ClientConnection {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private final ClientConnect onConnect;
    private final ClientDisconnect onDisconnect;
    private final ClientReceive onReceive;

    private Thread updateThread;
    private volatile boolean active;

    public ClientConnection(ClientConnect onConnect, ClientDisconnect onDisconnect, ClientReceive onReceive) {
        this.onConnect = onConnect;
        this.onDisconnect = onDisconnect;
        this.onReceive = onReceive;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public String getAddress() {
        return this.socket.getRemoteSocketAddress().toString();
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean start(Socket socket) {
        try {
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            this.active = true;

            this.onConnect.run(this);
            this.updateThread = new Thread(this::update);
            this.updateThread.start();

            return true;
        } catch (IOException e) {
            Log.error("Net Client failed to start: %s", e.getMessage());
            return false;
        }
    }

    public boolean stop() {
        this.active = false;
        this.onDisconnect.run(this);

        try {
            if (this.socket != null && !this.socket.isClosed()) {
                this.socket.close();
            }
            return true;
        } catch (IOException e) {
            Log.error("Net Client failed to close: %s", e.getMessage());
            return false;
        }
    }

    private void update() {
        try {
            while (!this.socket.isClosed() && this.active) {
                int id = in.readInt();
                int length = in.readInt();
                byte[] data = in.readNBytes(length);

                NetObject obj = NetworkManager.createObjectById(id);
                if (obj == null) {
                    Log.error("Net Client received unknown object id: %d", id);
                    continue;
                }
                obj.read(new NetBuffer(data));
                this.onReceive.run(this, obj);
            }
        } catch (IOException e) {
            this.stop();
        }
    }

    public boolean send(NetObject obj) {
        NetBuffer buffer = new NetBuffer();
        obj.write(buffer);

        Optional<byte[]> optData = buffer.toBytes();
        if (optData.isEmpty()) {
            return false;
        }
        byte[] data = optData.get();
        try {
            out.writeInt(NetworkManager.getObjectId(obj));
            out.writeInt(data.length);
            out.write(data);
            out.flush();
            return true;
        } catch (IOException e) {
            Log.error("Net Client failed to send: %s", e.getMessage());
            this.stop();
            return false;
        }
    }
}
