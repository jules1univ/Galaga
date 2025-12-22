package engine.utils;

import java.util.HashMap;
import java.util.Map;

public final class Args {

    private final Map<String, String> args = new HashMap<>();

    public Args(String[] cmdArgs) {
        for (int i = 0; i < cmdArgs.length; i++) {
            String arg = cmdArgs[i];
            if (arg.startsWith("--")) {
                String key = arg.substring(2);
                String value = (i + 1 < cmdArgs.length && !cmdArgs[i + 1].startsWith("--"))
                        ? cmdArgs[++i]
                        : "true";

                this.args.put(key, value);
            }
        }
    }

    public int getInt(String key, int def) {
        return this.args.containsKey(key) ? Integer.parseInt(args.get(key)) : def;
    }

    public boolean getBool(String key) {
        return Boolean.parseBoolean(this.args.getOrDefault(key, "false"));
    }

    public String get(String key, String def) {
        return this.args.getOrDefault(key, def);
    }
}
