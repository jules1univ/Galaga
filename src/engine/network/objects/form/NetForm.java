package engine.network.objects.form;

import java.util.Map;

import engine.network.NetBuffer;
import engine.network.NetObject;
import engine.network.NetworkManager;

public class NetForm implements NetObject {

    private Map<String, NetObject> fields;
    private NetFormAction action;
    private String resourceId;

    public NetForm() {
    }

    public static NetForm create(Class<?> resId, Map<String, NetObject> fields) {
        NetForm form = new NetForm();
        form.action = NetFormAction.REQ_CREATE;
        form.resourceId = resId.getSimpleName().toLowerCase();
        form.fields = fields;
        return form;
    }

    public static NetForm read(Class<?> resId) {
        NetForm form = new NetForm();
        form.action = NetFormAction.REQ_READ;
        form.resourceId = resId.getSimpleName().toLowerCase();
        return form;
    }

    public static NetForm update(Class<?> resId, Map<String, NetObject> fields) {
        NetForm form = new NetForm();
        form.action = NetFormAction.REQ_UPDATE;
        form.resourceId = resId.getSimpleName().toLowerCase();
        form.fields = fields;
        return form;
    }

    public static NetForm delete(Class<?> resId) {
        NetForm form = new NetForm();
        form.action = NetFormAction.REQ_DELETE;
        form.resourceId = resId.getSimpleName().toLowerCase();
        return form;
    }

    public static NetForm response(Class<?> resId, Map<String, NetObject> fields) {
        NetForm form = new NetForm();
        form.action = NetFormAction.RESPONSE;
        form.resourceId = resId.getSimpleName().toLowerCase();
        form.fields = fields;
        return form;
    }

    public String getResourceId() {
        return this.resourceId;
    }

    public boolean isResourceId(Class<?> class1) {
        return this.resourceId.equals(class1.getSimpleName().toLowerCase());
    }

    public NetFormAction getAction() {
        return this.action;
    }

    public void addField(String name, NetObject value) {
        fields.put(name, value);
    }

    public boolean hasField(String name) {
        return fields.containsKey(name);
    }

    public NetObject getField(String name) {
        return fields.get(name);
    }

    public <E extends NetObject> E getFieldAs(String name, Class<E> cls) {
        NetObject obj = fields.get(name);
        if (cls.isInstance(obj)) {
            return cls.cast(obj);
        }
        return null;
    }

    @Override
    public void read(NetBuffer buff) {
        int stateOrdinal = buff.readInt().orElse(0);
        this.action = NetFormAction.values()[stateOrdinal];

        this.resourceId = buff.readString().orElse(null);

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
        buff.write(this.action.ordinal());
        buff.write(this.resourceId);
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
