package galaga.resources.settings;

import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import engine.utils.ini.Ini;
import engine.utils.ini.IniValue;
import engine.utils.logger.Log;

public class Setting {

    private final Map<String, IniValue> values;
    private final Map<String, List<Integer>> keyCache = new HashMap<>();

    public static Setting load(InputStream in) {
        Ini config = Ini.load(in);
        if (config == null) {
            return null;
        }

        Map<String, IniValue> values = new HashMap<>();
        config.getSections().forEach(section -> {
            values.putAll(section.getVariables());
        });
        return new Setting(values);
    }

    private Setting(Map<String, IniValue> values) {
        this.values = values;
    }

    public Optional<List<Integer>> getKeys(String... varnames) {
        if (this.keyCache.containsKey(String.join(",", varnames))) {
            return Optional.of(this.keyCache.get(String.join(",", varnames)));
        }

        List<Integer> keys = new ArrayList<>();
        for (String varname : varnames) {
            Optional<List<Integer>> varkeys = getKeys(varname);
            varkeys.ifPresent(keys::addAll);
        }

        this.keyCache.put(String.join(",", varnames), keys);
        return keys.isEmpty() ? Optional.empty() : Optional.of(keys);
    }

    public Optional<List<Integer>> getKeys(String varname) {
        if (!this.values.containsKey(varname)) {
            return Optional.empty();
        }

        if (this.keyCache.containsKey(varname)) {
            return Optional.of(this.keyCache.get(varname));
        }

        String[] rawKeys = this.values.get(varname).toString().split(",");
        List<Integer> keys = new ArrayList<>();

        for (String raw : rawKeys) {
            raw = raw.trim();

            try {
                int keyCode;
                if (raw.startsWith("VK_")) {
                    Field field = KeyEvent.class.getField(raw);
                    keyCode = field.getInt(null);
                } else {
                    keyCode = Integer.parseInt(raw);
                }

                keys.add(keyCode);
            } catch (Exception e) {
                Log.warning("Setting has invalid keyvent for \"" + varname + "\": ", e.toString());
            }
        }

        this.keyCache.put(varname, keys);
        return keys.isEmpty() ? Optional.empty() : Optional.of(keys);
    }

    public Optional<Integer> getKey(String varname) {
        if(!this.values.containsKey(varname)) {
            return Optional.empty();
        }

        if (this.keyCache.containsKey(varname)) {
            List<Integer> cachedKeys = this.keyCache.get(varname);
            return cachedKeys.isEmpty() ? Optional.empty() : Optional.of(cachedKeys.get(0));
        }

        Optional<List<Integer>> keys = getKeys(varname);
        this.keyCache.put(varname, keys.orElse(new ArrayList<>()));
        if (keys.isEmpty()) {
            return Optional.empty();
        }
        return keys.get().isEmpty() ? Optional.empty() : Optional.of(keys.get().get(0));
    }

    public Optional<Integer> getInt(String varname) {
        if (!this.values.containsKey(varname)) {
            return Optional.empty();
        }

        try {
            return Optional.of(Integer.parseInt(this.values.get(varname).toString().trim()));
        } catch (NumberFormatException e) {
            Log.warning("Settings has invalid integer value for \"" + varname + "\": ", e.toString());
            return Optional.empty();
        }
    }

    public Optional<Boolean> getBoolean(String varname) {
        if (!this.values.containsKey(varname)) {
            return Optional.empty();
        }

        String value = this.values.get(varname).toString().trim().toLowerCase();

        if (value.equals("true") || value.equals("false")) {
            return Optional.of(Boolean.parseBoolean(value));
        }

        Log.warning("Settings has invalid boolean value for \"" + varname + "\"");
        return Optional.empty();
    }

    public Optional<String> getString(String varname) {
        if (!this.values.containsKey(varname)) {
            return Optional.empty();
        }

        return Optional.of(this.values.get(varname).toString());
    }
}
