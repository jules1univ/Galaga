package galaga.gscript.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import galaga.gscript.ast.Program;
import galaga.gscript.util.Message;

public class AnalyzerContext {
    private final List<Message> messages = new ArrayList<>();
    private final Map<String, Program> programs;

    public static AnalyzerContext of(Map<String, Program> programs) {
        return new AnalyzerContext(programs);
    }

    private AnalyzerContext(Map<String, Program> programs) {
        this.programs = programs;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Map<String, Program> getPrograms() {
        return programs;
    }

}
