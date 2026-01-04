package engine.network.objects.primitives;

import engine.network.NetBuffer;
import engine.network.NetObject;

public final class NetNull implements NetObject {

    public static NetNull of() {
        return new NetNull();
    }

    public NetNull() {
    }


    @Override
    public void read(NetBuffer buff) {
    }

    @Override
    public void write(NetBuffer buff) {
    }

    @Override
    public void interpolate(NetObject other, float factor) {
    }
    
}
