package galaga.gscript.ast.statement.logic;

import galaga.gscript.ast.statement.StatementBase;

public record ContinueStatement() implements StatementBase {

    @Override
    public String format() {
        return "continue;";
    }
    
}
