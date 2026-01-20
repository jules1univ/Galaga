package galaga.gscript.semantic;

import java.util.HashMap;
import java.util.Map;

import galaga.gscript.ast.statement.StatementBase;

public final class Scope {
    private final Scope parent;
    private final Map<String, StatementBase> variables = new HashMap<>();

    public Scope(Scope parent) {
        this.parent = parent;
    }

    public void addVariable(String name, StatementBase variable) {
        variables.put(name, variable);
    }

    public StatementBase resolveVariable(String name) {
        StatementBase variable = variables.get(name);
        if (variable != null) {
            return variable;
        } else if (parent != null) {
            return parent.resolveVariable(name);
        } else {
            return null;
        }
    }

    public boolean hasVariable(String name) {
        return resolveVariable(name) != null;
    }
}
