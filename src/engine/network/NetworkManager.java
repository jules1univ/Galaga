package engine.network;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NetworkManager {
    private final Map<Integer, Class<? extends NetObject>> networkClass = new HashMap<>();
    private int nextId = 1;

    public NetworkManager() {
    }

    public <T extends NetObject> void register(Class<T> netClass, Supplier<T> factory) {
        int id = this.nextId++;
        this.networkClass.put(id, netClass);
    }

    public NetObject create(int id) {
        try {
            return this.networkClass.get(id).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            return null;
        }
    }

    public int get(NetObject obj) {
        for (Map.Entry<Integer, Class<? extends NetObject>> entry : this.networkClass.entrySet()) {
            if (entry.getValue().equals(obj.getClass())) {
                return entry.getKey();
            }
        }
        return -1;
    }

}
