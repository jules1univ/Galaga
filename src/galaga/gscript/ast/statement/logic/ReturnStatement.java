package galaga.gscript.ast.statement.logic;

import java.util.Optional;

import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.statement.StatementBase;

public record ReturnStatement(Optional<ExpressionBase> body) implements StatementBase {

    @Override
    public String format() {
        if (body.isPresent()) {
            return "return " + body.get().format() + ";";
        }
        return "return;";
    }

}
