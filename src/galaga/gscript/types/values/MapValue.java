package galaga.gscript.types.values;

import java.util.Map;

public record MapValue(Map<Value, Value> value) implements Value {

    
    @Override
    public Map<Value, Value> getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int i = 0;
        for (Map.Entry<Value, Value> entry : value.entrySet()) {
            sb.append(entry.getKey().toString());
            sb.append(": ");
            sb.append(entry.getValue().toString());
            if (i < value.size() - 1) {
                sb.append(", ");
            }
            i++;
        }
        sb.append("}");
        return sb.toString();
    }
    
}
