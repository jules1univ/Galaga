package galaga.gscript.semantic;

import java.util.HashMap;
import java.util.Map;

import galaga.gscript.ast.statement.VariableStatement;

public final class Scope {
    private final Scope parent;
    private final Map<String, VariableStatement> variables = new HashMap<>();

    public Scope(Scope parent) {
        this.parent = parent;
    }

    public void addVariable(VariableStatement variable) {
        variables.put(variable.name(), variable);
    }

    public VariableStatement resolveVariable(String name) {
        VariableStatement variable = variables.get(name);
        if (variable != null) {
            return variable;
        } else if (parent != null) {
            return parent.resolveVariable(name);
        } else {
            return null;
        }
    }
}
