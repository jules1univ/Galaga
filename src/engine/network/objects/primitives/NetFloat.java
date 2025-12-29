package engine.network.objects.primitives;

import engine.network.NetBuffer;
import engine.network.NetObject;

public class NetFloat implements NetObject {
    private float value;

    public static NetFloat of(float value) {
        NetFloat obj = new NetFloat();
        obj.value = value;
        return obj;
    }

    public NetFloat() {
        this.value = 0.f;
    }

    public float getValue() {
        return this.value;
    }

    @Override
    public void read(NetBuffer buff) {
        this.value = buff.readFloat().orElse(0.f);
    }

    @Override
    public void write(NetBuffer buff) {
        buff.write(this.value);
    }

    @Override
    public void interpolate(NetObject other, float factor) {
    }
    
}
