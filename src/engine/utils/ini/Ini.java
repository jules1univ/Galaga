package engine.utils.ini;

import engine.utils.logger.Log;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Ini {

    private final Map<String, Map<String, IniVariable>> sections = new HashMap<>();

    public static Ini load(List<String> lines) {
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

        Log.message("Ini loaded successfully.");
        return ini;
    }

    private Ini() {
    }

    public void write(OutputStream out) {
        try (Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {

            for (Map.Entry<String, Map<String, IniVariable>> sectionEntry : sections.entrySet()) {
                String sectionName = sectionEntry.getKey();
                Map<String, IniVariable> vars = sectionEntry.getValue();

                if (!sectionName.isEmpty()) {
                    writer.write("[" + sectionName + "]\n");
                }

                for (Map.Entry<String, IniVariable> varEntry : vars.entrySet()) {
                    writer.write(varEntry.getKey());
                    writer.write(" = ");
                    writer.write(varEntry.getValue().toString());
                    writer.write("\n");
                }

                writer.write("\n");
            }

            writer.flush();
            Log.message("Ini saved successfully.");
        } catch (IOException e) {
            Log.error("Ini failed to save: %s", e.getMessage());
        }
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
