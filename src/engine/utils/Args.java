package engine.utils;

import java.util.HashMap;
import java.util.Map;

public final class Args {

    private final Map<String, String> args = new HashMap<>();

    public Args(String[] cmdArgs) {
        for (int i = 0; i < cmdArgs.length; i++) {
            String arg = cmdArgs[i];

            if (arg.startsWith("--")) {
                String[] parts = arg.split("=");
                if (parts.length == 0) {
                    continue;
                }

                String key = parts[0].substring(2);
                String value = parts.length > 1 ? parts[1] : "true";
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
