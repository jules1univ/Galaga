package galaga.gscript.parser.statement;

public class ContinueStatement extends Statement {

    public ContinueStatement() {
        super();
    }

    @Override
    public String format() {
        return "break;\n";
    }

}
