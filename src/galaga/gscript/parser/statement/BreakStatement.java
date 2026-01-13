package galaga.gscript.parser.statement;

public class BreakStatement extends Statement {

    public BreakStatement() {
        super();
    }

    @Override
    public String format() {
        return "break;\n";
    }
    
}
