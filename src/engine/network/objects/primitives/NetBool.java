package engine.network.objects.primitives;

import engine.network.NetBuffer;
import engine.network.NetObject;

public class NetBool implements NetObject {
    private boolean value;

    public static NetBool of(boolean value) {
        NetBool obj = new NetBool();
        obj.value = value;
        return obj;
    }

    public NetBool() {
        this.value = false;
    }

    public boolean getValue() {
        return this.value;
    }

    @Override
    public void read(NetBuffer buff) {
        this.value = buff.readBool().orElse(false);
    }

    @Override
    public void write(NetBuffer buff) {
        buff.write(this.value);
    }

    @Override
    public void interpolate(NetObject other, float factor) {
    }
    
}
