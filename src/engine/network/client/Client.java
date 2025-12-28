package engine.network.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

import engine.network.NetBuffer;
import engine.network.NetObject;
import engine.network.NetworkManager;
import engine.utils.logger.Log;

public abstract class Client {

    protected Socket socket;
    protected DataInputStream in;
    protected DataOutputStream out;

    protected Thread updateThread;
    protected volatile boolean active = false;

    public Client() {
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean start(String host, int port) {
        try {
            this.socket = new Socket(host, port);
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            this.active = true;
            this.onConnect();

            this.updateThread = new Thread(this::update);
            this.updateThread.start();

            return true;
        } catch (Exception e) {
            Log.error("Net Client failed to connect to " + host + ":" + port + ": " + e.getMessage());
            return false;
        }
    }

    public boolean stop() {
        this.onDisconnect();

        try {
            this.active = false;
            if (this.socket != null && !this.socket.isClosed()) {
                this.socket.close();
            }
            return true;
        } catch (IOException e) {
            Log.error("Net Client failed to close: " + e.getMessage());
            return false;
        }
    }

    protected void update() {
        try {
            while (!this.socket.isClosed() && this.active) {
                int id = this.in.readInt();

                int length = this.in.readInt();
                byte[] data = this.in.readNBytes(length);

                NetObject obj = NetworkManager.createObjectById(id);
                if (obj == null) {
                    Log.error("Net Client received unknown object id: " + id);
                    continue;
                }

                obj.read(new NetBuffer(data));
                this.onReceive(obj);
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
            Log.error("Net Client failed to send: " + e.getMessage());
            this.stop();
            return false;
        }
    }

    protected abstract void onReceive(NetObject obj);

    protected abstract void onConnect();

    protected abstract void onDisconnect();

}
