package galaga.gscript.ast.statement.logic;

import galaga.gscript.ast.statement.Statement;

public record BreakStatement() implements Statement {

    @Override
    public String format() {
        return "break;";
    }
    
}
