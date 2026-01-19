package galaga.gscript.semantic;

import galaga.gscript.ast.Program;

public final class SemanticAnalyzer {
    private final AnalyzerContext context;

    public static SemanticAnalyzer of(Program program) {
        return new SemanticAnalyzer(AnalyzerContext.of(program));
    }

    private SemanticAnalyzer(AnalyzerContext context) {
        this.context = context;
    }

    public void analyze() {
        
    }
}
