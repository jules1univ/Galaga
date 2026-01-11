package engine.utils.ini;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class IniSection {

    private final String name;
    private final Map<String, IniValue> values = new LinkedHashMap<>();

    IniSection(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void set(String key, String value) {
        this.set(key, IniValue.of(value));
    }

    public void set(String key, IniValue value) {
        this.values.put(key.toLowerCase(), value);
    }

    public IniValue get(String key) {
        if (!this.values.containsKey(key.toLowerCase())) {
            return IniValue.of(null);
        }
        return this.values.get(key.toLowerCase());
    }

    public boolean has(String key) {
        return this.values.containsKey(key.toLowerCase());
    }

    public void remove(String key) {
        this.values.remove(key.toLowerCase());
    }

    public Set<Map.Entry<String, IniValue>> entries() {
        return this.values.entrySet();
    }

    @Override
    public String toString() {
        String output = "[" + this.name + "]\n";
        for (Map.Entry<String, IniValue> entry : this.values.entrySet()) {
            if(entry.getValue() != null) {
                output += entry.getKey() + " = " + entry.getValue().toString() + "\n";
            }
        }
        return output;
    }

}
