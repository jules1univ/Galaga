package galaga.net.shared;

import engine.network.NetBuffer;
import engine.network.NetObject;

public class NetPlayerData implements NetObject {
 
    private String username;

    public static NetPlayerData of(String username) {
        NetPlayerData data = new NetPlayerData();
        data.username = username;
        return data;
    }

    public NetPlayerData() {
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public void read(NetBuffer buff) {
        buff.readString().ifPresent(name -> this.username = name);
    }

    @Override
    public void write(NetBuffer buff) {
        buff.write(this.username);
    }

    @Override
    public void interpolate(NetObject other, float factor) {
    }

    @Override
    public String toString() {
        return "NetPlayerData{username=" + this.username + "}";
    }
}
