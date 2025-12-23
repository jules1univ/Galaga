package engine.network.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Optional;

import engine.network.NetBuffer;
import engine.network.NetObject;
import engine.network.NetworkManager;
import engine.utils.logger.Log;

public final class ClientConnection {

    private final NetworkManager netm;

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    private final ClientDisconnect onClientDisconnected;
    private final ClientReceive onReceive;

    private Thread updateThread;
    private boolean active;

    public ClientConnection(NetworkManager netm, Socket socket,
            ClientDisconnect onDisconnect,
            ClientReceive onReceive) {

        this.netm = netm;
        this.socket = socket;

        this.onReceive = onReceive;
        this.onClientDisconnected = onDisconnect;

        InputStream ins;
        try {
            ins = this.socket.getInputStream();
        } catch (IOException e) {
            Log.error("Net Server Client failed to get input stream: " + e.getMessage());
            ins = null;
        }
        this.in = new DataInputStream(ins);

        DataOutputStream outs;
        try {
            outs = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            Log.error("Net Server Client failed to get output stream: " + e.getMessage());
            outs = null;
        }
        this.out = outs;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public boolean isActive() {
        return this.active;
    }

    public void start() {
        this.active = true;
        this.updateThread = new Thread(this::update);
        this.updateThread.start();
    }

    public void stop() {
        this.active = false;
        try {
            this.socket.close();
        } catch (IOException e) {
            Log.error("Net Server Client failed to close: " + e.getMessage());
        }
    }

    private void update() {
        try {
            while (!this.socket.isClosed() && this.active) {
                int id = in.readInt();
                int length = in.readInt();
                byte[] data = in.readNBytes(length);

                NetObject obj = this.netm.create(id);
                if(obj == null) {
                    Log.error("Net Server Client received unknown object id: " + id);
                    continue;
                }
                obj.read(new NetBuffer(data));
                this.onReceive.run(this, obj);
            }
        } catch (IOException e) {
            Log.error("Net Server Client failed to receive: " + e.getMessage());
            this.onClientDisconnected.run(this);
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
            Log.error("Net Server Client failed to send: " + e.getMessage());
            return false;
        }
    }
}
