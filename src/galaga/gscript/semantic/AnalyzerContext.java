package galaga.gscript.semantic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import galaga.gscript.ast.Program;

public class AnalyzerContext {
    private final List<Program> programs;
    private final Map<String, Program> programMap = new HashMap<>();
    private Program current;

    public static AnalyzerContext of(List<Program> programs) {
        return new AnalyzerContext(programs);
    }

    private AnalyzerContext(List<Program> programs) {
        this.programs = programs;
        this.current = programs.get(0);
    }

 
}
