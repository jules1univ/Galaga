package engine.network.objects.primitives;

import engine.network.NetBuffer;
import engine.network.NetObject;

public final  class NetString implements NetObject {
    private String value;

    public static NetObject of(String value) {
        if(value == null) {
            return NetNull.of();
        }
        NetString obj = new NetString();
        obj.value = value;
        return obj;
    }

    public NetString() {
        this.value = "";
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public void read(NetBuffer buff) {
        this.value = buff.readString().orElse("");
    }

    @Override
    public void write(NetBuffer buff) {
        buff.write(this.value);
    }

    @Override
    public void interpolate(NetObject other, float factor) {
        if (other instanceof NetString o) {
            this.value = o.value;
        }
    }
    
}
