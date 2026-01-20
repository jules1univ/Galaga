package galaga.gscript.semantic;

import java.util.List;
import java.util.Map;

import galaga.gscript.ast.Program;
import galaga.gscript.util.Message;

public final class SemanticAnalyzer {
    private final AnalyzerContext context;

    public static SemanticAnalyzer of(Map<String, Program> programs) {
        return new SemanticAnalyzer(AnalyzerContext.of(programs));
    }

    private SemanticAnalyzer(AnalyzerContext context) {
        this.context = context;
    }

    public void analyze() {
        
    }

    public List<Message> getMessages() {
        return context.getMessages();
    }
}
