package galaga.gscript.ast.statement.logic;

import galaga.gscript.ast.statement.StatementBase;

public record BreakStatement() implements StatementBase {

    @Override
    public String format() {
        return "break;";
    }
    
}
