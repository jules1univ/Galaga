package galaga.net.objects.request;

import engine.network.NetBuffer;
import engine.network.NetObject;

public class NetRequest implements NetObject {

    private NetRequestType type;
    private String field;

    public static NetRequest create(NetRequestType type) {
        NetRequest req = new NetRequest();
        req.type = type;
        return req;
    }

    public static NetObject create(NetRequestType type, String string) {
        NetRequest req = new NetRequest();
        req.type = type;
        req.field = string;
        return req;
    }


    public NetRequest() {
    }

    public NetRequestType getType() {
        return this.type;
    }

    public String getField() {
        return this.field;
    }

    @Override
    public void read(NetBuffer buff) {
        buff.readInt().ifPresent(v -> this.type = NetRequestType.values()[v]);
        buff.readString().ifPresent(v -> this.field = v);
    }

    @Override
    public void write(NetBuffer buff) {
        buff.write(this.type.ordinal());
        buff.write(this.field);
    }

    @Override
    public void interpolate(NetObject other, float factor) {
    }

    
}
