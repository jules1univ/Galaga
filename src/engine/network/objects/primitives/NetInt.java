package engine.network.objects.primitives;

import engine.network.NetBuffer;
import engine.network.NetObject;

public final class NetInt implements NetObject {
    private int value;

    public static NetInt of(int value) {
        NetInt NetInt = new NetInt();
        NetInt.value = value;
        return NetInt;
    }

    public NetInt() {
        this.value = 0;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public void read(NetBuffer buff) {
        this.value = buff.readInt().orElse(0);
    }

    @Override
    public void write(NetBuffer buff) {
        buff.write(this.value);
    }

    @Override
    public void interpolate(NetObject other, float factor) {
        if (other instanceof NetInt o) {
            this.value = this.value + (int)((o.value - this.value) * factor);
        }
    }
    
}
