package galaga.gscript.interpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import galaga.gscript.types.values.Value;

public final class Scope {
    private final Scope parent;
    private final Map<String, Value> variables = new HashMap<>();
    private final Map<String, Value> constants = new HashMap<>();

    public Scope(Scope parent) {
        this.parent = parent;
    }

    public Scope getParent() {
        return parent;
    }

    public void setVariable(String name, Value value) {
        if(variables.containsKey(name)) {
            variables.put(name, value);
        } else if (parent != null && parent.hasVariable(name)) {
            parent.setVariable(name, value);
        } else {
            variables.put(name, value);
        }
    }

    public void defineConstant(String name, Value value) {
        constants.put(name, value);
    }

    public Optional<Value> getVariable(String name) {
        if (variables.containsKey(name)) {
            return Optional.of(variables.get(name));
        } else if (parent != null) {
            return parent.getVariable(name);
        } else {
            return Optional.empty();
        }
    }

    public Optional<Value> getConstant(String name) {
        if (constants.containsKey(name)) {
            return Optional.of(constants.get(name));
        } else if (parent != null) {
            return parent.getConstant(name);
        } else {
            return Optional.empty();
        }
    }

    public boolean hasVariable(String name) {
        if (variables.containsKey(name)) {
            return true;
        } else if (parent != null) {
            return parent.hasVariable(name);
        } else {
            return false;
        }
    }

    public boolean hasConstant(String name) {
        if (constants.containsKey(name)) {
            return true;
        } else if (parent != null) {
            return parent.hasConstant(name);
        } else {
            return false;
        }
    }

}
