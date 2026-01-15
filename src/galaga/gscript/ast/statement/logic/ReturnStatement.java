package galaga.gscript.ast.statement.logic;

import java.util.Optional;

import galaga.gscript.ast.expression.Expression;
import galaga.gscript.ast.statement.Statement;

public record ReturnStatement(Optional<Expression> body) implements Statement {

    @Override
    public String format() {
        if (body.isPresent()) {
            return "return " + body.get().format() + ";";
        }
        return "return;";
    }

}
