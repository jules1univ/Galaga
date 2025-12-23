package engine.network;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.utils.logger.Log;

public class NetworkManager {
    private final Map<Integer, Class<? extends NetObject>> networkClass = new HashMap<>();
    private int nextId = 1;

    public static NetworkManager of(List<Class<? extends NetObject>> netClasses) {
        NetworkManager netm = new NetworkManager();
        for (Class<? extends NetObject> netClass : netClasses) {
            netm.register(netClass);
        }
        return netm;
    }

    private NetworkManager() {
    }

    public <T extends NetObject> int register(Class<T> netClass) {
        
        boolean hasEmpty = false;
        for (Constructor<?> constructor : netClass.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                hasEmpty = true;
                break;
            }
        }

        if (!hasEmpty) {
            throw new IllegalArgumentException("NetObject must has an empty constructor");
        }

        int id = this.nextId++;
        this.networkClass.put(id, netClass);
        return id;
    }

    public NetObject create(int id) {
        try {
            return this.networkClass.get(id).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            Log.error("Net Manager failed to create object for id " + id + ": " + e.getMessage());
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
