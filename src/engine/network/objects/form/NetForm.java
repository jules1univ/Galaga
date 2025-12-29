package engine.network.objects.form;

import java.util.Map;

import engine.network.NetBuffer;
import engine.network.NetObject;
import engine.network.NetworkManager;

public class NetForm<T extends Enum<T>> implements NetObject {

    private Map<String, NetObject> fields;
    private T state;

    public NetForm() {
    }

    public static <E extends Enum<E>> NetForm<E> create(E state) {
        NetForm<E> form = new NetForm<>();
        form.state = state;
        return form;
    }

    
    public static <E extends Enum<E>> NetForm<E> create(E state, Map<String, NetObject> fields) {
        NetForm<E> form = new NetForm<>();
        form.state = state;
        form.fields = fields;
        return form;
    }

    public void setState(T state) {
        this.state = state;
    }

    public T getState() {
        return this.state;
    }

    public void addField(String name, NetObject value) {
        fields.put(name, value);
    }

    @Override
    public void read(NetBuffer buff) {
        int stateOrdinal = buff.readInt().orElse(0);
        this.state = (T) this.state.getDeclaringClass().getEnumConstants()[stateOrdinal];

        int fieldCount = buff.readInt().orElse(0);
        for (int i = 0; i < fieldCount; i++) {
            String key = buff.readString().orElse(null);
            if (key == null) {
                continue;
            }
            int id = buff.readInt().orElse(-1);
            NetObject obj = NetworkManager.createObjectById(id);
            if (obj != null) {
                obj.read(buff);
                fields.put(key, obj);
            }
        }
    }

    @Override
    public void write(NetBuffer buff) {
        buff.write(this.state.ordinal());
        buff.write(fields.size());
        for (String key : fields.keySet()) {
            buff.write(key);

            NetObject obj = fields.get(key);
            buff.write(NetworkManager.getObjectId(obj));
            buff.write(obj);
        }
    }

    @Override
    public void interpolate(NetObject other, float factor) {
    }

}
