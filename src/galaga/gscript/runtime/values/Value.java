package galaga.gscript.runtime.values;

public sealed interface Value permits NullValue, BooleanValue, IntegerValue, FloatValue, StringValue , ListValue, MapValue, FunctionValue {
    public <T> T getValue();
}
