package engine.network.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

import engine.network.NetBuffer;
import engine.network.NetObject;
import engine.network.NetworkManager;

public abstract class Client {

    protected Socket socket;
    protected DataInputStream in;
    protected DataOutputStream out;

    protected Thread updateThread;
    protected volatile boolean active = false;

    protected final NetworkManager netm;

    public Client(NetworkManager netm) {
        this.netm = netm;
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
            return false;
        }
    }

    public boolean stop() {
        try {
            this.active = false;
            this.socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    protected void update() {
        try {
            while (!socket.isClosed() && this.active) {
                int id = in.readInt();

                int length = in.readInt();
                byte[] data = in.readNBytes(length);

                NetObject obj = this.netm.create(id);
                obj.read(new NetBuffer(data));

                this.receive(obj);
            }
        } catch (IOException e) {
            this.onDisconnect();
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

            out.writeInt(this.netm.get(obj));
            out.writeInt(data.length);
            out.write(data);
            out.flush();

            return true;
        } catch (IOException e) {
            this.onDisconnect();
            return false;
        }
    }

    protected abstract void receive(NetObject obj);

    protected abstract void onConnect();

    protected abstract void onDisconnect();

}
