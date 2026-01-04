package engine.network;

import engine.utils.logger.Log;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NetworkManager {
    private static final Map<Integer, Class<? extends NetObject>> networkClass = new HashMap<>();
    private static int nextId = 1;

    public static void register(List<Class<? extends NetObject>> netClasses) {
        for (Class<? extends NetObject> netClass : netClasses) {
            register(netClass);
        }
    }

    public static <T extends NetObject> int register(Class<T> netClass) {
        
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

        int id = nextId++;
        networkClass.put(id, netClass);
        return id;
    }

    public static NetObject createObjectById(int id) {
        if (!networkClass.containsKey(id)) {
            return null;
        }
        
        try {
            return networkClass.get(id).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            Log.error("Net Manager failed to create object for id %d: %s", id, e.getMessage());
            return null;
        }
    }

    public static int getObjectId(NetObject obj) {
        for (Map.Entry<Integer, Class<? extends NetObject>> entry : networkClass.entrySet()) {
            if (entry.getValue().equals(obj.getClass())) {
                return entry.getKey();
            }
        }
        return -1;
    }

}
