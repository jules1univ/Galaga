package engine.utils.ini;

import engine.utils.logger.Log;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Ini {

    private final Map<String, IniSection> sections = new LinkedHashMap<>();

    public static Ini empty() {
        return new Ini();
    }

    public static Ini load(String path) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                lines.add(line);
            }
            return load(lines);
        } catch (IOException e) {
            Log.error("Ini loading failed: %s", e.getMessage());
            return null;
        }
    }

    public static Ini load(List<String> lines) {
        Ini ini = new Ini();

        IniSection current = null;
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty()) {
                continue;
            }

            boolean quoted = false;
            for (int i = 0; i < raw.length(); i++) {
                char c = raw.charAt(i);

                if (c == '"') {
                    quoted = !quoted;
                }

                if (!quoted && (c == ';' || c == '#')) {
                    line = raw.substring(0, i).trim();
                    break;
                }
            }

            if (line.startsWith("[") && line.endsWith("]")) {
                String name = line.substring(1, line.length() - 1).trim().toLowerCase();
                current = ini.sections.computeIfAbsent(name, sec -> new IniSection(name));
                continue;
            }

            int eq = line.indexOf('=');
            if (eq == -1) {
                continue;
            }

            String key = line.substring(0, eq).trim().toLowerCase();
            String value = line.substring(eq + 1).trim();
            if (current == null) {
                continue;
            }
            current.set(key, IniValue.of(value));
        }

        Log.message("Ini loaded successfully.");
        return ini;
    }

    private Ini() {
    }

    public void write(OutputStream out) {
        try (Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
            writer.write(this.toString());
            writer.flush();
            Log.message("Ini saved successfully.");
        } catch (IOException e) {
            Log.error("Ini failed to save: %s", e.getMessage());
        }
    }

    public IniSection addSection(String name) {
        IniSection section = new IniSection(name);
        this.sections.put(name.toLowerCase(), section);
        return section;
    }

    public IniSection addSection(IniSection section) {
        this.sections.put(section.getName().toLowerCase(), section);
        return section;
    }

    public IniSection getSection(String name) {
        return this.sections.get(name.toLowerCase());
    }

    public IniValue getVariable(String section, String name) {
        if(this.sections.containsKey(section)) {
            return this.sections.get(section).get(name);
        }
        return IniValue.of("");
    }

    public boolean hasSection(String name) {
        return this.sections.containsKey(name);
    }

    public boolean hasVariable(String section, String name) {
        if(this.sections.containsKey(section)) {
            return this.sections.get(section).has(name);
        }
        return false;
    }

    @Override
    public String toString() {
        String output = "";
        for (IniSection section : this.sections.values()) {
            output += section.toString() + "\n";
        }
        return output;
    }
}
