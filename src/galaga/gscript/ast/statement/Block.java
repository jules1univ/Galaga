package galaga.gscript.ast.statement;

import java.util.List;

public record Block(List<StatementBase> statements) implements StatementBase {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for (var statement : statements) {
            sb.append("    ");
            sb.append(statement.format());
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
    
}
