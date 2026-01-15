package galaga.gscript.ast.statement.logic;

import galaga.gscript.ast.statement.Statement;

public record ContinueStatement() implements Statement {

    @Override
    public String format() {
        return "continue;";
    }
    
}
