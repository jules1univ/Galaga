package engine.utils.ini;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Ini {

    private final Map<String, Map<String, IniVariable>> sections = new HashMap<>();

    public static Ini create(List<String> lines) {
        Ini ini = new Ini();

        String currentSection = "";
        ini.sections.put(currentSection, new HashMap<>());
        for (String rawLine : lines) {
            String line = rawLine.trim();

            if (line.isEmpty() || line.startsWith("#") || line.startsWith(";")) {
                continue;
            }

            if (line.startsWith("[") && line.endsWith("]")) {
                currentSection = line.substring(1, line.length() - 1).trim().toLowerCase();
                ini.sections.putIfAbsent(currentSection, new HashMap<>());
                continue;
            }

            int eq = line.indexOf('=');
            if (eq == -1) {
                continue; 
            }

            String key = line.substring(0, eq).trim().toLowerCase();
            String value = line.substring(eq + 1).trim();

            ini.sections
                .computeIfAbsent(currentSection, s -> new HashMap<>())
                .put(key, new IniVariable(value));
        }

        return ini;
    }

    private Ini() {
    }

    public Map<String, IniVariable> getSection(String name) {
        return sections.get(name.toLowerCase());
    }

    public IniVariable getVariable(String section, String name) {
        Map<String, IniVariable> vars = sections.get(section.toLowerCase());
        if (vars == null) {
            return null;
        }
        return vars.get(name.toLowerCase());
    }

    public boolean containsSection(String name) {
        return sections.containsKey(name);
    }

    public boolean containsVariable(String section, String name) {
        Map<String, IniVariable> vars = sections.get(section);
        return vars != null && vars.containsKey(name);
    }
}
