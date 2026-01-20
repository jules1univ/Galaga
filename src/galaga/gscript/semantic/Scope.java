package galaga.gscript.semantic;

import java.util.HashMap;
import java.util.Map;

import galaga.gscript.ast.statement.AssignStatement;

public final class Scope {
    private final Scope parent;
    private final Map<String, AssignStatement> variables = new HashMap<>();

    public Scope(Scope parent) {
        this.parent = parent;
    }

    public void addVariable(AssignStatement variable) {
        variables.put(variable.name(), variable);
    }

    public AssignStatement resolveVariable(String name) {
        AssignStatement variable = variables.get(name);
        if (variable != null) {
            return variable;
        } else if (parent != null) {
            return parent.resolveVariable(name);
        } else {
            return null;
        }
    }
}
