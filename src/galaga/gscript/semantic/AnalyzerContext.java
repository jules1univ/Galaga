package galaga.gscript.semantic;

import galaga.gscript.ast.Program;

public class AnalyzerContext {
    private final Program program;

    public static AnalyzerContext of(Program program) {
        return new AnalyzerContext(program);
    }

    private AnalyzerContext(Program program) {
        this.program = program;
    }

 
}
