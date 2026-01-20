package galaga.gscript.semantic;

import java.util.List;

import galaga.gscript.ast.Program;

public final class SemanticAnalyzer {
    private final AnalyzerContext context;

    public static SemanticAnalyzer of(List<Program> programs) {
        return new SemanticAnalyzer(AnalyzerContext.of(programs));
    }

    private SemanticAnalyzer(AnalyzerContext context) {
        this.context = context;
    }

    public void analyze() {
        
    }
}
